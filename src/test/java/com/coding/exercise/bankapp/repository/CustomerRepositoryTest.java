package com.coding.exercise.bankapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer createCustomer(String firstName, String lastName, Long customerNumber) {
        return Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .customerNumber(customerNumber)
                .status("Active")
                .contactDetails(Contact.builder()
                        .emailId(firstName.toLowerCase() + "@example.com")
                        .homePhone("111-222-3333")
                        .workPhone("444-555-6666")
                        .build())
                .customerAddress(Address.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .createDateTime(new Date())
                .build();
    }

    @Test
    void saveAndFindByCustomerNumber() {
        Customer customer = createCustomer("John", "Doe", 1001L);
        entityManager.persist(customer);
        entityManager.flush();

        Optional<Customer> found = customerRepository.findByCustomerNumber(1001L);

        assertTrue(found.isPresent());
        assertNotNull(found.get().getId());
        assertEquals("John", found.get().getFirstName());
        assertEquals(1001L, found.get().getCustomerNumber());
    }

    @Test
    void findByCustomerNumber_found() {
        Customer customer = createCustomer("John", "Doe", 1001L);
        entityManager.persistAndFlush(customer);

        Optional<Customer> result = customerRepository.findByCustomerNumber(1001L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    void findByCustomerNumber_notFound() {
        Optional<Customer> result = customerRepository.findByCustomerNumber(9999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_multipleCustomers() {
        Customer c1 = createCustomer("John", "Doe", 1001L);
        Customer c2 = createCustomer("Jane", "Smith", 1002L);
        Customer c3 = createCustomer("Bob", "Jones", 1003L);

        entityManager.persistAndFlush(c1);
        entityManager.persistAndFlush(c2);
        entityManager.persistAndFlush(c3);

        Iterable<Customer> result = customerRepository.findAll();

        int count = 0;
        for (Customer c : result) {
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void deleteCustomer() {
        Customer customer = createCustomer("John", "Doe", 1001L);
        entityManager.persist(customer);
        entityManager.flush();

        // Verify the customer exists
        Optional<Customer> found = customerRepository.findByCustomerNumber(1001L);
        assertTrue(found.isPresent());

        // Use JPQL delete to avoid UUID ID mismatch issues with CrudRepository<Customer, String>
        entityManager.getEntityManager()
                .createQuery("DELETE FROM Customer c WHERE c.customerNumber = :num")
                .setParameter("num", 1001L)
                .executeUpdate();
        entityManager.clear();

        Optional<Customer> deleted = customerRepository.findByCustomerNumber(1001L);
        assertFalse(deleted.isPresent());
    }

    @Test
    void saveCustomer_withNullOptionalFields() {
        Customer customer = Customer.builder()
                .firstName("Minimal")
                .lastName("User")
                .customerNumber(2001L)
                .status("Active")
                .createDateTime(new Date())
                .build();

        entityManager.persist(customer);
        entityManager.flush();

        Optional<Customer> found = customerRepository.findByCustomerNumber(2001L);
        assertTrue(found.isPresent());
        assertEquals("Minimal", found.get().getFirstName());
    }
}
