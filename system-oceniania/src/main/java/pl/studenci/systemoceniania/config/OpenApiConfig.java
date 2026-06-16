package pl.studenci.systemoceniania.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("System Oceniania - REST API")
                        .version("1.0")
                        .description("API do zarządzania ocenami, studentami, przedmiotami itp.")
                        .contact(new Contact()
                                .name("Twoje imię")
                                .email("email@example.com")));
    }
}