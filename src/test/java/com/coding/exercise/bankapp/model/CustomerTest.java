package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void testCustomerBuilder() {
        UUID id = UUID.randomUUID();
        Date createDate = new Date();
        Date updateDate = new Date();
        
        Address address = Address.builder()
                .address1("123 Main St")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();
        
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();
        
        Customer customer = Customer.builder()
                .id(id)
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(createDate)
                .updateDateTime(updateDate)
                .build();
        
        assertEquals(id, customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals(12345L, customer.getCustomerNumber());
        assertEquals("Active", customer.getStatus());
        assertEquals(address, customer.getCustomerAddress());
        assertEquals(contact, customer.getContactDetails());
        assertEquals(createDate, customer.getCreateDateTime());
        assertEquals(updateDate, customer.getUpdateDateTime());
    }

    @Test
    void testCustomerSetters() {
        Customer customer = new Customer();
        UUID id = UUID.randomUUID();
        Date createDate = new Date();
        Date updateDate = new Date();
        
        Address address = Address.builder().address1("456 Oak Ave").build();
        Contact contact = Contact.builder().emailId("new@example.com").build();
        
        customer.setId(id);
        customer.setFirstName("Jane");
        customer.setMiddleName("A");
        customer.setLastName("Smith");
        customer.setCustomerNumber(67890L);
        customer.setStatus("Inactive");
        customer.setCustomerAddress(address);
        customer.setContactDetails(contact);
        customer.setCreateDateTime(createDate);
        customer.setUpdateDateTime(updateDate);
        
        assertEquals(id, customer.getId());
        assertEquals("Jane", customer.getFirstName());
        assertEquals("A", customer.getMiddleName());
        assertEquals("Smith", customer.getLastName());
        assertEquals(67890L, customer.getCustomerNumber());
        assertEquals("Inactive", customer.getStatus());
        assertEquals(address, customer.getCustomerAddress());
        assertEquals(contact, customer.getContactDetails());
        assertEquals(createDate, customer.getCreateDateTime());
        assertEquals(updateDate, customer.getUpdateDateTime());
    }

    @Test
    void testCustomerEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        
        Customer customer1 = Customer.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .build();
        
        Customer customer2 = Customer.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .build();
        
        Customer customer3 = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(67890L)
                .build();
        
        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
        assertNotEquals(customer1, customer3);
        assertEquals(customer1, customer1);
        assertNotEquals(customer1, null);
        assertNotEquals(customer1, "string");
    }

    @Test
    void testCustomerToString() {
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .build();
        
        String toString = customer.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("12345"));
    }

    @Test
    void testCustomerNoArgsConstructor() {
        Customer customer = new Customer();
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getFirstName());
    }

    @Test
    void testCustomerAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date createDate = new Date();
        Date updateDate = new Date();
        Address address = Address.builder().build();
        Contact contact = Contact.builder().build();
        
        Customer customer = new Customer(id, "John", "Doe", "M", 12345L, "Active", 
                address, contact, createDate, updateDate);
        
        assertEquals(id, customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals(12345L, customer.getCustomerNumber());
        assertEquals("Active", customer.getStatus());
    }
}
