package ru.hogwarts.school.config;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.CompositeUriComponentsContributor;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(CompositeUriComponentsContributor components) {
        return new OpenAPI()
                .info(new Info()
                        .title("Hogwarts School API")
                        .version("1.0.0")
                        .description("API для усправления студентами и факультетами школы Хогвартс")
                        .contact(new Contact()
                                .name("Hogwarts School")
                                .email("hogwarts@example.com")));
                .components(new io.swagger.v3.oas.models.Components()
                .addRequestBodies("multipartFile", new RequestBody()
                        .content(new Content()
                                .addMediaType(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE, new MediaType().schema(new io.swagger.v3.oas.models.media.Schema<>()
                                        .type("object")
                                        .properties(new java.util.HashMap<>() {{
                                            put("file", new io.swagger.v3.oas.models.media.Schema<>()
                                                    .type("string")
                                                    .format("binary"));
                                        }}))))));

    }
}
