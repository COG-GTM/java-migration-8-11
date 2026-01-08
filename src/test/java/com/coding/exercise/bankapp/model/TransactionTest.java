package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void testTransactionBuilder() {
        UUID id = UUID.randomUUID();
        Date txDateTime = new Date();
        
        Transaction transaction = Transaction.builder()
                .id(id)
                .accountNumber(1000001L)
                .txDateTime(txDateTime)
                .txType("DEBIT")
                .txAmount(100.00)
                .build();
        
        assertEquals(id, transaction.getId());
        assertEquals(1000001L, transaction.getAccountNumber());
        assertEquals(txDateTime, transaction.getTxDateTime());
        assertEquals("DEBIT", transaction.getTxType());
        assertEquals(100.00, transaction.getTxAmount());
    }

    @Test
    void testTransactionSetters() {
        Transaction transaction = new Transaction();
        UUID id = UUID.randomUUID();
        Date txDateTime = new Date();
        
        transaction.setId(id);
        transaction.setAccountNumber(2000002L);
        transaction.setTxDateTime(txDateTime);
        transaction.setTxType("CREDIT");
        transaction.setTxAmount(500.00);
        
        assertEquals(id, transaction.getId());
        assertEquals(2000002L, transaction.getAccountNumber());
        assertEquals(txDateTime, transaction.getTxDateTime());
        assertEquals("CREDIT", transaction.getTxType());
        assertEquals(500.00, transaction.getTxAmount());
    }

    @Test
    void testTransactionEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Date txDateTime = new Date();
        
        Transaction transaction1 = Transaction.builder()
                .id(id)
                .accountNumber(1000001L)
                .txDateTime(txDateTime)
                .txType("DEBIT")
                .txAmount(100.00)
                .build();
        
        Transaction transaction2 = Transaction.builder()
                .id(id)
                .accountNumber(1000001L)
                .txDateTime(txDateTime)
                .txType("DEBIT")
                .txAmount(100.00)
                .build();
        
        Transaction transaction3 = Transaction.builder()
                .id(UUID.randomUUID())
                .accountNumber(2000002L)
                .txType("CREDIT")
                .txAmount(500.00)
                .build();
        
        assertEquals(transaction1, transaction2);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
        assertNotEquals(transaction1, transaction3);
        assertEquals(transaction1, transaction1);
        assertNotEquals(transaction1, null);
        assertNotEquals(transaction1, "string");
    }

    @Test
    void testTransactionToString() {
        Transaction transaction = Transaction.builder()
                .accountNumber(1000001L)
                .txType("DEBIT")
                .txAmount(100.00)
                .build();
        
        String toString = transaction.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1000001"));
        assertTrue(toString.contains("DEBIT"));
    }

    @Test
    void testTransactionNoArgsConstructor() {
        Transaction transaction = new Transaction();
        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertNull(transaction.getAccountNumber());
    }

    @Test
    void testTransactionAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date txDateTime = new Date();
        
        Transaction transaction = new Transaction(id, 1000001L, txDateTime, "DEBIT", 100.00);
        
        assertEquals(id, transaction.getId());
        assertEquals(1000001L, transaction.getAccountNumber());
        assertEquals(txDateTime, transaction.getTxDateTime());
        assertEquals("DEBIT", transaction.getTxType());
        assertEquals(100.00, transaction.getTxAmount());
    }
}
