package com.coding.exercise.bankapp.e2e;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.coding.exercise.bankapp.domain.NotificationItem;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E/Functional tests for the dashboard.
 * Covers MBA-1795 (dashboard load), MBA-1796 (transactions),
 * MBA-1797 (transfer widget), MBA-1798 (notifications).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardE2ETest {

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
    void seedData() {
        cleanDatabase();

        Customer customer = Customer.builder()
                .firstName("Maria").lastName("Garcia")
                .middleName("L").customerNumber(2001L)
                .status("Active").createDateTime(new Date()).build();
        customerRepository.save(customer);

        Account savings = Account.builder()
                .accountNumber(5001L).accountType("SAVINGS")
                .accountStatus("Active").accountBalance(5000.0)
                .createDateTime(new Date()).build();
        accountRepository.save(savings);

        Account checking = Account.builder()
                .accountNumber(5002L).accountType("CHECKING")
                .accountStatus("Active").accountBalance(2500.0)
                .createDateTime(new Date()).build();
        accountRepository.save(checking);

        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5001L).customerNumber(2001L).build());
        custAccXRefRepository.save(CustomerAccountXRef.builder()
                .accountNumber(5002L).customerNumber(2001L).build());

        long now = System.currentTimeMillis();
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date(now - 1000))
                .txType("CREDIT").txAmount(500.0).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date(now - 2000))
                .txType("DEBIT").txAmount(200.0).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date(now - 3000))
                .txType("CREDIT").txAmount(300.0).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5001L).txDateTime(new Date(now - 500))
                .txType("CREDIT").txAmount(100.0).build());
        transactionRepository.save(Transaction.builder()
                .accountNumber(5002L).txDateTime(new Date(now - 4000))
                .txType("DEBIT").txAmount(100.0).build());

    }

    // ==================== MBA-1795: Full Dashboard Page Load ====================

    @Nested
    @DisplayName("MBA-1795: Full Dashboard Page Load E2E")
    class DashboardPageLoadTests {

        @Test
        @DisplayName("Dashboard displays total consolidated balance")
        void dashboardDisplaysTotalBalance() throws Exception {
            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("totalBalance", 7500.0));
        }

        @Test
        @DisplayName("Individual account cards displayed")
        void individualAccountCardsDisplayed() throws Exception {
            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("accounts", hasSize(2)))
                    .andExpect(model().attribute("totalAccounts", 2));
        }

        @Test
        @DisplayName("Income and expense summary calculated correctly")
        void incomeAndExpenseSummary() throws Exception {
            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("totalIncome", 900.0))
                    .andExpect(model().attribute("totalExpenses", 300.0));
        }

        @Test
        @DisplayName("Customer name and initials displayed")
        void customerNameAndInitials() throws Exception {
            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("customerName", "Maria Garcia"))
                    .andExpect(model().attribute("customerInitials", "MG"));
        }

        @Test
        @DisplayName("Dashboard requires authentication")
        void dashboardRequiresAuth() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ==================== MBA-1796: Recent Transactions List ====================

    @Nested
    @DisplayName("MBA-1796: Recent Transactions List E2E")
    class TransactionsListTests {

        @Test
        @DisplayName("Transactions displayed sorted by date newest first")
        void transactionsSortedByDateNewestFirst() throws Exception {
            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("transactions", hasSize(5)))
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<TransactionDetails> txns =
                    (java.util.List<TransactionDetails>)
                            result.getModelAndView().getModel().get("transactions");

            for (int i = 0; i < txns.size() - 1; i++) {
                Date current = txns.get(i).getTxDateTime();
                Date next = txns.get(i + 1).getTxDateTime();
                if (current != null && next != null) {
                    assertTrue(current.compareTo(next) >= 0,
                            "Transactions should be sorted newest first");
                }
            }
        }

        @Test
        @DisplayName("Each transaction has required fields")
        void eachTransactionHasRequiredFields() throws Exception {
            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<TransactionDetails> txns =
                    (java.util.List<TransactionDetails>)
                            result.getModelAndView().getModel().get("transactions");

            for (TransactionDetails tx : txns) {
                assertNotNull(tx.getAccountNumber());
                assertNotNull(tx.getTxType());
                assertNotNull(tx.getTxAmount());
            }
        }

        @Test
        @DisplayName("Transaction types CREDIT and DEBIT present")
        void transactionTypesPresent() throws Exception {
            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<TransactionDetails> txns =
                    (java.util.List<TransactionDetails>)
                            result.getModelAndView().getModel().get("transactions");

            assertTrue(txns.stream().anyMatch(t -> "CREDIT".equals(t.getTxType())));
            assertTrue(txns.stream().anyMatch(t -> "DEBIT".equals(t.getTxType())));
        }

        @Test
        @DisplayName("Transactions from multiple accounts aggregated")
        void transactionsFromMultipleAccountsAggregated() throws Exception {
            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<TransactionDetails> txns =
                    (java.util.List<TransactionDetails>)
                            result.getModelAndView().getModel().get("transactions");

            assertTrue(txns.stream().anyMatch(t -> t.getAccountNumber().equals(5001L)));
            assertTrue(txns.stream().anyMatch(t -> t.getAccountNumber().equals(5002L)));
        }
    }

    // ==================== MBA-1797: Quick Transfer Widget ====================

    @Nested
    @DisplayName("MBA-1797: Quick Transfer Widget E2E")
    class QuickTransferTests {

        @Test
        @DisplayName("Successful transfer returns success response")
        void successfulTransferReturnsSuccess() throws Exception {
            mockMvc.perform(post("/dashboard/transfer")
                            .with(httpBasic("bankapp", "changeit"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 500.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Transfer fails with invalid source account")
        void transferFailsInvalidSourceAccount() throws Exception {
            mockMvc.perform(post("/dashboard/transfer")
                            .with(httpBasic("bankapp", "changeit"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 9999, \"toAccountNumber\": 5002, \"transferAmount\": 100.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Account owner not found."));
        }

        @Test
        @DisplayName("Transfer requires authentication")
        void transferRequiresAuth() throws Exception {
            mockMvc.perform(post("/dashboard/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountNumber\": 5001, \"toAccountNumber\": 5002, \"transferAmount\": 100.0}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Transfer available notification shown when 2+ accounts")
        void transferAvailableNotification() throws Exception {
            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<NotificationItem> notifications =
                    (java.util.List<NotificationItem>)
                            result.getModelAndView().getModel().get("notifications");

            assertTrue(notifications.stream()
                    .anyMatch(n -> "Quick Transfer Available".equals(n.getTitle())));
        }
    }

    // ==================== MBA-1798: Notification Center Panel ====================

    @Nested
    @DisplayName("MBA-1798: Notification Center Panel E2E")
    class NotificationCenterTests {

        @Test
        @DisplayName("Notification count shown in model")
        void notificationCountInModel() throws Exception {
            mockMvc.perform(get("/dashboard").with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("notificationCount"));
        }

        @Test
        @DisplayName("Low balance notification for account below $100")
        void lowBalanceNotification() throws Exception {
            Account lowBalanceAcct = Account.builder()
                    .accountNumber(5003L).accountType("SAVINGS")
                    .accountStatus("Active").accountBalance(50.0)
                    .createDateTime(new Date()).build();
            accountRepository.save(lowBalanceAcct);

            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<NotificationItem> notifications =
                    (java.util.List<NotificationItem>)
                            result.getModelAndView().getModel().get("notifications");

            assertTrue(notifications.stream()
                    .anyMatch(n -> "Low Balance Alert".equals(n.getTitle())));
        }

        @Test
        @DisplayName("Transaction activity notification when transactions exist")
        void transactionActivityNotification() throws Exception {
            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<NotificationItem> notifications =
                    (java.util.List<NotificationItem>)
                            result.getModelAndView().getModel().get("notifications");

            assertTrue(notifications.stream()
                    .anyMatch(n -> "Transaction Activity".equals(n.getTitle())));
        }

        @Test
        @DisplayName("Welcome notification shown when no accounts exist")
        void welcomeNotificationWhenEmpty() throws Exception {
            cleanDatabase();

            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<NotificationItem> notifications =
                    (java.util.List<NotificationItem>)
                            result.getModelAndView().getModel().get("notifications");

            assertTrue(notifications.stream()
                    .anyMatch(n -> "Welcome to BBVA Net Cash".equals(n.getTitle())));
        }

        @Test
        @DisplayName("Unread notification count reflects only unread items")
        void unreadNotificationCountCorrect() throws Exception {
            MvcResult result = mockMvc.perform(get("/dashboard")
                            .with(httpBasic("bankapp", "changeit")))
                    .andExpect(status().isOk())
                    .andReturn();

            @SuppressWarnings("unchecked")
            java.util.List<NotificationItem> notifications =
                    (java.util.List<NotificationItem>)
                            result.getModelAndView().getModel().get("notifications");

            long expectedUnread = notifications.stream().filter(n -> !n.isRead()).count();
            Long actualCount = (Long) result.getModelAndView().getModel().get("notificationCount");
            assertEquals(expectedUnread, actualCount);
        }
    }
}
