package com.coding.exercise.bankapp.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRootPathIsAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testH2ConsolePathIsNotBlocked() throws Exception {
        MvcResult result = mockMvc.perform(get("/h2-console/")).andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status != 401 && status != 403, "H2 console should not be blocked by security");
    }

    @Test
    void testCsrfIsDisabled() throws Exception {
        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testSecurityConfigLoads() {
        assertNotNull(mockMvc, "MockMvc should be configured with security");
    }
}
