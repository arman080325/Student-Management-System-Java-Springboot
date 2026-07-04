package online.armanportfolio.sms.repository;

import online.armanportfolio.sms.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByRollNo(String rollNo);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Student> findByRollNoContainingIgnoreCase(String rollNo, Pageable pageable);

    Page<Student> findByCourseContainingIgnoreCase(String course, Pageable pageable);

    Page<Student> findByBranchContainingIgnoreCase(String branch, Pageable pageable);

    /** Unpaginated variant used by CSV export and dashboard analytics. */
    List<Student> findAll(Sort sort);
}
