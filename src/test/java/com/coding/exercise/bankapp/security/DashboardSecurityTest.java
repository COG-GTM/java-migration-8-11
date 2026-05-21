package com.coding.exercise.bankapp.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for security configuration on dashboard endpoints.
 * Covers MBA-1799.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private CustomerAccountXRefRepository custAccXRefRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private BankingServiceImpl bankingService;

    @MockBean
    private BankingServiceHelper bankingServiceHelper;

    @BeforeEach
    void setupMocks() {
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{5001L, "CHECKING", "Active", 1000.0});
        when(accountRepository.findAllAccountSummaries()).thenReturn(rows);
        when(customerRepository.findAllCustomerNames()).thenReturn(Collections.emptyList());
        when(transactionRepository.findByAccountNumber(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("Authenticated user can access dashboard (200)")
    void authenticatedUserCanAccessDashboard() throws Exception {
        mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unauthenticated GET /dashboard returns 401")
    void unauthenticatedGetDashboardReturns401() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Unauthenticated POST /dashboard/transfer returns 401")
    void unauthenticatedPostTransferReturns401() throws Exception {
        mockMvc.perform(post("/dashboard/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 100.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Wrong credentials rejected (401)")
    void wrongCredentialsRejected() throws Exception {
        mockMvc.perform(get("/dashboard").with(httpBasic("wrong", "credentials")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Swagger UI accessible without authentication")
    void swaggerAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Static CSS resources accessible without auth")
    void staticCssAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/css/dashboard.css"))
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    assertTrue(s == 200 || s == 404,
                            "Static resource should be accessible or not found (not 401), got: " + s);
                });
    }

    @Test
    @DisplayName("Root path accessible without auth")
    void rootPathAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    assertTrue(s >= 200 && s < 400,
                            "Root should be accessible without auth, got: " + s);
                });
    }
}
