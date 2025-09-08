package com.coding.exercise.bankapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void testAddressBuilder_ShouldCreateAddressWithAllFields() {
        Address address = Address.builder()
            .address1("123 Main Street")
            .address2("Suite 100")
            .city("Test City")
            .state("TC")
            .zip("12345")
            .country("USA")
            .build();
        
        assertNotNull(address);
        assertEquals("123 Main Street", address.getAddress1());
        assertEquals("Suite 100", address.getAddress2());
        assertEquals("Test City", address.getCity());
        assertEquals("TC", address.getState());
        assertEquals("12345", address.getZip());
        assertEquals("USA", address.getCountry());
    }
    
    @Test
    void testAddressNoArgsConstructor_ShouldCreateEmptyAddress() {
        Address address = new Address();
        
        assertNotNull(address);
        assertNull(address.getAddress1());
        assertNull(address.getAddress2());
        assertNull(address.getCity());
        assertNull(address.getState());
        assertNull(address.getZip());
        assertNull(address.getCountry());
    }
    
    @Test
    void testAddressSettersAndGetters_ShouldWorkCorrectly() {
        Address address = new Address();
        
        address.setAddress1("456 Oak Avenue");
        address.setAddress2("Apartment 2B");
        address.setCity("Setter City");
        address.setState("SC");
        address.setZip("67890");
        address.setCountry("Canada");
        
        assertEquals("456 Oak Avenue", address.getAddress1());
        assertEquals("Apartment 2B", address.getAddress2());
        assertEquals("Setter City", address.getCity());
        assertEquals("SC", address.getState());
        assertEquals("67890", address.getZip());
        assertEquals("Canada", address.getCountry());
    }
    
    @Test
    void testAddressEqualsAndHashCode_ShouldWorkCorrectly() {
        Address address1 = Address.builder()
            .address1("789 Pine Road")
            .city("Equals City")
            .state("EC")
            .zip("11111")
            .country("USA")
            .build();
        
        Address address2 = Address.builder()
            .address1("789 Pine Road")
            .city("Equals City")
            .state("EC")
            .zip("11111")
            .country("USA")
            .build();
        
        Address address3 = Address.builder()
            .address1("999 Different Road")
            .city("Equals City")
            .state("EC")
            .zip("11111")
            .country("USA")
            .build();
        
        assertEquals(address1, address2);
        assertNotEquals(address1, address3);
        assertEquals(address1.hashCode(), address2.hashCode());
    }
    
    @Test
    void testAddressToString_ShouldContainAllFields() {
        Address address = Address.builder()
            .address1("321 ToString Street")
            .city("String City")
            .state("SS")
            .zip("99999")
            .country("Mexico")
            .build();
        
        String addressString = address.toString();
        
        assertNotNull(addressString);
        assertTrue(addressString.contains("321 ToString Street"));
        assertTrue(addressString.contains("String City"));
        assertTrue(addressString.contains("SS"));
        assertTrue(addressString.contains("99999"));
        assertTrue(addressString.contains("Mexico"));
    }
}
