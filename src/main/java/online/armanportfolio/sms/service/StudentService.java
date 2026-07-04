package online.armanportfolio.sms.service;

import online.armanportfolio.sms.dto.PagedResponse;
import online.armanportfolio.sms.dto.StudentRequest;
import online.armanportfolio.sms.dto.StudentResponse;
import online.armanportfolio.sms.dto.StudentStatsResponse;
import online.armanportfolio.sms.exception.StudentNotFoundException;
import online.armanportfolio.sms.model.Student;
import online.armanportfolio.sms.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {

    /** Whitelisted sort keys — never pass a raw client string straight into Sort.by(). */
    private static final Set<String> SORTABLE_FIELDS =
            Set.of("rollNo", "name", "dob", "classXiiPercent", "course", "branch", "createdAt");

    private static final int MAX_PAGE_SIZE = 100;

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    /**
     * Paginated, sortable, optionally-filtered listing — backs the main records table.
     * Field is whitelisted, so no dynamic column names ever reach the query.
     */
    @Transactional(readOnly = true)
    public PagedResponse<StudentResponse> findAll(String field, String search, int page, int size,
                                                   String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);

        Page<Student> result;
        if (search != null && !search.isBlank()) {
            String q = search.trim();
            String f = field == null ? "name" : field.trim().toLowerCase();
            result = switch (f) {
                case "rollno", "roll no", "roll_no" -> repository.findByRollNoContainingIgnoreCase(q, pageable);
                case "course" -> repository.findByCourseContainingIgnoreCase(q, pageable);
                case "branch" -> repository.findByBranchContainingIgnoreCase(q, pageable);
                default -> repository.findByNameContainingIgnoreCase(q, pageable);
            };
        } else {
            result = repository.findAll(pageable);
        }
        return PagedResponse.from(result.map(StudentResponse::from));
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return StudentResponse.from(getOrThrow(id));
    }

    @Transactional
    public StudentResponse create(StudentRequest req) {
        Student student = new Student();
        apply(student, req);
        // Roll number is assigned in Student#assignRollNo() from the sequence id.
        return StudentResponse.from(repository.save(student));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest req) {
        Student student = getOrThrow(id);
        apply(student, req);
        return StudentResponse.from(repository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /** Deletes only the ids that actually exist; silently ignores stale/unknown ones. */
    @Transactional
    public int deleteBulk(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<Student> existing = repository.findAllById(ids);
        repository.deleteAll(existing);
        return existing.size();
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    /** Aggregate figures for the dashboard: totals, averages, and breakdowns. */
    @Transactional(readOnly = true)
    public StudentStatsResponse stats() {
        List<Student> all = repository.findAll(Sort.by(Sort.Direction.DESC, "classXiiPercent"));

        double average = all.stream()
                .map(Student::getClassXiiPercent)
                .filter(p -> p != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        Map<String, Long> byCourse = all.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCourse() == null || s.getCourse().isBlank() ? "Unspecified" : s.getCourse(),
                        LinkedHashMap::new, Collectors.counting()));

        Map<String, Long> byBranch = all.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getBranch() == null || s.getBranch().isBlank() ? "Unspecified" : s.getBranch(),
                        LinkedHashMap::new, Collectors.counting()));

        StudentResponse top = all.stream()
                .filter(s -> s.getClassXiiPercent() != null)
                .max(Comparator.comparingDouble(Student::getClassXiiPercent))
                .map(StudentResponse::from)
                .orElse(null);

        return new StudentStatsResponse(
                all.size(),
                byCourse.size(),
                byBranch.size(),
                Math.round(average * 100.0) / 100.0,
                byCourse,
                byBranch,
                top
        );
    }

    /** Renders every record as RFC-4180-ish CSV for the "Export" action. */
    @Transactional(readOnly = true)
    public String exportCsv() {
        List<Student> all = repository.findAll(Sort.by(Sort.Direction.ASC, "rollNo"));
        StringBuilder sb = new StringBuilder();
        sb.append("Roll No,Name,Father's Name,DOB,Address,Phone,Email,SIC,Class XII %,Course,Branch\n");
        for (Student s : all) {
            sb.append(csv(s.getRollNo())).append(',')
              .append(csv(s.getName())).append(',')
              .append(csv(s.getFatherName())).append(',')
              .append(csv(s.getDob() == null ? "" : s.getDob().toString())).append(',')
              .append(csv(s.getAddress())).append(',')
              .append(csv(s.getPhone())).append(',')
              .append(csv(s.getEmail())).append(',')
              .append(csv(s.getSic())).append(',')
              .append(s.getClassXiiPercent() == null ? "" : s.getClassXiiPercent()).append(',')
              .append(csv(s.getCourse())).append(',')
              .append(csv(s.getBranch())).append('\n');
        }
        return sb.toString();
    }

    // --- helpers ---------------------------------------------------------

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        String field = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, Sort.by(direction, field));
    }

    private Student getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    private void apply(Student s, StudentRequest req) {
        s.setName(req.name());
        s.setFatherName(req.fatherName());
        s.setDob(req.dob());
        s.setAddress(req.address());
        s.setPhone(req.phone());
        s.setEmail(req.email());
        s.setSic(req.sic());
        s.setClassXiiPercent(req.classXiiPercent());
        s.setCourse(req.course());
        s.setBranch(req.branch());
    }

    private String csv(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
