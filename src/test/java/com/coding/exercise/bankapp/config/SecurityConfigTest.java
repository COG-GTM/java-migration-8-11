package com.coding.exercise.bankapp.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootPath_isAccessibleWithoutAuth() throws Exception {
        // Root path is explicitly permitted - should not return 401
        int statusCode = mockMvc.perform(get("/"))
                .andReturn().getResponse().getStatus();
        // Should be anything other than 401 (likely 404 since no root mapping)
        assert statusCode != 401 : "Root path should not require authentication";
    }

    @Test
    void h2ConsolePath_isExplicitlyPermitted() throws Exception {
        // h2-console path is explicitly permitted in SecurityConfig
        int statusCode = mockMvc.perform(get("/h2-console/"))
                .andReturn().getResponse().getStatus();
        // Should not return 401 (may return 200 or 404 depending on H2 console availability)
        assert statusCode != 401 : "H2 console path should not require authentication";
    }

    @Test
    void csrfIsDisabled() throws Exception {
        // CSRF is disabled in SecurityConfig - POST without CSRF token should not return 403
        // Use a non-existent path to avoid triggering actual controller logic
        int statusCode = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/nonexistent-path")
                                .contentType("application/json")
                                .content("{}"))
                .andReturn().getResponse().getStatus();
        // Should not be 403 Forbidden (CSRF rejection) - would be 404 since path doesn't exist
        assert statusCode != 403 : "CSRF should be disabled";
    }

    @Test
    void customersEndpoint_accessibleWithoutExplicitAuth() throws Exception {
        // SecurityConfig permits / and /h2-console/** but does not call anyRequest().authenticated()
        // so API endpoints are accessible without credentials
        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk());
    }

    @Test
    void customersEndpoint_validCredentials_grantsAccess() throws Exception {
        mockMvc.perform(get("/customers/all")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("bankapp", "changeit")))
                .andExpect(status().isOk());
    }

    @Test
    void securityConfigLoads() throws Exception {
        // Verify that SecurityConfig is loaded and the security filter chain is active
        // by confirming that requests are processed (not rejected by misconfiguration)
        mockMvc.perform(get("/customers/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
}
