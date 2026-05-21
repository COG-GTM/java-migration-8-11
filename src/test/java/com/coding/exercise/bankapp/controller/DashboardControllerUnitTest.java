package com.coding.exercise.bankapp.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coding.exercise.bankapp.domain.DashboardAccountInfo;
import com.coding.exercise.bankapp.domain.NotificationItem;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DashboardController private methods.
 * Covers MBA-1787, MBA-1788, MBA-1789, MBA-1790, MBA-1791.
 */
@ExtendWith(MockitoExtension.class)
class DashboardControllerUnitTest {

    @InjectMocks
    private DashboardController dashboardController;

    @Mock
    private BankingServiceImpl bankingService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerAccountXRefRepository custAccXRefRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankingServiceHelper bankingServiceHelper;

    private Method getInitialsMethod;
    private Method getFirstCustomerNameMethod;
    private Method buildNotificationsMethod;
    private Method getAllAccountsMethod;
    private Method getAllTransactionsMethod;

    @BeforeEach
    void setUp() throws Exception {
        getInitialsMethod = DashboardController.class.getDeclaredMethod("getInitials", String.class);
        getInitialsMethod.setAccessible(true);

        getFirstCustomerNameMethod = DashboardController.class.getDeclaredMethod("getFirstCustomerName");
        getFirstCustomerNameMethod.setAccessible(true);

        buildNotificationsMethod = DashboardController.class.getDeclaredMethod(
                "buildNotifications", List.class, List.class);
        buildNotificationsMethod.setAccessible(true);

        getAllAccountsMethod = DashboardController.class.getDeclaredMethod("getAllAccounts");
        getAllAccountsMethod.setAccessible(true);

        getAllTransactionsMethod = DashboardController.class.getDeclaredMethod("getAllTransactions", List.class);
        getAllTransactionsMethod.setAccessible(true);
    }

    // ==================== MBA-1787: getInitials() ====================

    @Nested
    @DisplayName("MBA-1787: getInitials()")
    class GetInitialsTests {

        @Test
        @DisplayName("Full name with first and last name returns both initials")
        void fullNameReturnsBothInitials() throws Exception {
            String result = (String) getInitialsMethod.invoke(dashboardController, "Maria Garcia");
            assertEquals("MG", result);
        }

        @Test
        @DisplayName("Single name only returns one initial")
        void singleNameReturnsOneInitial() throws Exception {
            String result = (String) getInitialsMethod.invoke(dashboardController, "Carlos");
            assertEquals("C", result);
        }

        @Test
        @DisplayName("Name with multiple parts returns first and last initials")
        void multiplePartsReturnsFirstAndLastInitials() throws Exception {
            String result = (String) getInitialsMethod.invoke(dashboardController, "Carlos Alberto Rodriguez Lopez");
            assertEquals("CL", result);
        }

        @Test
        @DisplayName("Null input returns default 'U'")
        void nullInputReturnsDefault() throws Exception {
            String result = (String) getInitialsMethod.invoke(dashboardController, (Object) null);
            assertEquals("U", result);
        }

        @Test
        @DisplayName("Empty string returns default 'U'")
        void emptyStringReturnsDefault() throws Exception {
            String result = (String) getInitialsMethod.invoke(dashboardController, "");
            assertEquals("U", result);
        }

        @Test
        @DisplayName("Whitespace only returns default 'U'")
        void whitespaceOnlyReturnsDefault() throws Exception {
            String result = (String) getInitialsMethod.invoke(dashboardController, "   ");
            assertEquals("U", result);
        }

        @Test
        @DisplayName("Lowercase name returns uppercase initials")
        void lowercaseNameReturnsUppercaseInitials() throws Exception {
            String result = (String) getInitialsMethod.invoke(dashboardController, "maria garcia");
            assertEquals("MG", result);
        }
    }

    // ==================== MBA-1788: getFirstCustomerName() ====================

    @Nested
    @DisplayName("MBA-1788: getFirstCustomerName()")
    class GetFirstCustomerNameTests {

