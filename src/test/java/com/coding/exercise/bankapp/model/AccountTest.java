package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    void testAccountBuilder() {
        UUID id = UUID.randomUUID();
        Date createDate = new Date();
        Date updateDate = new Date();
        
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .build();
        
        Account account = Account.builder()
                .id(id)
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInfo)
                .createDateTime(createDate)
                .updateDateTime(updateDate)
                .build();
        
        assertEquals(id, account.getId());
        assertEquals(1000001L, account.getAccountNumber());
        assertEquals("Savings", account.getAccountType());
        assertEquals("Active", account.getAccountStatus());
        assertEquals(5000.00, account.getAccountBalance());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals(createDate, account.getCreateDateTime());
        assertEquals(updateDate, account.getUpdateDateTime());
    }

    @Test
    void testAccountSetters() {
        Account account = new Account();
        UUID id = UUID.randomUUID();
        Date createDate = new Date();
        Date updateDate = new Date();
        BankInfo bankInfo = BankInfo.builder().branchName("Branch").build();
        
        account.setId(id);
        account.setAccountNumber(2000002L);
        account.setAccountType("Checking");
        account.setAccountStatus("Inactive");
        account.setAccountBalance(10000.00);
        account.setBankInformation(bankInfo);
        account.setCreateDateTime(createDate);
        account.setUpdateDateTime(updateDate);
        
        assertEquals(id, account.getId());
        assertEquals(2000002L, account.getAccountNumber());
        assertEquals("Checking", account.getAccountType());
        assertEquals("Inactive", account.getAccountStatus());
        assertEquals(10000.00, account.getAccountBalance());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals(createDate, account.getCreateDateTime());
        assertEquals(updateDate, account.getUpdateDateTime());
    }

    @Test
    void testAccountEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        
        Account account1 = Account.builder()
                .id(id)
                .accountNumber(1000001L)
                .accountType("Savings")
                .build();
        
        Account account2 = Account.builder()
                .id(id)
                .accountNumber(1000001L)
                .accountType("Savings")
                .build();
        
        Account account3 = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(2000002L)
                .accountType("Checking")
                .build();
        
        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
        assertNotEquals(account1, account3);
        assertEquals(account1, account1);
        assertNotEquals(account1, null);
        assertNotEquals(account1, "string");
    }

    @Test
    void testAccountToString() {
        Account account = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountBalance(5000.00)
                .build();
        
        String toString = account.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1000001"));
        assertTrue(toString.contains("Savings"));
    }

    @Test
    void testAccountNoArgsConstructor() {
        Account account = new Account();
        assertNotNull(account);
        assertNull(account.getId());
        assertNull(account.getAccountNumber());
    }

    @Test
    void testAccountAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date createDate = new Date();
        Date updateDate = new Date();
        BankInfo bankInfo = BankInfo.builder().build();
        
        Account account = new Account(id, 1000001L, bankInfo, "Active", "Savings", 5000.00, 
                createDate, updateDate);
        
        assertEquals(id, account.getId());
        assertEquals(1000001L, account.getAccountNumber());
        assertEquals("Savings", account.getAccountType());
        assertEquals("Active", account.getAccountStatus());
        assertEquals(5000.00, account.getAccountBalance());
    }
}
