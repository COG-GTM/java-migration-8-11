package com.coding.exercise.bankapp.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class ApplicationConfigTest {

    @Test
    void testCustomOpenAPI() {
        ApplicationConfig config = new ApplicationConfig();
        
        OpenAPI openAPI = config.customOpenAPI();
        
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("BANKING APPLICATION REST API", openAPI.getInfo().getTitle());
        assertEquals("API for Banking Application.", openAPI.getInfo().getDescription());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
    }
}
