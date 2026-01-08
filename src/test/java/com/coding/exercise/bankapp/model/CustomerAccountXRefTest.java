package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustomerAccountXRefTest {

    @Test
    void testCustomerAccountXRefBuilder() {
        UUID id = UUID.randomUUID();
        
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
                .id(id)
                .customerNumber(12345L)
                .accountNumber(1000001L)
                .build();
        
        assertEquals(id, xref.getId());
        assertEquals(12345L, xref.getCustomerNumber());
        assertEquals(1000001L, xref.getAccountNumber());
    }

    @Test
    void testCustomerAccountXRefSetters() {
        CustomerAccountXRef xref = new CustomerAccountXRef();
        UUID id = UUID.randomUUID();
        
        xref.setId(id);
        xref.setCustomerNumber(67890L);
        xref.setAccountNumber(2000002L);
        
        assertEquals(id, xref.getId());
        assertEquals(67890L, xref.getCustomerNumber());
        assertEquals(2000002L, xref.getAccountNumber());
    }

    @Test
    void testCustomerAccountXRefEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
                .id(id)
                .customerNumber(12345L)
                .accountNumber(1000001L)
                .build();
        
        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
                .id(id)
                .customerNumber(12345L)
                .accountNumber(1000001L)
                .build();
        
        CustomerAccountXRef xref3 = CustomerAccountXRef.builder()
                .id(UUID.randomUUID())
                .customerNumber(67890L)
                .accountNumber(2000002L)
                .build();
        
        assertEquals(xref1, xref2);
        assertEquals(xref1.hashCode(), xref2.hashCode());
        assertNotEquals(xref1, xref3);
        assertEquals(xref1, xref1);
        assertNotEquals(xref1, null);
        assertNotEquals(xref1, "string");
    }

    @Test
    void testCustomerAccountXRefToString() {
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
                .customerNumber(12345L)
                .accountNumber(1000001L)
                .build();
        
        String toString = xref.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("12345"));
        assertTrue(toString.contains("1000001"));
    }

    @Test
    void testCustomerAccountXRefNoArgsConstructor() {
        CustomerAccountXRef xref = new CustomerAccountXRef();
        assertNotNull(xref);
        assertNull(xref.getId());
        assertNull(xref.getCustomerNumber());
    }

    @Test
    void testCustomerAccountXRefAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        
        CustomerAccountXRef xref = new CustomerAccountXRef(id, 1000001L, 12345L);
        
        assertEquals(id, xref.getId());
        assertEquals(12345L, xref.getCustomerNumber());
        assertEquals(1000001L, xref.getAccountNumber());
    }
}
