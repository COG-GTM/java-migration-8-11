package com.coding.exercise.bankapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.UUID;

class AccountTest {

    @Test
    void testAccountBuilder_ShouldCreateAccountWithAllFields() {
        BankInfo bankInfo = BankInfo.builder()
            .branchName("Main Branch")
            .branchCode(123)
            .routingNumber(456789)
            .build();
        
        Date createDate = new Date();
        Date updateDate = new Date();
        UUID accountId = UUID.randomUUID();
        
        Account account = Account.builder()
            .id(accountId)
            .accountNumber(12345L)
            .bankInformation(bankInfo)
            .accountStatus("ACTIVE")
            .accountType("SAVINGS")
            .accountBalance(1000.0)
            .createDateTime(createDate)
            .updateDateTime(updateDate)
            .build();
        
        assertNotNull(account);
        assertEquals(accountId, account.getId());
        assertEquals(Long.valueOf(12345L), account.getAccountNumber());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals("ACTIVE", account.getAccountStatus());
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(Double.valueOf(1000.0), account.getAccountBalance());
        assertEquals(createDate, account.getCreateDateTime());
        assertEquals(updateDate, account.getUpdateDateTime());
    }
    
    @Test
    void testAccountNoArgsConstructor_ShouldCreateEmptyAccount() {
        Account account = new Account();
        
        assertNotNull(account);
        assertNull(account.getId());
        assertNull(account.getAccountNumber());
        assertNull(account.getBankInformation());
        assertNull(account.getAccountStatus());
        assertNull(account.getAccountType());
        assertNull(account.getAccountBalance());
        assertNull(account.getCreateDateTime());
        assertNull(account.getUpdateDateTime());
    }
    
    @Test
    void testAccountAllArgsConstructor_ShouldCreateAccountWithAllParameters() {
        BankInfo bankInfo = BankInfo.builder()
            .branchName("Test Branch")
            .branchCode(999)
            .routingNumber(111222)
            .build();
        
        Date createDate = new Date();
        Date updateDate = new Date();
        UUID accountId = UUID.randomUUID();
        
        Account account = new Account(accountId, 67890L, bankInfo, "INACTIVE", "CHECKING", 2500.0, createDate, updateDate);
        
        assertNotNull(account);
        assertEquals(accountId, account.getId());
        assertEquals(Long.valueOf(67890L), account.getAccountNumber());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals("INACTIVE", account.getAccountStatus());
        assertEquals("CHECKING", account.getAccountType());
        assertEquals(Double.valueOf(2500.0), account.getAccountBalance());
        assertEquals(createDate, account.getCreateDateTime());
        assertEquals(updateDate, account.getUpdateDateTime());
    }
    
    @Test
    void testAccountSettersAndGetters_ShouldWorkCorrectly() {
        Account account = new Account();
        BankInfo bankInfo = BankInfo.builder()
            .branchName("Setter Branch")
            .branchCode(777)
            .routingNumber(888999)
            .build();
        
        Date createDate = new Date();
        Date updateDate = new Date();
        UUID accountId = UUID.randomUUID();
        
        account.setId(accountId);
        account.setAccountNumber(55555L);
        account.setBankInformation(bankInfo);
        account.setAccountStatus("PENDING");
        account.setAccountType("BUSINESS");
        account.setAccountBalance(5000.0);
        account.setCreateDateTime(createDate);
        account.setUpdateDateTime(updateDate);
        
        assertEquals(accountId, account.getId());
        assertEquals(Long.valueOf(55555L), account.getAccountNumber());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals("PENDING", account.getAccountStatus());
        assertEquals("BUSINESS", account.getAccountType());
        assertEquals(Double.valueOf(5000.0), account.getAccountBalance());
        assertEquals(createDate, account.getCreateDateTime());
        assertEquals(updateDate, account.getUpdateDateTime());
    }
    
    @Test
    void testAccountEqualsAndHashCode_ShouldWorkCorrectly() {
        BankInfo bankInfo = BankInfo.builder()
            .branchName("Equals Branch")
            .branchCode(333)
            .routingNumber(444555)
            .build();
        
        Account account1 = Account.builder()
            .accountNumber(11111L)
            .bankInformation(bankInfo)
            .accountStatus("ACTIVE")
            .accountType("SAVINGS")
            .accountBalance(1500.0)
            .build();
        
        Account account2 = Account.builder()
            .accountNumber(11111L)
            .bankInformation(bankInfo)
            .accountStatus("ACTIVE")
            .accountType("SAVINGS")
            .accountBalance(1500.0)
            .build();
        
        Account account3 = Account.builder()
            .accountNumber(22222L)
            .bankInformation(bankInfo)
            .accountStatus("ACTIVE")
            .accountType("SAVINGS")
            .accountBalance(1500.0)
            .build();
        
        assertEquals(account1, account2);
        assertNotEquals(account1, account3);
        assertEquals(account1.hashCode(), account2.hashCode());
        assertNotEquals(account1.hashCode(), account3.hashCode());
    }
    
    @Test
    void testAccountToString_ShouldContainAllFields() {
        Account account = Account.builder()
            .accountNumber(99999L)
            .accountStatus("CLOSED")
            .accountType("LOAN")
            .accountBalance(0.0)
            .build();
        
        String accountString = account.toString();
        
        assertNotNull(accountString);
        assertTrue(accountString.contains("99999"));
        assertTrue(accountString.contains("CLOSED"));
        assertTrue(accountString.contains("LOAN"));
        assertTrue(accountString.contains("0.0"));
    }
}
