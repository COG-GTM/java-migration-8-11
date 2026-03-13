package com.coding.exercise.bankapp.config;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads_mockMvcAvailable() {
        assertNotNull(mockMvc);
    }

    @Test
    void customersEndpoint_unauthenticated_isAccessible() throws Exception {
        // SecurityConfig does not enforce authentication on API endpoints;
        // it only explicitly permits "/" and "/h2-console/**" and disables CSRF.
        // Without .anyRequest().authenticated(), all endpoints are open.
        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk());
    }

    @Test
    void accountsEndpoint_unauthenticated_returns404ForNonExistentAccount() throws Exception {
        // Endpoint is accessible without auth (no .anyRequest().authenticated() in config).
        // Returns 404 because account 1001 does not exist in the test DB.
        mockMvc.perform(get("/accounts/1001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void csrfDisabled_postWithoutCsrfToken_doesNotReturn403() throws Exception {
        // POST without CSRF token should NOT return 403 since CSRF is disabled
        String customerJson = "{\"firstName\":\"Test\",\"lastName\":\"User\",\"customerNumber\":9999,\"status\":\"ACTIVE\","
                + "\"contactDetails\":{\"emailId\":\"test@test.com\",\"homePhone\":\"555-1234\",\"workPhone\":\"555-5678\"},"
                + "\"customerAddress\":{\"address1\":\"123 Test St\",\"city\":\"TestCity\",\"state\":\"TS\",\"zip\":\"12345\",\"country\":\"US\"}}";
        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assertNotEquals(403, statusCode, "Expected non-403 status but got 403, CSRF may not be disabled");
                });
    }

    @Test
    void frameOptionsDisabled_forH2Console() throws Exception {
        // Verify frame options header is not set (disabled for H2 console access)
        mockMvc.perform(get("/customers/all"))
                .andExpect(result -> {
                    String frameOptions = result.getResponse().getHeader("X-Frame-Options");
                    assertNull(frameOptions, "Expected X-Frame-Options to be absent but was: " + frameOptions);
                });
    }
}
