package com.coding.exercise.bankapp.repository;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.exercise.bankapp.model.Transaction;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
    }

    @Test
    public void testSaveAndFindByAccountNumber() {
        Transaction tx = Transaction.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(1000.0)
                .txDateTime(new Date())
                .build();
        entityManager.persistAndFlush(tx);

        Optional<List<Transaction>> found = transactionRepository.findByAccountNumber(100001L);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().size());
        assertEquals("CREDIT", found.get().get(0).getTxType());
        assertEquals(1000.0, found.get().get(0).getTxAmount(), 0.01);
    }

    @Test
    public void testFindByAccountNumber_NotFound() {
        Optional<List<Transaction>> found = transactionRepository.findByAccountNumber(99999L);

        assertTrue(!found.isPresent() || found.get().isEmpty());
    }

    @Test
    public void testMultipleTransactionsForAccount() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(1000.0)
                .txDateTime(new Date())
                .build();
        Transaction tx2 = Transaction.builder()
                .accountNumber(100001L)
                .txType("DEBIT")
                .txAmount(500.0)
                .txDateTime(new Date())
                .build();
        entityManager.persistAndFlush(tx1);
        entityManager.persistAndFlush(tx2);

        Optional<List<Transaction>> found = transactionRepository.findByAccountNumber(100001L);

        assertTrue(found.isPresent());
        assertEquals(2, found.get().size());
    }

    @Test
    public void testSaveTransaction() {
        Transaction tx = Transaction.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(2500.0)
                .txDateTime(new Date())
                .build();

        Transaction saved = transactionRepository.save(tx);

        assertNotNull(saved.getId());
        assertEquals(Long.valueOf(100001L), saved.getAccountNumber());
        assertEquals(2500.0, saved.getTxAmount(), 0.01);
    }

    @Test
    public void testFindAllTransactions() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(1000.0)
                .txDateTime(new Date())
                .build();
        Transaction tx2 = Transaction.builder()
                .accountNumber(100002L)
                .txType("DEBIT")
                .txAmount(500.0)
                .txDateTime(new Date())
                .build();
        entityManager.persistAndFlush(tx1);
        entityManager.persistAndFlush(tx2);

        Iterable<Transaction> transactions = transactionRepository.findAll();

        int count = 0;
        for (Transaction t : transactions) {
            count++;
        }
        assertEquals(2, count);
    }
}
