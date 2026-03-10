package com.coding.exercise.bankapp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer buildCustomer(Long customerNumber) {
        Address address = Address.builder()
                .address1("123 Main St")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("111-222-3333")
                .workPhone("444-555-6666")
                .build();
        return Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(customerNumber)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .build();
    }

    @Test
    public void save_and_findById() {
        Customer customer = buildCustomer(1001L);
        Customer saved = customerRepository.save(customer);

        assertNotNull(saved.getId());
    }

    @Test
    public void findByCustomerNumber_found() {
        Customer customer = buildCustomer(2001L);
        customerRepository.save(customer);

        Optional<Customer> result = customerRepository.findByCustomerNumber(2001L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals(Long.valueOf(2001L), result.get().getCustomerNumber());
    }

    @Test
    public void findByCustomerNumber_notFound() {
        Optional<Customer> result = customerRepository.findByCustomerNumber(9999L);

        assertFalse(result.isPresent());
    }

    @Test
    public void findAll_returnsAllCustomers() {
        customerRepository.save(buildCustomer(3001L));
        customerRepository.save(buildCustomer(3002L));

        Iterable<Customer> all = customerRepository.findAll();
        int count = 0;
        for (Customer c : all) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void delete_removesCustomer() {
        Customer customer = buildCustomer(4001L);
        Customer saved = customerRepository.save(customer);

        customerRepository.delete(saved);

        Optional<Customer> result = customerRepository.findByCustomerNumber(4001L);
        assertFalse(result.isPresent());
    }

    @Test
    public void update_modifiesCustomer() {
        Customer customer = buildCustomer(5001L);
        Customer saved = customerRepository.save(customer);

        saved.setFirstName("Jane");
        saved.setStatus("Inactive");
        saved.setUpdateDateTime(new Date());
        customerRepository.save(saved);

        Optional<Customer> result = customerRepository.findByCustomerNumber(5001L);
        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Inactive", result.get().getStatus());
    }
}
