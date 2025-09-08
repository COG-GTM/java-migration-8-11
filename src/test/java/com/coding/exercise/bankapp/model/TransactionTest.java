package com.coding.exercise.bankapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.UUID;

class TransactionTest {

    @Test
    void testTransactionBuilder_ShouldCreateTransactionWithAllFields() {
        Date txDate = new Date();
        UUID txId = UUID.randomUUID();
        
        Transaction transaction = Transaction.builder()
            .id(txId)
            .accountNumber(12345L)
            .txAmount(250.0)
            .txDateTime(txDate)
            .txType("DEBIT")
            .build();
        
        assertNotNull(transaction);
        assertEquals(txId, transaction.getId());
        assertEquals(Long.valueOf(12345L), transaction.getAccountNumber());
        assertEquals(Double.valueOf(250.0), transaction.getTxAmount());
        assertEquals(txDate, transaction.getTxDateTime());
        assertEquals("DEBIT", transaction.getTxType());
    }
    
    @Test
    void testTransactionNoArgsConstructor_ShouldCreateEmptyTransaction() {
        Transaction transaction = new Transaction();
        
        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertNull(transaction.getAccountNumber());
        assertNull(transaction.getTxAmount());
        assertNull(transaction.getTxDateTime());
        assertNull(transaction.getTxType());
    }
    
    @Test
    void testTransactionSettersAndGetters_ShouldWorkCorrectly() {
        Transaction transaction = new Transaction();
        Date txDate = new Date();
        UUID txId = UUID.randomUUID();
        
        transaction.setId(txId);
        transaction.setAccountNumber(67890L);
        transaction.setTxAmount(500.0);
        transaction.setTxDateTime(txDate);
        transaction.setTxType("CREDIT");
        
        assertEquals(txId, transaction.getId());
        assertEquals(Long.valueOf(67890L), transaction.getAccountNumber());
        assertEquals(Double.valueOf(500.0), transaction.getTxAmount());
        assertEquals(txDate, transaction.getTxDateTime());
        assertEquals("CREDIT", transaction.getTxType());
    }
    
    @Test
    void testTransactionEqualsAndHashCode_ShouldWorkCorrectly() {
        Date txDate = new Date();
        
        Transaction transaction1 = Transaction.builder()
            .accountNumber(11111L)
            .txAmount(100.0)
            .txDateTime(txDate)
            .txType("DEBIT")
            .build();
        
        Transaction transaction2 = Transaction.builder()
            .accountNumber(11111L)
            .txAmount(100.0)
            .txDateTime(txDate)
            .txType("DEBIT")
            .build();
        
        Transaction transaction3 = Transaction.builder()
            .accountNumber(22222L)
            .txAmount(100.0)
            .txDateTime(txDate)
            .txType("DEBIT")
            .build();
        
        assertEquals(transaction1, transaction2);
        assertNotEquals(transaction1, transaction3);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }
    
    @Test
    void testTransactionToString_ShouldContainAllFields() {
        Transaction transaction = Transaction.builder()
            .accountNumber(99999L)
            .txAmount(750.0)
            .txType("TRANSFER")
            .build();
        
        String transactionString = transaction.toString();
        
        assertNotNull(transactionString);
        assertTrue(transactionString.contains("99999"));
        assertTrue(transactionString.contains("750.0"));
        assertTrue(transactionString.contains("TRANSFER"));
    }
}
