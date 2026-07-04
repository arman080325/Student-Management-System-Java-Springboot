package online.armanportfolio.sms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

/**
 * A student record.
 *
 * Key improvements over the original desktop schema:
 *  - Surrogate {@code id} primary key (auto-increment) instead of a random string roll number.
 *  - {@code rollNo} is a unique, server-generated business key (no collisions).
 *  - Real column types: {@link LocalDate} for DOB, numeric percentage for Class XII.
 *  - Audit timestamps ({@code createdAt} / {@code updatedAt}).
 *  - The Aadhaar field from the desktop version is intentionally dropped — storing a
 *    government identity number in a public demo database is an unnecessary PII exposure.
 */
@Entity
@Table(
        name = "students",
        indexes = {
                @Index(name = "idx_student_roll_no", columnList = "rollNo", unique = true)
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @SequenceGenerator(name = "student_seq", sequenceName = "student_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String rollNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String fatherName;

    private LocalDate dob;

    @Column(length = 250)
    private String address;

    @Column(length = 15)
    private String phone;

    @Column(length = 120)
    private String email;

    /** Student Identification Code (institution-specific). */
    @Column(length = 20)
    private String sic;

    /** Class XII aggregate percentage (0–100). */
    private Double classXiiPercent;

    @Column(length = 50)
    private String course;

    @Column(length = 100)
    private String branch;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /** Derive a unique, human-readable roll number from the sequence-generated id. */
    @PrePersist
    private void assignRollNo() {
        if (rollNo == null && id != null) {
            rollNo = String.format("14%06d", id);
        }
    }
}