        @Test
        @DisplayName("Customer exists with full name")
        void customerExistsWithFullName() throws Exception {
            List<Object[]> names = new ArrayList<>();
            names.add(new Object[]{"Maria", "Garcia"});
            when(customerRepository.findAllCustomerNames()).thenReturn(names);

            String result = (String) getFirstCustomerNameMethod.invoke(dashboardController);
            assertEquals("Maria Garcia", result);
        }

        @Test
        @DisplayName("Customer exists with first name only (null last name)")
        void customerExistsWithFirstNameOnly() throws Exception {
            List<Object[]> names = new ArrayList<>();
            names.add(new Object[]{"Carlos", null});
            when(customerRepository.findAllCustomerNames()).thenReturn(names);

            String result = (String) getFirstCustomerNameMethod.invoke(dashboardController);
            assertEquals("Carlos", result);
        }

        @Test
        @DisplayName("Customer exists with last name only (null first name)")
        void customerExistsWithLastNameOnly() throws Exception {
            List<Object[]> names = new ArrayList<>();
            names.add(new Object[]{null, "Rodriguez"});
            when(customerRepository.findAllCustomerNames()).thenReturn(names);

            String result = (String) getFirstCustomerNameMethod.invoke(dashboardController);
            assertEquals("Rodriguez", result);
        }

        @Test
        @DisplayName("No customers returns default 'Banking User'")
        void noCustomersReturnsDefault() throws Exception {
            when(customerRepository.findAllCustomerNames()).thenReturn(Collections.emptyList());

            String result = (String) getFirstCustomerNameMethod.invoke(dashboardController);
            assertEquals("Banking User", result);
        }

        @Test
        @DisplayName("Customer with both names null returns empty string")
        void bothNamesNullReturnsEmptyString() throws Exception {
            List<Object[]> names = new ArrayList<>();
            names.add(new Object[]{null, null});
            when(customerRepository.findAllCustomerNames()).thenReturn(names);

            String result = (String) getFirstCustomerNameMethod.invoke(dashboardController);
            assertEquals("", result);
        }
    }

    // ==================== MBA-1789: buildNotifications() ====================

    @Nested
    @DisplayName("MBA-1789: buildNotifications()")
    class BuildNotificationsTests {

        @Test
        @DisplayName("Empty accounts list generates welcome notification")
        @SuppressWarnings("unchecked")
        void emptyAccountsGeneratesWelcomeNotification() throws Exception {
            List<DashboardAccountInfo> accounts = Collections.emptyList();
            List<TransactionDetails> transactions = Collections.emptyList();

            List<NotificationItem> result = (List<NotificationItem>) buildNotificationsMethod
                    .invoke(dashboardController, accounts, transactions);

            assertEquals(1, result.size());
            assertEquals("Welcome to BBVA Net Cash", result.get(0).getTitle());
            assertEquals("info", result.get(0).getType());
            assertFalse(result.get(0).isRead());
        }

        @Test
        @DisplayName("Low balance alert for account below $100")
        @SuppressWarnings("unchecked")
        void lowBalanceAlertGenerated() throws Exception {
            List<DashboardAccountInfo> accounts = Arrays.asList(
                    DashboardAccountInfo.builder().accountNumber(1001L).accountBalance(50.0).build());
            List<TransactionDetails> transactions = Collections.emptyList();

            List<NotificationItem> result = (List<NotificationItem>) buildNotificationsMethod
                    .invoke(dashboardController, accounts, transactions);

            assertTrue(result.stream().anyMatch(n -> "Low Balance Alert".equals(n.getTitle())));
            NotificationItem lowBalance = result.stream()
                    .filter(n -> "Low Balance Alert".equals(n.getTitle())).findFirst().orElseThrow();
            assertEquals("warning", lowBalance.getType());
            assertFalse(lowBalance.isRead());
        }

