package com.coding.exercise.bankapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.UUID;

class CustomerTest {

    @Test
    void testCustomerBuilder_ShouldCreateCustomerWithAllFields() {
        Contact contact = Contact.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();
        
        Address address = Address.builder()
            .address1("123 Main St")
            .address2("Apt 4B")
            .city("Test City")
            .state("TS")
            .zip("12345")
            .country("USA")
            .build();
        
        Date createDate = new Date();
        Date updateDate = new Date();
        UUID customerId = UUID.randomUUID();
        
        Customer customer = Customer.builder()
            .id(customerId)
            .customerNumber(1001L)
            .firstName("John")
            .middleName("M")
            .lastName("Doe")
            .contactDetails(contact)
            .customerAddress(address)
            .status("ACTIVE")
            .createDateTime(createDate)
            .updateDateTime(updateDate)
            .build();
        
        assertNotNull(customer);
        assertEquals(customerId, customer.getId());
        assertEquals(Long.valueOf(1001L), customer.getCustomerNumber());
        assertEquals("John", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals(contact, customer.getContactDetails());
        assertEquals(address, customer.getCustomerAddress());
        assertEquals("ACTIVE", customer.getStatus());
        assertEquals(createDate, customer.getCreateDateTime());
        assertEquals(updateDate, customer.getUpdateDateTime());
    }
    
    @Test
    void testCustomerNoArgsConstructor_ShouldCreateEmptyCustomer() {
        Customer customer = new Customer();
        
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getCustomerNumber());
        assertNull(customer.getFirstName());
        assertNull(customer.getMiddleName());
        assertNull(customer.getLastName());
        assertNull(customer.getContactDetails());
        assertNull(customer.getCustomerAddress());
        assertNull(customer.getStatus());
        assertNull(customer.getCreateDateTime());
        assertNull(customer.getUpdateDateTime());
    }
    
    @Test
    void testCustomerSettersAndGetters_ShouldWorkCorrectly() {
        Customer customer = new Customer();
        Contact contact = Contact.builder()
            .emailId("setter@example.com")
            .homePhone("555-1234")
            .workPhone("555-5678")
            .build();
        
        Address address = Address.builder()
            .address1("456 Oak Ave")
            .city("Setter City")
            .state("SC")
            .zip("67890")
            .country("USA")
            .build();
        
        Date createDate = new Date();
        Date updateDate = new Date();
        UUID customerId = UUID.randomUUID();
        
        customer.setId(customerId);
        customer.setCustomerNumber(2002L);
        customer.setFirstName("Jane");
        customer.setMiddleName("A");
        customer.setLastName("Smith");
        customer.setContactDetails(contact);
        customer.setCustomerAddress(address);
        customer.setStatus("INACTIVE");
        customer.setCreateDateTime(createDate);
        customer.setUpdateDateTime(updateDate);
        
        assertEquals(customerId, customer.getId());
        assertEquals(Long.valueOf(2002L), customer.getCustomerNumber());
        assertEquals("Jane", customer.getFirstName());
        assertEquals("A", customer.getMiddleName());
        assertEquals("Smith", customer.getLastName());
        assertEquals(contact, customer.getContactDetails());
        assertEquals(address, customer.getCustomerAddress());
        assertEquals("INACTIVE", customer.getStatus());
        assertEquals(createDate, customer.getCreateDateTime());
        assertEquals(updateDate, customer.getUpdateDateTime());
    }
    
    @Test
    void testCustomerEqualsAndHashCode_ShouldWorkCorrectly() {
        Customer customer1 = Customer.builder()
            .customerNumber(3003L)
            .firstName("Bob")
            .lastName("Johnson")
            .status("ACTIVE")
            .build();
        
        Customer customer2 = Customer.builder()
            .customerNumber(3003L)
            .firstName("Bob")
            .lastName("Johnson")
            .status("ACTIVE")
            .build();
        
        Customer customer3 = Customer.builder()
            .customerNumber(4004L)
            .firstName("Bob")
            .lastName("Johnson")
            .status("ACTIVE")
            .build();
        
        assertEquals(customer1, customer2);
        assertNotEquals(customer1, customer3);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }
    
    @Test
    void testCustomerToString_ShouldContainAllFields() {
        Customer customer = Customer.builder()
            .customerNumber(5005L)
            .firstName("Alice")
            .lastName("Brown")
            .status("PENDING")
            .build();
        
        String customerString = customer.toString();
        
        assertNotNull(customerString);
        assertTrue(customerString.contains("5005"));
        assertTrue(customerString.contains("Alice"));
        assertTrue(customerString.contains("Brown"));
        assertTrue(customerString.contains("PENDING"));
    }
}
