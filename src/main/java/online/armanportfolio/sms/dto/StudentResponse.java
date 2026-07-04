package online.armanportfolio.sms.dto;

import online.armanportfolio.sms.model.Student;

import java.time.LocalDate;

/**
 * What the API returns for a student. Keeps the entity out of the web layer.
 */
public record StudentResponse(
        Long id,
        String rollNo,
        String name,
        String fatherName,
        LocalDate dob,
        String address,
        String phone,
        String email,
        String sic,
        Double classXiiPercent,
        String course,
        String branch
) {
    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getRollNo(),
                s.getName(),
                s.getFatherName(),
                s.getDob(),
                s.getAddress(),
                s.getPhone(),
                s.getEmail(),
                s.getSic(),
                s.getClassXiiPercent(),
                s.getCourse(),
                s.getBranch()
        );
    }
}