        @Test
        @DisplayName("No low balance alert for account at or above $100")
        @SuppressWarnings("unchecked")
        void noLowBalanceAlertAboveThreshold() throws Exception {
            List<DashboardAccountInfo> accounts = Arrays.asList(
                    DashboardAccountInfo.builder().accountNumber(1001L).accountBalance(100.0).build());
            List<TransactionDetails> transactions = Collections.emptyList();

            List<NotificationItem> result = (List<NotificationItem>) buildNotificationsMethod
                    .invoke(dashboardController, accounts, transactions);

            assertTrue(result.stream().noneMatch(n -> "Low Balance Alert".equals(n.getTitle())));
        }

        @Test
        @DisplayName("Transaction activity notification when transactions exist")
        @SuppressWarnings("unchecked")
        void transactionActivityNotification() throws Exception {
            List<DashboardAccountInfo> accounts = Arrays.asList(
                    DashboardAccountInfo.builder().accountNumber(1001L).accountBalance(500.0).build());
            List<TransactionDetails> transactions = Arrays.asList(
                    TransactionDetails.builder().txAmount(100.0).txType("CREDIT").build(),
                    TransactionDetails.builder().txAmount(50.0).txType("DEBIT").build(),
                    TransactionDetails.builder().txAmount(200.0).txType("CREDIT").build());

            List<NotificationItem> result = (List<NotificationItem>) buildNotificationsMethod
                    .invoke(dashboardController, accounts, transactions);

            NotificationItem activity = result.stream()
                    .filter(n -> "Transaction Activity".equals(n.getTitle())).findFirst().orElseThrow();
            assertTrue(activity.getMessage().contains("3 transaction(s)"));
            assertEquals("success", activity.getType());
            assertTrue(activity.isRead());
        }

        @Test
        @DisplayName("Quick Transfer Available when 2+ accounts exist")
        @SuppressWarnings("unchecked")
        void quickTransferNotificationWithMultipleAccounts() throws Exception {
            List<DashboardAccountInfo> accounts = Arrays.asList(
                    DashboardAccountInfo.builder().accountNumber(1001L).accountBalance(500.0).build(),
                    DashboardAccountInfo.builder().accountNumber(1002L).accountBalance(300.0).build());
            List<TransactionDetails> transactions = Collections.emptyList();

            List<NotificationItem> result = (List<NotificationItem>) buildNotificationsMethod
                    .invoke(dashboardController, accounts, transactions);

            assertTrue(result.stream().anyMatch(n -> "Quick Transfer Available".equals(n.getTitle())));
        }

        @Test
        @DisplayName("No Quick Transfer notification with single account")
        @SuppressWarnings("unchecked")
        void noQuickTransferWithSingleAccount() throws Exception {
            List<DashboardAccountInfo> accounts = Arrays.asList(
                    DashboardAccountInfo.builder().accountNumber(1001L).accountBalance(500.0).build());
            List<TransactionDetails> transactions = Collections.emptyList();

            List<NotificationItem> result = (List<NotificationItem>) buildNotificationsMethod
                    .invoke(dashboardController, accounts, transactions);

            assertTrue(result.stream().noneMatch(n -> "Quick Transfer Available".equals(n.getTitle())));
        }

        @Test
        @DisplayName("Multiple conditions combined generate correct notifications")
        @SuppressWarnings("unchecked")
        void multipleCombinedConditions() throws Exception {
            List<DashboardAccountInfo> accounts = Arrays.asList(
                    DashboardAccountInfo.builder().accountNumber(1001L).accountBalance(50.0).build(),
                    DashboardAccountInfo.builder().accountNumber(1002L).accountBalance(200.0).build());
            List<TransactionDetails> transactions = Arrays.asList(
                    TransactionDetails.builder().txAmount(100.0).txType("CREDIT").build());

            List<NotificationItem> result = (List<NotificationItem>) buildNotificationsMethod
                    .invoke(dashboardController, accounts, transactions);

            assertTrue(result.stream().anyMatch(n -> "Low Balance Alert".equals(n.getTitle())));
            assertTrue(result.stream().anyMatch(n -> "Transaction Activity".equals(n.getTitle())));
            assertTrue(result.stream().anyMatch(n -> "Quick Transfer Available".equals(n.getTitle())));
            assertTrue(result.stream().noneMatch(n -> "Welcome to BBVA Net Cash".equals(n.getTitle())));
        }
    }

