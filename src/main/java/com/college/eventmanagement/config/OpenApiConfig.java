package com.college.eventmanagement.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("Event Management API").description("API for Events, Bookings, Feedback").version("v1"))
                .externalDocs(new ExternalDocumentation().description("Swagger UI").url("/swagger-ui"));
    }
}
