package com.coding.exercise.bankapp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.exercise.bankapp.model.CustomerAccountXRef;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerAccountXRefRepositoryTest {

    @Autowired
    private CustomerAccountXRefRepository xRefRepository;

    @Test
    public void save_and_retrieve() {
        CustomerAccountXRef xRef = CustomerAccountXRef.builder()
                .customerNumber(1001L)
                .accountNumber(5001L)
                .build();

        CustomerAccountXRef saved = xRefRepository.save(xRef);

        assertNotNull(saved.getId());
        assertEquals(Long.valueOf(1001L), saved.getCustomerNumber());
        assertEquals(Long.valueOf(5001L), saved.getAccountNumber());
    }

    @Test
    public void save_multiple_and_findAll() {
        xRefRepository.save(CustomerAccountXRef.builder()
                .customerNumber(1001L)
                .accountNumber(5001L)
                .build());
        xRefRepository.save(CustomerAccountXRef.builder()
                .customerNumber(1001L)
                .accountNumber(5002L)
                .build());
        xRefRepository.save(CustomerAccountXRef.builder()
                .customerNumber(1002L)
                .accountNumber(5003L)
                .build());

        Iterable<CustomerAccountXRef> all = xRefRepository.findAll();
        int count = 0;
        for (CustomerAccountXRef x : all) {
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    public void delete_removesXRef() {
        CustomerAccountXRef xRef = xRefRepository.save(CustomerAccountXRef.builder()
                .customerNumber(1001L)
                .accountNumber(5001L)
                .build());

        xRefRepository.delete(xRef);

        assertEquals(0, xRefRepository.count());
    }

    @Test
    public void count_returnsCorrectNumber() {
        xRefRepository.save(CustomerAccountXRef.builder()
                .customerNumber(1001L)
                .accountNumber(5001L)
                .build());
        xRefRepository.save(CustomerAccountXRef.builder()
                .customerNumber(1002L)
                .accountNumber(5002L)
                .build());

        assertEquals(2, xRefRepository.count());
    }
}
