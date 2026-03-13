package com.coding.exercise.bankapp.repository;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer sampleCustomer;

    @Before
    public void setUp() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        Contact contact = Contact.builder()
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        sampleCustomer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .build();
    }

    @Test
    public void testSaveAndFindByCustomerNumber() {
        entityManager.persistAndFlush(sampleCustomer);

        Optional<Customer> found = customerRepository.findByCustomerNumber(12345L);

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
        assertEquals(Long.valueOf(12345L), found.get().getCustomerNumber());
    }

    @Test
    public void testFindByCustomerNumber_NotFound() {
        Optional<Customer> found = customerRepository.findByCustomerNumber(99999L);

        assertFalse(found.isPresent());
    }

    @Test
    public void testSaveCustomer() {
        Customer saved = customerRepository.save(sampleCustomer);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
    }

    @Test
    public void testFindAllCustomers() {
        entityManager.persistAndFlush(sampleCustomer);

        Customer customer2 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(67890L)
                .status("Active")
                .createDateTime(new Date())
                .build();
        entityManager.persistAndFlush(customer2);

        Iterable<Customer> customers = customerRepository.findAll();

        int count = 0;
        for (Customer c : customers) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testDeleteCustomer() {
        Customer persisted = entityManager.persistAndFlush(sampleCustomer);

        customerRepository.delete(persisted);
        entityManager.flush();

        Optional<Customer> found = customerRepository.findByCustomerNumber(12345L);
        assertFalse(found.isPresent());
    }

    @Test
    public void testUpdateCustomer() {
        Customer persisted = entityManager.persistAndFlush(sampleCustomer);

        persisted.setFirstName("UpdatedName");
        persisted.setUpdateDateTime(new Date());
        customerRepository.save(persisted);
        entityManager.flush();

        Optional<Customer> found = customerRepository.findByCustomerNumber(12345L);
        assertTrue(found.isPresent());
        assertEquals("UpdatedName", found.get().getFirstName());
    }
}
