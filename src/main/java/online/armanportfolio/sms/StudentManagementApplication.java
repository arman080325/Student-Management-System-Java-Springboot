package online.armanportfolio.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Student Management System.
 *
 * A Spring Boot rewrite of the original Java Swing + Oracle desktop app —
 * now a deployable REST API + web dashboard backed by PostgreSQL.
 */
@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }
}
