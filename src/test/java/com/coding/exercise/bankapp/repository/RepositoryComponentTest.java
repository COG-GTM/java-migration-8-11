package com.coding.exercise.bankapp.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Transaction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Component tests for repository queries.
 * Covers MBA-1794.
 */
@DataJpaTest
@ActiveProfiles("test")
class RepositoryComponentTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // ==================== AccountRepository.findAllAccountSummaries() ====================

    @Nested
    @DisplayName("AccountRepository.findAllAccountSummaries()")
    class AccountRepositorySummaryTests {

        @Test
        @DisplayName("Returns all account summaries as Object arrays")
        void returnsAllAccountSummaries() {
            Account a1 = Account.builder().accountNumber(1001L).accountType("SAVINGS")
                    .accountStatus("Active").accountBalance(5000.0).createDateTime(new Date()).build();
            Account a2 = Account.builder().accountNumber(1002L).accountType("CHECKING")
                    .accountStatus("Active").accountBalance(2500.0).createDateTime(new Date()).build();
            Account a3 = Account.builder().accountNumber(1003L).accountType("CREDIT")
                    .accountStatus("Active").accountBalance(1000.0).createDateTime(new Date()).build();
            accountRepository.save(a1);
            accountRepository.save(a2);
            accountRepository.save(a3);

            List<Object[]> summaries = accountRepository.findAllAccountSummaries();

            assertEquals(3, summaries.size());
            for (Object[] row : summaries) {
                assertNotNull(row[0]); // accountNumber
                assertNotNull(row[1]); // accountType
                assertNotNull(row[2]); // accountStatus
            }
        }

        @Test
        @DisplayName("Empty table returns empty list")
        void emptyTableReturnsEmptyList() {
            List<Object[]> summaries = accountRepository.findAllAccountSummaries();
            assertTrue(summaries.isEmpty());
        }

        @Test
        @DisplayName("Null balance field returned as null in array")
        void nullBalanceFieldHandled() {
            Account a = Account.builder().accountNumber(1001L).accountType("SAVINGS")
                    .accountStatus("Active").accountBalance(null).createDateTime(new Date()).build();
            accountRepository.save(a);

            List<Object[]> summaries = accountRepository.findAllAccountSummaries();

            assertEquals(1, summaries.size());
            assertNull(summaries.get(0)[3]);
        }
    }

    // ==================== TransactionRepository.findByAccountNumber() ====================

    @Nested
    @DisplayName("TransactionRepository.findByAccountNumber()")
    class TransactionRepositoryTests {

        @Test
        @DisplayName("Returns transactions for existing account")
        void returnsTransactionsForExistingAccount() {
            for (int i = 0; i < 5; i++) {
                Transaction tx = Transaction.builder().accountNumber(1001L)
                        .txDateTime(new Date()).txType("CREDIT").txAmount(100.0 + i).build();
                transactionRepository.save(tx);
            }

            Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(1001L);

            assertTrue(result.isPresent());
            assertEquals(5, result.get().size());
        }

        @Test
        @DisplayName("Returns empty for non-existent account")
        void returnsEmptyForNonExistentAccount() {
            Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(9999L);

            assertTrue(result.isEmpty() || (result.isPresent() && result.get().isEmpty()));
        }

        @Test
        @DisplayName("Only returns transactions matching the account number")
        void onlyMatchingAccountTransactions() {
            Transaction tx1001 = Transaction.builder().accountNumber(1001L)
                    .txDateTime(new Date()).txType("CREDIT").txAmount(100.0).build();
            Transaction tx1002 = Transaction.builder().accountNumber(1002L)
                    .txDateTime(new Date()).txType("DEBIT").txAmount(200.0).build();
            transactionRepository.save(tx1001);
            transactionRepository.save(tx1002);

            Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(1001L);

            assertTrue(result.isPresent());
            assertEquals(1, result.get().size());
            assertEquals(1001L, result.get().get(0).getAccountNumber());
        }
    }
}
