package online.armanportfolio.sms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Payload for creating or updating a student.
 * All input validation lives here — the desktop version had none.
 */
public record StudentRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Father's name is required")
        @Size(max = 100, message = "Father's name must be at most 100 characters")
        String fatherName,

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,

        @Size(max = 250, message = "Address must be at most 250 characters")
        String address,

        @Pattern(regexp = "^$|^[0-9]{10}$", message = "Phone must be 10 digits")
        String phone,

        @Email(message = "Email must be valid")
        @Size(max = 120, message = "Email must be at most 120 characters")
        String email,

        @Size(max = 20, message = "SIC must be at most 20 characters")
        String sic,

        @DecimalMin(value = "0.0", message = "Class XII % cannot be negative")
        @DecimalMax(value = "100.0", message = "Class XII % cannot exceed 100")
        Double classXiiPercent,

        @NotBlank(message = "Course is required")
        @Size(max = 50)
        String course,

        @NotBlank(message = "Branch is required")
        @Size(max = 100)
        String branch
) {
}
