package com.coding.exercise.bankapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankInfoTest {

    @Test
    void testBankInfoBuilder_ShouldCreateBankInfoWithAllFields() {
        Address address = Address.builder()
            .address1("100 Bank St")
            .city("Financial City")
            .state("FC")
            .zip("54321")
            .country("USA")
            .build();
        
        BankInfo bankInfo = BankInfo.builder()
            .branchName("Downtown Branch")
            .branchCode(101)
            .branchAddress(address)
            .routingNumber(987654321)
            .build();
        
        assertNotNull(bankInfo);
        assertEquals("Downtown Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(101), bankInfo.getBranchCode());
        assertEquals(address, bankInfo.getBranchAddress());
        assertEquals(Integer.valueOf(987654321), bankInfo.getRoutingNumber());
    }
    
    @Test
    void testBankInfoNoArgsConstructor_ShouldCreateEmptyBankInfo() {
        BankInfo bankInfo = new BankInfo();
        
        assertNotNull(bankInfo);
        assertNull(bankInfo.getBranchName());
        assertNull(bankInfo.getBranchCode());
        assertNull(bankInfo.getBranchAddress());
        assertNull(bankInfo.getRoutingNumber());
    }
    
    @Test
    void testBankInfoSettersAndGetters_ShouldWorkCorrectly() {
        BankInfo bankInfo = new BankInfo();
        Address address = Address.builder()
            .address1("200 Finance Ave")
            .city("Banking City")
            .state("BC")
            .zip("98765")
            .country("USA")
            .build();
        
        bankInfo.setBranchName("Uptown Branch");
        bankInfo.setBranchCode(202);
        bankInfo.setBranchAddress(address);
        bankInfo.setRoutingNumber(123456789);
        
        assertEquals("Uptown Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(202), bankInfo.getBranchCode());
        assertEquals(address, bankInfo.getBranchAddress());
        assertEquals(Integer.valueOf(123456789), bankInfo.getRoutingNumber());
    }
    
    @Test
    void testBankInfoEqualsAndHashCode_ShouldWorkCorrectly() {
        BankInfo bankInfo1 = BankInfo.builder()
            .branchName("Test Branch")
            .branchCode(303)
            .routingNumber(111222333)
            .build();
        
        BankInfo bankInfo2 = BankInfo.builder()
            .branchName("Test Branch")
            .branchCode(303)
            .routingNumber(111222333)
            .build();
        
        BankInfo bankInfo3 = BankInfo.builder()
            .branchName("Different Branch")
            .branchCode(303)
            .routingNumber(111222333)
            .build();
        
        assertEquals(bankInfo1, bankInfo2);
        assertNotEquals(bankInfo1, bankInfo3);
        assertEquals(bankInfo1.hashCode(), bankInfo2.hashCode());
    }
    
    @Test
    void testBankInfoToString_ShouldContainAllFields() {
        BankInfo bankInfo = BankInfo.builder()
            .branchName("String Test Branch")
            .branchCode(404)
            .routingNumber(444555666)
            .build();
        
        String bankInfoString = bankInfo.toString();
        
        assertNotNull(bankInfoString);
        assertTrue(bankInfoString.contains("String Test Branch"));
        assertTrue(bankInfoString.contains("404"));
        assertTrue(bankInfoString.contains("444555666"));
    }
}