    // ==================== MBA-1790: getAllAccounts() ====================

    @Nested
    @DisplayName("MBA-1790: getAllAccounts()")
    class GetAllAccountsTests {

        @Test
        @DisplayName("Multiple accounts returned correctly")
        @SuppressWarnings("unchecked")
        void multipleAccountsReturnedCorrectly() throws Exception {
            List<Object[]> rows = Arrays.asList(
                    new Object[]{1001L, "SAVINGS", "Active", 5000.0},
                    new Object[]{1002L, "CHECKING", "Active", 2500.0});
            when(accountRepository.findAllAccountSummaries()).thenReturn(rows);

            List<DashboardAccountInfo> result = (List<DashboardAccountInfo>) getAllAccountsMethod
                    .invoke(dashboardController);

            assertEquals(2, result.size());
            assertEquals(1001L, result.get(0).getAccountNumber());
            assertEquals("SAVINGS", result.get(0).getAccountType());
            assertEquals("Active", result.get(0).getAccountStatus());
            assertEquals(5000.0, result.get(0).getAccountBalance());
            assertEquals(1002L, result.get(1).getAccountNumber());
            assertEquals("CHECKING", result.get(1).getAccountType());
            assertEquals(2500.0, result.get(1).getAccountBalance());
        }

        @Test
        @DisplayName("Empty account list returns empty result")
        @SuppressWarnings("unchecked")
        void emptyAccountListReturnsEmpty() throws Exception {
            when(accountRepository.findAllAccountSummaries()).thenReturn(Collections.emptyList());

            List<DashboardAccountInfo> result = (List<DashboardAccountInfo>) getAllAccountsMethod
                    .invoke(dashboardController);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Null balance in account row defaults to 0.0")
        @SuppressWarnings("unchecked")
        void nullBalanceDefaultsToZero() throws Exception {
            List<Object[]> rows = new ArrayList<>();
            rows.add(new Object[]{1001L, "SAVINGS", "Active", null});
            when(accountRepository.findAllAccountSummaries()).thenReturn(rows);

            List<DashboardAccountInfo> result = (List<DashboardAccountInfo>) getAllAccountsMethod
                    .invoke(dashboardController);

            assertEquals(1, result.size());
            assertEquals(0.0, result.get(0).getAccountBalance());
        }

        @Test
        @DisplayName("Various account types mapped correctly")
        @SuppressWarnings("unchecked")
        void variousAccountTypesMapped() throws Exception {
            List<Object[]> rows = Arrays.asList(
                    new Object[]{1001L, "SAVINGS", "Active", 100.0},
                    new Object[]{1002L, "CHECKING", "Active", 200.0},
                    new Object[]{1003L, "CREDIT", "Active", 300.0});
            when(accountRepository.findAllAccountSummaries()).thenReturn(rows);

            List<DashboardAccountInfo> result = (List<DashboardAccountInfo>) getAllAccountsMethod
                    .invoke(dashboardController);

            assertEquals(3, result.size());
            assertEquals("SAVINGS", result.get(0).getAccountType());
            assertEquals("CHECKING", result.get(1).getAccountType());
            assertEquals("CREDIT", result.get(2).getAccountType());
        }

        @Test
        @DisplayName("Account status values preserved")
        @SuppressWarnings("unchecked")
        void accountStatusValuesPreserved() throws Exception {
            List<Object[]> rows = Arrays.asList(
                    new Object[]{1001L, "SAVINGS", "Active", 100.0},
                    new Object[]{1002L, "CHECKING", "Inactive", 200.0});
            when(accountRepository.findAllAccountSummaries()).thenReturn(rows);

            List<DashboardAccountInfo> result = (List<DashboardAccountInfo>) getAllAccountsMethod
                    .invoke(dashboardController);

            assertEquals("Active", result.get(0).getAccountStatus());
            assertEquals("Inactive", result.get(1).getAccountStatus());
        }
    }

