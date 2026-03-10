package com.coding.exercise.bankapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testFindByAccountNumber_multipleTransactions() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(5001L)
                .txAmount(100.0)
                .txType("DEBIT")
                .txDateTime(new Date())
                .build();

        Transaction tx2 = Transaction.builder()
                .accountNumber(5001L)
                .txAmount(200.0)
                .txType("CREDIT")
                .txDateTime(new Date())
                .build();

        entityManager.persistAndFlush(tx1);
        entityManager.persistAndFlush(tx2);

        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
    }

    @Test
    public void testFindByAccountNumber_notFound() {
        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(99999L);

        // When no transactions exist, Spring Data may return Optional of empty list or Optional.empty
        if (result.isPresent()) {
            assertTrue(result.get().isEmpty());
        } else {
            assertFalse(result.isPresent());
        }
    }
}
