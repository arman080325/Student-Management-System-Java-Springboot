package online.armanportfolio.sms.config;

import online.armanportfolio.sms.dto.StudentRequest;
import online.armanportfolio.sms.service.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/**
 * Seeds a few demo records the first time the app boots against an empty database,
 * so the deployed portfolio demo never shows a blank table.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(StudentService service) {
        return args -> {
            if (service.count() > 0) {
                return;
            }
            service.create(new StudentRequest(
                    "Arman Ahemad Khan", "Ahemad Khan",
                    LocalDate.of(2005, 3, 8), "Bhubaneswar, Odisha",
                    "9876543210", "armanxploit@gmail.com", "23BCSE001",
                    88.5, "B.Tech", "Computer Science & Engineering (CSE)"));
            service.create(new StudentRequest(
                    "Anurag Pradhan", "Suresh Pradhan",
                    LocalDate.of(2005, 7, 21), "Cuttack, Odisha",
                    "9812345678", "anurag@example.com", "23BCSE014",
                    85.2, "B.Tech", "Computer Science & Engineering (CSE)"));
            service.create(new StudentRequest(
                    "Priya Mohanty", "Rajesh Mohanty",
                    LocalDate.of(2004, 11, 2), "Bhubaneswar, Odisha",
                    "9765432109", "priya@example.com", "23BECE008",
                    91.0, "B.Tech", "Electronics & Communication Engineering (ECE)"));
            service.create(new StudentRequest(
                    "Rahul Das", "Bikram Das",
                    LocalDate.of(2005, 1, 17), "Rourkela, Odisha",
                    "9700011223", "rahul@example.com", "23BEEE021",
                    79.8, "B.Tech", "Electrical & Electronics Engineering (EEE)"));
        };
    }
}
