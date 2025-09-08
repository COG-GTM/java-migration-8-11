package com.coding.exercise.bankapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

class CustomerAccountXRefTest {

    @Test
    void testCustomerAccountXRefBuilder_ShouldCreateXRefWithAllFields() {
        UUID xrefId = UUID.randomUUID();
        
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
            .id(xrefId)
            .customerNumber(1001L)
            .accountNumber(12345L)
            .build();
        
        assertNotNull(xref);
        assertEquals(xrefId, xref.getId());
        assertEquals(Long.valueOf(1001L), xref.getCustomerNumber());
        assertEquals(Long.valueOf(12345L), xref.getAccountNumber());
    }
    
    @Test
    void testCustomerAccountXRefNoArgsConstructor_ShouldCreateEmptyXRef() {
        CustomerAccountXRef xref = new CustomerAccountXRef();
        
        assertNotNull(xref);
        assertNull(xref.getId());
        assertNull(xref.getCustomerNumber());
        assertNull(xref.getAccountNumber());
    }
    
    @Test
    void testCustomerAccountXRefSettersAndGetters_ShouldWorkCorrectly() {
        CustomerAccountXRef xref = new CustomerAccountXRef();
        UUID xrefId = UUID.randomUUID();
        
        xref.setId(xrefId);
        xref.setCustomerNumber(2002L);
        xref.setAccountNumber(67890L);
        
        assertEquals(xrefId, xref.getId());
        assertEquals(Long.valueOf(2002L), xref.getCustomerNumber());
        assertEquals(Long.valueOf(67890L), xref.getAccountNumber());
    }
    
    @Test
    void testCustomerAccountXRefEqualsAndHashCode_ShouldWorkCorrectly() {
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
            .customerNumber(3003L)
            .accountNumber(11111L)
            .build();
        
        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
            .customerNumber(3003L)
            .accountNumber(11111L)
            .build();
        
        CustomerAccountXRef xref3 = CustomerAccountXRef.builder()
            .customerNumber(4004L)
            .accountNumber(11111L)
            .build();
        
        assertEquals(xref1, xref2);
        assertNotEquals(xref1, xref3);
        assertEquals(xref1.hashCode(), xref2.hashCode());
    }
    
    @Test
    void testCustomerAccountXRefToString_ShouldContainAllFields() {
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
            .customerNumber(5005L)
            .accountNumber(99999L)
            .build();
        
        String xrefString = xref.toString();
        
        assertNotNull(xrefString);
        assertTrue(xrefString.contains("5005"));
        assertTrue(xrefString.contains("99999"));
    }
}