    // ==================== MBA-1791: getAllTransactions() ====================

    @Nested
    @DisplayName("MBA-1791: getAllTransactions()")
    class GetAllTransactionsTests {

        @Test
        @DisplayName("Transactions sorted by date descending (newest first)")
        @SuppressWarnings("unchecked")
        void transactionsSortedByDateDesc() throws Exception {
            Date oldest = new Date(1000000);
            Date middle = new Date(2000000);
            Date newest = new Date(3000000);

            DashboardAccountInfo acct = DashboardAccountInfo.builder().accountNumber(1001L).build();

            Transaction tx1 = Transaction.builder().accountNumber(1001L).txDateTime(oldest)
                    .txType("CREDIT").txAmount(100.0).build();
            Transaction tx2 = Transaction.builder().accountNumber(1001L).txDateTime(newest)
                    .txType("DEBIT").txAmount(200.0).build();
            Transaction tx3 = Transaction.builder().accountNumber(1001L).txDateTime(middle)
                    .txType("CREDIT").txAmount(300.0).build();

            when(transactionRepository.findByAccountNumber(1001L))
                    .thenReturn(Optional.of(Arrays.asList(tx1, tx2, tx3)));

            when(bankingServiceHelper.convertToTransactionDomain(tx1))
                    .thenReturn(TransactionDetails.builder().accountNumber(1001L)
                            .txDateTime(oldest).txType("CREDIT").txAmount(100.0).build());
            when(bankingServiceHelper.convertToTransactionDomain(tx2))
                    .thenReturn(TransactionDetails.builder().accountNumber(1001L)
                            .txDateTime(newest).txType("DEBIT").txAmount(200.0).build());
            when(bankingServiceHelper.convertToTransactionDomain(tx3))
                    .thenReturn(TransactionDetails.builder().accountNumber(1001L)
                            .txDateTime(middle).txType("CREDIT").txAmount(300.0).build());

            List<TransactionDetails> result = (List<TransactionDetails>) getAllTransactionsMethod
                    .invoke(dashboardController, Arrays.asList(acct));

            assertEquals(3, result.size());
            assertEquals(newest, result.get(0).getTxDateTime());
            assertEquals(middle, result.get(1).getTxDateTime());
            assertEquals(oldest, result.get(2).getTxDateTime());
        }

