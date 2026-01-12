package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Phase 3: Migrated from Springfox Swagger to SpringDoc OpenAPI
 * Swagger UI now available at /swagger-ui/index.html
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public OpenAPI bankingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BANKING APPLICATION REST API")
                        .description("API for Banking Application.")
                        .version("1.0.0"));
    }
}
