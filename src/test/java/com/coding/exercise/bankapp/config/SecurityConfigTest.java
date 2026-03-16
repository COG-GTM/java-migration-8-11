package com.coding.exercise.bankapp.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void rootPath_permitAll_doesNotReturn401() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertNotEquals(401, status, "Root path should be accessible without auth (permitAll)");
                });
    }

    @Test
    void h2Console_permitAll_doesNotReturn401() throws Exception {
        mockMvc.perform(get("/h2-console/"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertNotEquals(401, status, "H2 console should be accessible without auth (permitAll)");
                });
    }

    @Test
    void customersAll_noExplicitAuthRule_doesNotReturn401() throws Exception {
        mockMvc.perform(get("/customers/all"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertNotEquals(401, status,
                            "No anyRequest().authenticated() rule - endpoint is accessible without auth");
                });
    }

    @Test
    void authenticatedRequest_withBasicAuth_returns200() throws Exception {
        mockMvc.perform(get("/customers/all")
                        .header("Authorization", basicAuth("bankapp", "changeit")))
                .andExpect(status().isOk());
    }

    @Test
    void csrfDisabled_postWithoutCsrfToken_succeeds() throws Exception {
        String customerJson = "{\"firstName\":\"SecJ\",\"lastName\":\"Doe\",\"customerNumber\":9901,\"status\":\"ACTIVE\","
                + "\"contactDetails\":{\"emailId\":\"j@d.com\",\"homePhone\":\"111\",\"workPhone\":\"222\"},"
                + "\"customerAddress\":{\"address1\":\"123 St\",\"city\":\"NYC\",\"state\":\"NY\",\"zip\":\"10001\",\"country\":\"US\"}}";

        mockMvc.perform(post("/customers/add")
                        .header("Authorization", basicAuth("bankapp", "changeit"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated());
    }

    @Test
    void csrfDisabled_postWithoutAuth_notBlockedByCsrf() throws Exception {
        String customerJson = "{\"firstName\":\"SecT\",\"lastName\":\"User\",\"customerNumber\":9902,\"status\":\"ACTIVE\","
                + "\"contactDetails\":{\"emailId\":\"t@u.com\",\"homePhone\":\"111\",\"workPhone\":\"222\"},"
                + "\"customerAddress\":{\"address1\":\"456 St\",\"city\":\"LA\",\"state\":\"CA\",\"zip\":\"90001\",\"country\":\"US\"}}";

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertNotEquals(403, status, "CSRF disabled - POST should not return 403");
                });
    }

    @Test
    void frameOptionsDisabled_noXFrameOptionsHeader() throws Exception {
        mockMvc.perform(get("/h2-console/")
                        .header("Authorization", basicAuth("bankapp", "changeit")))
                .andExpect(result -> {
                    String xFrameOptions = result.getResponse().getHeader("X-Frame-Options");
                    assertNull(xFrameOptions, "X-Frame-Options should not be present (frameOptions disabled)");
                });
    }

    private String basicAuth(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