        @Test
        @DisplayName("Empty accounts list returns empty transactions")
        @SuppressWarnings("unchecked")
        void emptyAccountsReturnsEmptyTransactions() throws Exception {
            List<TransactionDetails> result = (List<TransactionDetails>) getAllTransactionsMethod
                    .invoke(dashboardController, Collections.emptyList());

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Account with no transactions returns no entries")
        @SuppressWarnings("unchecked")
        void accountWithNoTransactions() throws Exception {
            DashboardAccountInfo acct = DashboardAccountInfo.builder().accountNumber(1001L).build();
            when(transactionRepository.findByAccountNumber(1001L)).thenReturn(Optional.empty());

            List<TransactionDetails> result = (List<TransactionDetails>) getAllTransactionsMethod
                    .invoke(dashboardController, Arrays.asList(acct));

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Multiple accounts aggregate all transactions")
        @SuppressWarnings("unchecked")
        void multipleAccountsAggregateTransactions() throws Exception {
            Date date1 = new Date(3000000);
            Date date2 = new Date(1000000);
            Date date3 = new Date(2000000);

            DashboardAccountInfo acctA = DashboardAccountInfo.builder().accountNumber(1001L).build();
            DashboardAccountInfo acctB = DashboardAccountInfo.builder().accountNumber(1002L).build();

            Transaction txA1 = Transaction.builder().accountNumber(1001L).txDateTime(date1)
                    .txType("CREDIT").txAmount(100.0).build();
            Transaction txA2 = Transaction.builder().accountNumber(1001L).txDateTime(date2)
                    .txType("DEBIT").txAmount(50.0).build();
            Transaction txB1 = Transaction.builder().accountNumber(1002L).txDateTime(date3)
                    .txType("CREDIT").txAmount(200.0).build();

            when(transactionRepository.findByAccountNumber(1001L))
                    .thenReturn(Optional.of(Arrays.asList(txA1, txA2)));
            when(transactionRepository.findByAccountNumber(1002L))
                    .thenReturn(Optional.of(Arrays.asList(txB1)));

            when(bankingServiceHelper.convertToTransactionDomain(txA1))
                    .thenReturn(TransactionDetails.builder().accountNumber(1001L)
                            .txDateTime(date1).txType("CREDIT").txAmount(100.0).build());
            when(bankingServiceHelper.convertToTransactionDomain(txA2))
                    .thenReturn(TransactionDetails.builder().accountNumber(1001L)
                            .txDateTime(date2).txType("DEBIT").txAmount(50.0).build());
            when(bankingServiceHelper.convertToTransactionDomain(txB1))
                    .thenReturn(TransactionDetails.builder().accountNumber(1002L)
                            .txDateTime(date3).txType("CREDIT").txAmount(200.0).build());

            List<TransactionDetails> result = (List<TransactionDetails>) getAllTransactionsMethod
                    .invoke(dashboardController, Arrays.asList(acctA, acctB));

            assertEquals(3, result.size());
            assertEquals(date1, result.get(0).getTxDateTime());
            assertEquals(date3, result.get(1).getTxDateTime());
            assertEquals(date2, result.get(2).getTxDateTime());
        }

        @Test
        @DisplayName("Null txDateTime handled in sorting without exception")
        @SuppressWarnings("unchecked")
        void nullDateTimeHandledInSorting() throws Exception {
            Date validDate = new Date(1000000);

            DashboardAccountInfo acct = DashboardAccountInfo.builder().accountNumber(1001L).build();

            Transaction tx1 = Transaction.builder().accountNumber(1001L).txDateTime(validDate)
                    .txType("CREDIT").txAmount(100.0).build();
            Transaction tx2 = Transaction.builder().accountNumber(1001L).txDateTime(null)
                    .txType("DEBIT").txAmount(50.0).build();

            when(transactionRepository.findByAccountNumber(1001L))
                    .thenReturn(Optional.of(Arrays.asList(tx1, tx2)));

            when(bankingServiceHelper.convertToTransactionDomain(tx1))
                    .thenReturn(TransactionDetails.builder().accountNumber(1001L)
                            .txDateTime(validDate).txType("CREDIT").txAmount(100.0).build());
            when(bankingServiceHelper.convertToTransactionDomain(tx2))
                    .thenReturn(TransactionDetails.builder().accountNumber(1001L)
                            .txDateTime(null).txType("DEBIT").txAmount(50.0).build());

            List<TransactionDetails> result = (List<TransactionDetails>) getAllTransactionsMethod
                    .invoke(dashboardController, Arrays.asList(acct));

            assertEquals(2, result.size());
            assertNotNull(result.get(0).getTxDateTime());
            assertNull(result.get(1).getTxDateTime());
        }
    }

    // ==================== MBA-1787 (TransferResponse POJO — from original test plan) ====================

    @Nested
    @DisplayName("TransferResponse POJO tests")
    class TransferResponseTests {

        @Test
        @DisplayName("Success response construction")
        void successResponseConstruction() {
            DashboardController.TransferResponse response =
                    new DashboardController.TransferResponse(true, "Transfer completed successfully!");
            assertTrue(response.isSuccess());
            assertEquals("Transfer completed successfully!", response.getMessage());
        }

        @Test
        @DisplayName("Failure response construction")
        void failureResponseConstruction() {
            DashboardController.TransferResponse response =
                    new DashboardController.TransferResponse(false, "Insufficient funds.");
            assertFalse(response.isSuccess());
            assertEquals("Insufficient funds.", response.getMessage());
        }

        @Test
        @DisplayName("Null message handling")
        void nullMessageHandling() {
            DashboardController.TransferResponse response =
                    new DashboardController.TransferResponse(true, null);
            assertTrue(response.isSuccess());
            assertNull(response.getMessage());
        }
    }
}
