package com.coding.exercise.bankapp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.exercise.bankapp.model.Transaction;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction buildTransaction(Long accountNumber, String txType, Double amount) {
        return Transaction.builder()
                .accountNumber(accountNumber)
                .txType(txType)
                .txAmount(amount)
                .txDateTime(new Date())
                .build();
    }

    @Test
    public void save_and_findById() {
        Transaction tx = buildTransaction(5001L, "DEBIT", 100.0);
        Transaction saved = transactionRepository.save(tx);

        assertNotNull(saved.getId());
    }

    @Test
    public void findByAccountNumber_found() {
        transactionRepository.save(buildTransaction(5001L, "DEBIT", 100.0));
        transactionRepository.save(buildTransaction(5001L, "CREDIT", 200.0));
        transactionRepository.save(buildTransaction(5002L, "DEBIT", 50.0));

        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
    }

    @Test
    public void findByAccountNumber_notFound() {
        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(9999L);

        // Spring Data returns empty list wrapped in Optional, or Optional.empty
        assertTrue(!result.isPresent() || result.get().isEmpty());
    }

    @Test
    public void findAll_returnsAllTransactions() {
        transactionRepository.save(buildTransaction(5001L, "DEBIT", 100.0));
        transactionRepository.save(buildTransaction(5002L, "CREDIT", 200.0));

        Iterable<Transaction> all = transactionRepository.findAll();
        int count = 0;
        for (Transaction t : all) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void delete_removesTransaction() {
        Transaction tx = buildTransaction(5001L, "DEBIT", 100.0);
        Transaction saved = transactionRepository.save(tx);

        transactionRepository.delete(saved);

        long count = transactionRepository.count();
        assertEquals(0, count);
    }

    @Test
    public void multipleTransactionsForSameAccount() {
        transactionRepository.save(buildTransaction(5001L, "DEBIT", 50.0));
        transactionRepository.save(buildTransaction(5001L, "CREDIT", 75.0));
        transactionRepository.save(buildTransaction(5001L, "DEBIT", 25.0));

        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }
}
