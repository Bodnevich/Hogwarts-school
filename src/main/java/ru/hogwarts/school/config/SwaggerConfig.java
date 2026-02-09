package ru.hogwarts.school.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hogwarts School API")
                        .version("1.0.0")
                        .description("API для управления студентами, факультетами и аватарами школы Хогвартс")
                        .contact(new Contact()
                                .name("Hogwarts School")
                                .email("hogwarts@example.com")));
    }
}