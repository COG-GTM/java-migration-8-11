package com.coding.exercise.bankapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testFindByCustomerNumber_found() {
        Address address = Address.builder()
                .address1("123 Main St")
                .city("Springfield")
                .state("IL")
                .zip("62704")
                .country("US")
                .build();

        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .build();

        entityManager.persistAndFlush(customer);

        Optional<Customer> result = customerRepository.findByCustomerNumber(1001L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals(Long.valueOf(1001L), result.get().getCustomerNumber());
    }

    @Test
    public void testFindByCustomerNumber_notFound() {
        Optional<Customer> result = customerRepository.findByCustomerNumber(99999L);

        assertFalse(result.isPresent());
    }
}
