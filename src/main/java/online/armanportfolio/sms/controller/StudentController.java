package online.armanportfolio.sms.controller;

import jakarta.validation.Valid;
import online.armanportfolio.sms.dto.PagedResponse;
import online.armanportfolio.sms.dto.StudentRequest;
import online.armanportfolio.sms.dto.StudentResponse;
import online.armanportfolio.sms.dto.StudentStatsResponse;
import online.armanportfolio.sms.service.StudentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;
    private final boolean allowClearAll;

    public StudentController(StudentService service,
                            @Value("${app.allow-clear-all:true}") boolean allowClearAll) {
        this.service = service;
        this.allowClearAll = allowClearAll;
    }

    @GetMapping
    public PagedResponse<StudentResponse> list(
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return service.findAll(field, search, page, size, sortBy, sortDir);
    }

    @GetMapping("/count")
    public Map<String, Long> count() {
        return Map.of("count", service.count());
    }

    @GetMapping("/stats")
    public StudentStatsResponse stats() {
        return service.stats();
    }

    @GetMapping(value = "/export")
    public ResponseEntity<byte[]> export() {
        byte[] csv = service.exportCsv().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/{id}")
    public StudentResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, Object>> deleteBulk(@RequestBody List<Long> ids) {
        int deleted = service.deleteBulk(ids);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        if (!allowClearAll) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Clear all is disabled in this environment");
        }
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
