package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class ApplicationConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
        		.apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build();
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("BANKING APPLICATION REST API")
        		.description("API for Banking Application.")
                .version("1.0.0").build();
    }
}
