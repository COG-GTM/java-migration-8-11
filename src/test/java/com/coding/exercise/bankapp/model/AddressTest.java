package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void testAddressBuilder() {
        UUID id = UUID.randomUUID();
        
        Address address = Address.builder()
                .id(id)
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();
        
        assertEquals(id, address.getId());
        assertEquals("123 Main St", address.getAddress1());
        assertEquals("Apt 4", address.getAddress2());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZip());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void testAddressSetters() {
        Address address = new Address();
        UUID id = UUID.randomUUID();
        
        address.setId(id);
        address.setAddress1("456 Oak Ave");
        address.setAddress2("Suite 100");
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZip("90001");
        address.setCountry("USA");
        
        assertEquals(id, address.getId());
        assertEquals("456 Oak Ave", address.getAddress1());
        assertEquals("Suite 100", address.getAddress2());
        assertEquals("Los Angeles", address.getCity());
        assertEquals("CA", address.getState());
        assertEquals("90001", address.getZip());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void testAddressEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        
        Address address1 = Address.builder()
                .id(id)
                .address1("123 Main St")
                .city("New York")
                .build();
        
        Address address2 = Address.builder()
                .id(id)
                .address1("123 Main St")
                .city("New York")
                .build();
        
        Address address3 = Address.builder()
                .id(UUID.randomUUID())
                .address1("456 Oak Ave")
                .city("Los Angeles")
                .build();
        
        assertEquals(address1, address2);
        assertEquals(address1.hashCode(), address2.hashCode());
        assertNotEquals(address1, address3);
        assertEquals(address1, address1);
        assertNotEquals(address1, null);
        assertNotEquals(address1, "string");
    }

    @Test
    void testAddressToString() {
        Address address = Address.builder()
                .address1("123 Main St")
                .city("New York")
                .state("NY")
                .build();
        
        String toString = address.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("123 Main St"));
        assertTrue(toString.contains("New York"));
    }

    @Test
    void testAddressNoArgsConstructor() {
        Address address = new Address();
        assertNotNull(address);
        assertNull(address.getId());
        assertNull(address.getAddress1());
    }

    @Test
    void testAddressAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        
        Address address = new Address(id, "123 Main St", "Apt 4", "New York", "NY", "10001", "USA");
        
        assertEquals(id, address.getId());
        assertEquals("123 Main St", address.getAddress1());
        assertEquals("Apt 4", address.getAddress2());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZip());
        assertEquals("USA", address.getCountry());
    }
}
