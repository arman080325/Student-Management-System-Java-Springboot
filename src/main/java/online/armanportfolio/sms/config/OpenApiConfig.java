package online.armanportfolio.sms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentManagementOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Student Management System API")
                .description("REST API for managing student records — Silicon University.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Arman Ahemad Khan")
                        .url("https://arman-portfolio.online"))
                .license(new License().name("MIT")));
    }
}
