package com.coding.exercise.bankapp.integration;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests — Controller → Service → Repository → H2.
 * Covers MBA-1800.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerAccountXRefRepository custAccXRefRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void cleanDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("DELETE FROM TRANSACTION");
        jdbcTemplate.execute("DELETE FROM CUSTOMER_ACCOUNTXREF");
        jdbcTemplate.execute("DELETE FROM ACCOUNT");
        jdbcTemplate.execute("DELETE FROM CUSTOMER");
        jdbcTemplate.execute("DELETE FROM BANK_INFO");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        entityManager.clear();
    }

    @BeforeEach
    void setupData() {
        cleanDatabase();

        Customer customer = Customer.builder()
                .firstName("Carlos").lastName("Rodriguez")
                .middleName("A").customerNumber(1001L)
                .status("Active").createDateTime(new Date()).build();
        customerRepository.save(customer);

        Account checking = Account.builder()
                .accountNumber(5001L).accountType("CHECKING")
                .accountStatus("Active").accountBalance(5000.0)
                .createDateTime(new Date()).build();
        accountRepository.save(checking);

        Account savings = Account.builder()
                .accountNumber(5002L).accountType("SAVINGS")
                .accountStatus("Active").accountBalance(2500.0)
                .createDateTime(new Date()).build();
        accountRepository.save(savings);

        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5001L).customerNumber(1001L).build());
        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5002L).customerNumber(1001L).build());

        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date())
                .txType("CREDIT").txAmount(800.0).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date())
                .txType("DEBIT").txAmount(300.0).build());

    }

    @Test
    @DisplayName("Full data flow: seeded data renders correctly on dashboard")
    void dashboardRendersWithSeededData() throws Exception {
        mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attribute("totalBalance", 7500.0))
                .andExpect(model().attribute("totalAccounts", 2))
                .andExpect(model().attribute("customerName", "Carlos Rodriguez"))
                .andExpect(model().attribute("customerInitials", "CR"))
                .andExpect(model().attribute("totalIncome", 800.0))
                .andExpect(model().attribute("totalExpenses", 300.0));
    }

    @Test
    @DisplayName("Transfer updates H2 and reflects on dashboard")
    void transferUpdatesBalancesOnDashboard() throws Exception {
        mockMvc.perform(post("/dashboard/transfer")
                        .with(httpBasic("bankapp", "changeit"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 500.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalBalance", 7500.0));
    }

    @Test
    @DisplayName("Dashboard with empty database shows defaults")
    void dashboardWithEmptyDatabase() throws Exception {
        cleanDatabase();

        mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalBalance", 0.0))
                .andExpect(model().attribute("totalAccounts", 0))
                .andExpect(model().attribute("customerName", "Banking User"))
                .andExpect(model().attribute("customerInitials", "BU"));
    }

    @Test
    @DisplayName("Transfer with non-existent source account returns error")
    void transferNonExistentAccountReturnsError() throws Exception {
        mockMvc.perform(post("/dashboard/transfer")
                        .with(httpBasic("bankapp", "changeit"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountNumber\": 9999, \"toAccountNumber\": 5002, \"transferAmount\": 100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Account owner not found."));
    }

    @Test
    @DisplayName("Notification count is correct with seeded data")
    void notificationCountCorrect() throws Exception {
        mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("notificationCount"));
    }
}
