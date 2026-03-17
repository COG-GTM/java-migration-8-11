package com.coding.exercise.bankapp.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootPath_shouldBePermitted() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void csrfDisabled_shouldNotReturn403() throws Exception {
        // POST to a non-existent endpoint; if CSRF were enabled we'd get 403,
        // but since it's disabled we get 404 instead
        mockMvc.perform(post("/nonexistent")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void customerEndpoint_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk());
    }
}
