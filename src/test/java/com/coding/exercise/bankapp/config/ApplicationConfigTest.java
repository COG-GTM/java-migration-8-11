package com.coding.exercise.bankapp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.swagger.v3.oas.models.OpenAPI;

@SpringBootTest
class ApplicationConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void openApiBeanIsCreated() {
        assertNotNull(openAPI);
    }

    @Test
    void apiTitle_isSetCorrectly() {
        assertEquals("BANKING APPLICATION REST API", openAPI.getInfo().getTitle());
    }

    @Test
    void apiDescription_isSetCorrectly() {
        assertEquals("API for Banking Application.", openAPI.getInfo().getDescription());
    }

    @Test
    void apiVersion_isSetCorrectly() {
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
    }
}
