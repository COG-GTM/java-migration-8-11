package com.coding.exercise.bankapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Component tests for DashboardController endpoints.
 * Covers MBA-1792 (GET /dashboard) and MBA-1793 (POST /dashboard/transfer).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerMvcTest {

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

    private void setupDefaultMocks() {
        List<Object[]> accountRows = Arrays.asList(
                new Object[]{5001L, "CHECKING", "Active", 15750.50},
                new Object[]{5002L, "SAVINGS", "Active", 42300.00});
        when(accountRepository.findAllAccountSummaries()).thenReturn(accountRows);

        List<Object[]> customerNames = new ArrayList<>();
        customerNames.add(new Object[]{"Carlos", "Rodriguez"});
        when(customerRepository.findAllCustomerNames()).thenReturn(customerNames);

        Date txDate = new Date();
        Transaction tx1 = Transaction.builder().accountNumber(5001L).txDateTime(txDate)
                .txType("CREDIT").txAmount(500.0).build();
        when(transactionRepository.findByAccountNumber(5001L))
                .thenReturn(Optional.of(Arrays.asList(tx1)));
        when(transactionRepository.findByAccountNumber(5002L))
                .thenReturn(Optional.empty());

        when(bankingServiceHelper.convertToTransactionDomain(any(Transaction.class)))
                .thenReturn(TransactionDetails.builder().accountNumber(5001L)
                        .txDateTime(txDate).txType("CREDIT").txAmount(500.0).build());
    }

    // ==================== MBA-1792: GET /dashboard ====================

    @Nested
    @DisplayName("MBA-1792: GET /dashboard renders model attributes")
    class GetDashboardTests {

        @Test
        @DisplayName("Dashboard renders with all model attributes populated")
        void dashboardRendersWithAllAttributes() throws Exception {
            setupDefaultMocks();

            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard/index"))
                    .andExpect(model().attributeExists(
                            "accounts", "transactions", "notifications",
                            "totalBalance", "totalAccounts", "totalIncome",
                            "totalExpenses", "customerName", "customerInitials",
                            "notificationCount"));
        }

        @Test
        @DisplayName("Dashboard with no data shows empty state")
        void dashboardWithNoDataShowsEmptyState() throws Exception {
            when(accountRepository.findAllAccountSummaries()).thenReturn(Collections.emptyList());
            when(customerRepository.findAllCustomerNames()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("totalBalance", 0.0))
                    .andExpect(model().attribute("accounts", hasSize(0)))
                    .andExpect(model().attribute("customerName", "Banking User"));
        }

        @Test
        @DisplayName("Total balance correctly sums all account balances")
        void totalBalanceCorrectlySumsAccounts() throws Exception {
            List<Object[]> accountRows = Arrays.asList(
                    new Object[]{1001L, "CHECKING", "Active", 1000.0},
                    new Object[]{1002L, "SAVINGS", "Active", 2000.0},
                    new Object[]{1003L, "CREDIT", "Active", 3000.0});
            when(accountRepository.findAllAccountSummaries()).thenReturn(accountRows);
            when(customerRepository.findAllCustomerNames()).thenReturn(Collections.emptyList());
            when(transactionRepository.findByAccountNumber(any())).thenReturn(Optional.empty());

            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("totalBalance", 6000.0));
        }

        @Test
        @DisplayName("Income and expenses correctly calculated from transactions")
        void incomeAndExpensesCalculated() throws Exception {
            List<Object[]> accountRows = new ArrayList<>();
            accountRows.add(new Object[]{5001L, "CHECKING", "Active", 1000.0});
            when(accountRepository.findAllAccountSummaries()).thenReturn(accountRows);
            when(customerRepository.findAllCustomerNames()).thenReturn(Collections.emptyList());

            Transaction txCredit1 = Transaction.builder().accountNumber(5001L)
                    .txDateTime(new Date()).txType("CREDIT").txAmount(500.0).build();
            Transaction txCredit2 = Transaction.builder().accountNumber(5001L)
                    .txDateTime(new Date()).txType("CREDIT").txAmount(300.0).build();
            Transaction txDebit = Transaction.builder().accountNumber(5001L)
                    .txDateTime(new Date()).txType("DEBIT").txAmount(200.0).build();

            when(transactionRepository.findByAccountNumber(5001L))
                    .thenReturn(Optional.of(Arrays.asList(txCredit1, txCredit2, txDebit)));

            when(bankingServiceHelper.convertToTransactionDomain(txCredit1))
                    .thenReturn(TransactionDetails.builder().accountNumber(5001L)
                            .txDateTime(new Date()).txType("CREDIT").txAmount(500.0).build());
            when(bankingServiceHelper.convertToTransactionDomain(txCredit2))
                    .thenReturn(TransactionDetails.builder().accountNumber(5001L)
                            .txDateTime(new Date()).txType("CREDIT").txAmount(300.0).build());
            when(bankingServiceHelper.convertToTransactionDomain(txDebit))
                    .thenReturn(TransactionDetails.builder().accountNumber(5001L)
                            .txDateTime(new Date()).txType("DEBIT").txAmount(200.0).build());

            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("totalIncome", 800.0))
                    .andExpect(model().attribute("totalExpenses", 200.0));
        }
    }

    // ==================== MBA-1793: POST /dashboard/transfer ====================

    @Nested
    @DisplayName("MBA-1793: POST /dashboard/transfer success path")
    class PostTransferSuccessTests {

        @Test
        @DisplayName("Successful transfer returns success response")
        void successfulTransferReturnsSuccess() throws Exception {
            CustomerAccountXRef xref = CustomerAccountXRef.builder()
                    .accountNumber(5001L).customerNumber(1001L).build();
            when(custAccXRefRepository.findByAccountNumber(5001L))
                    .thenReturn(Optional.of(xref));
            when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                    .thenReturn(ResponseEntity.ok("Transfer successful"));

            mockMvc.perform(post("/dashboard/transfer")
                            .with(httpBasic("bankapp", "changeit"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 100.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }

        @Test
        @DisplayName("Transfer with valid amount returns correct success message")
        void transferWithValidAmountReturnsSuccess() throws Exception {
            CustomerAccountXRef xref = CustomerAccountXRef.builder()
                    .accountNumber(5001L).customerNumber(1001L).build();
            when(custAccXRefRepository.findByAccountNumber(5001L))
                    .thenReturn(Optional.of(xref));
            when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                    .thenReturn(ResponseEntity.ok("Funds transferred"));

            mockMvc.perform(post("/dashboard/transfer")
                            .with(httpBasic("bankapp", "changeit"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 500.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    // ==================== MBA-1784 (from earlier plan): POST /dashboard/transfer failure paths ====================

    @Nested
    @DisplayName("POST /dashboard/transfer failure paths")
    class PostTransferFailureTests {

        @Test
        @DisplayName("Transfer with non-existent source account returns failure")
        void transferNonExistentSourceAccount() throws Exception {
            when(custAccXRefRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/dashboard/transfer")
                            .with(httpBasic("bankapp", "changeit"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 9999, \"toAccountNumber\": 5002, \"transferAmount\": 100.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Account owner not found."));
        }

        @Test
        @DisplayName("Transfer where banking service returns error")
        void transferBankingServiceError() throws Exception {
            CustomerAccountXRef xref = CustomerAccountXRef.builder()
                    .accountNumber(5001L).customerNumber(1001L).build();
            when(custAccXRefRepository.findByAccountNumber(5001L))
                    .thenReturn(Optional.of(xref));
            when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                    .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

            mockMvc.perform(post("/dashboard/transfer")
                            .with(httpBasic("bankapp", "changeit"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 10000.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Transfer with null body in banking service response")
        void transferNullBodyResponse() throws Exception {
            CustomerAccountXRef xref = CustomerAccountXRef.builder()
                    .accountNumber(5001L).customerNumber(1001L).build();
            when(custAccXRefRepository.findByAccountNumber(5001L))
                    .thenReturn(Optional.of(xref));
            when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                    .thenReturn(ResponseEntity.ok(null));

            mockMvc.perform(post("/dashboard/transfer")
                            .with(httpBasic("bankapp", "changeit"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 100.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Transfer processed."));
        }
    }
}
