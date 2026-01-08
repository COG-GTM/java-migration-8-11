package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class BankInfoTest {

    @Test
    void testBankInfoBuilder() {
        UUID id = UUID.randomUUID();
        
        Address branchAddress = Address.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .state("IL")
                .build();
        
        BankInfo bankInfo = BankInfo.builder()
                .id(id)
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();
        
        assertEquals(id, bankInfo.getId());
        assertEquals("Main Branch", bankInfo.getBranchName());
        assertEquals(1001, bankInfo.getBranchCode());
        assertEquals(123456789, bankInfo.getRoutingNumber());
        assertEquals(branchAddress, bankInfo.getBranchAddress());
    }

    @Test
    void testBankInfoSetters() {
        BankInfo bankInfo = new BankInfo();
        UUID id = UUID.randomUUID();
        Address branchAddress = Address.builder().address1("456 Finance Ave").build();
        
        bankInfo.setId(id);
        bankInfo.setBranchName("Downtown Branch");
        bankInfo.setBranchCode(2002);
        bankInfo.setRoutingNumber(987654321);
        bankInfo.setBranchAddress(branchAddress);
        
        assertEquals(id, bankInfo.getId());
        assertEquals("Downtown Branch", bankInfo.getBranchName());
        assertEquals(2002, bankInfo.getBranchCode());
        assertEquals(987654321, bankInfo.getRoutingNumber());
        assertEquals(branchAddress, bankInfo.getBranchAddress());
    }

    @Test
    void testBankInfoEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        
        BankInfo bankInfo1 = BankInfo.builder()
                .id(id)
                .branchName("Main Branch")
                .branchCode(1001)
                .build();
        
        BankInfo bankInfo2 = BankInfo.builder()
                .id(id)
                .branchName("Main Branch")
                .branchCode(1001)
                .build();
        
        BankInfo bankInfo3 = BankInfo.builder()
                .id(UUID.randomUUID())
                .branchName("Other Branch")
                .branchCode(2002)
                .build();
        
        assertEquals(bankInfo1, bankInfo2);
        assertEquals(bankInfo1.hashCode(), bankInfo2.hashCode());
        assertNotEquals(bankInfo1, bankInfo3);
        assertEquals(bankInfo1, bankInfo1);
        assertNotEquals(bankInfo1, null);
        assertNotEquals(bankInfo1, "string");
    }

    @Test
    void testBankInfoToString() {
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .build();
        
        String toString = bankInfo.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Main Branch"));
        assertTrue(toString.contains("1001"));
    }

    @Test
    void testBankInfoNoArgsConstructor() {
        BankInfo bankInfo = new BankInfo();
        assertNotNull(bankInfo);
        assertNull(bankInfo.getId());
        assertNull(bankInfo.getBranchName());
    }

    @Test
    void testBankInfoAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Address branchAddress = Address.builder().build();
        
        BankInfo bankInfo = new BankInfo(id, "Main Branch", 1001, branchAddress, 123456789);
        
        assertEquals(id, bankInfo.getId());
        assertEquals("Main Branch", bankInfo.getBranchName());
        assertEquals(1001, bankInfo.getBranchCode());
        assertEquals(123456789, bankInfo.getRoutingNumber());
        assertEquals(branchAddress, bankInfo.getBranchAddress());
    }
}
