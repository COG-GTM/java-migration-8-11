package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "BANKING APPLICATION REST API",
        description = "API for Banking Application.",
        version = "1.0.0"
    )
)
public class ApplicationConfig {

}
