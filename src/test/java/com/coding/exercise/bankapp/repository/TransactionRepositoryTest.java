package com.coding.exercise.bankapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.coding.exercise.bankapp.model.Transaction;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void saveTransaction() {
        Transaction transaction = Transaction.builder()
                .accountNumber(5001L)
                .txType("CREDIT")
                .txAmount(500.0)
                .txDateTime(new Date())
                .build();

        entityManager.persist(transaction);
        entityManager.flush();

        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(5001L);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertNotNull(result.get().get(0).getId());
        assertEquals("CREDIT", result.get().get(0).getTxType());
        assertEquals(500.0, result.get().get(0).getTxAmount());
    }

    @Test
    void findByAccountNumber_found() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(5001L).txType("CREDIT").txAmount(500.0).txDateTime(new Date()).build();
        Transaction tx2 = Transaction.builder()
                .accountNumber(5001L).txType("DEBIT").txAmount(200.0).txDateTime(new Date()).build();

        entityManager.persist(tx1);
        entityManager.persist(tx2);
        entityManager.flush();

        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
    }

    @Test
    void findByAccountNumber_notFound() {
        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(9999L);

        assertTrue(!result.isPresent() || result.get().isEmpty());
    }

    @Test
    void findByAccountNumber_multipleAccounts() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(5001L).txType("CREDIT").txAmount(500.0).txDateTime(new Date()).build();
        Transaction tx2 = Transaction.builder()
                .accountNumber(5002L).txType("DEBIT").txAmount(200.0).txDateTime(new Date()).build();
        Transaction tx3 = Transaction.builder()
                .accountNumber(5001L).txType("DEBIT").txAmount(100.0).txDateTime(new Date()).build();

        entityManager.persist(tx1);
        entityManager.persist(tx2);
        entityManager.persist(tx3);
        entityManager.flush();

        Optional<List<Transaction>> result5001 = transactionRepository.findByAccountNumber(5001L);
        Optional<List<Transaction>> result5002 = transactionRepository.findByAccountNumber(5002L);

        assertTrue(result5001.isPresent());
        assertEquals(2, result5001.get().size());

        assertTrue(result5002.isPresent());
        assertEquals(1, result5002.get().size());
    }
}
