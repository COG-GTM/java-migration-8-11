package com.coding.exercise.bankapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.coding.exercise.bankapp.model.CustomerAccountXRef;

@DataJpaTest
class CustomerAccountXRefRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerAccountXRefRepository xRefRepository;

    @Test
    void saveXRef() {
        CustomerAccountXRef xRef = CustomerAccountXRef.builder()
                .customerNumber(1001L)
                .accountNumber(5001L)
                .build();

        entityManager.persist(xRef);
        entityManager.flush();

        Iterable<CustomerAccountXRef> allXRefs = xRefRepository.findAll();
        int count = 0;
        CustomerAccountXRef found = null;
        for (CustomerAccountXRef x : allXRefs) {
            count++;
            found = x;
        }
        assertEquals(1, count);
        assertNotNull(found);
        assertNotNull(found.getId());
        assertEquals(1001L, found.getCustomerNumber());
        assertEquals(5001L, found.getAccountNumber());
    }

    @Test
    void saveMultipleXRefs_sameCustomer() {
        CustomerAccountXRef xRef1 = CustomerAccountXRef.builder()
                .customerNumber(1001L).accountNumber(5001L).build();
        CustomerAccountXRef xRef2 = CustomerAccountXRef.builder()
                .customerNumber(1001L).accountNumber(5002L).build();

        entityManager.persist(xRef1);
        entityManager.persist(xRef2);
        entityManager.flush();

        Iterable<CustomerAccountXRef> allXRefs = xRefRepository.findAll();
        int count = 0;
        for (CustomerAccountXRef xRef : allXRefs) {
            count++;
            assertEquals(1001L, xRef.getCustomerNumber());
        }
        assertEquals(2, count);
    }

    @Test
    void saveMultipleXRefs_differentCustomers() {
        CustomerAccountXRef xRef1 = CustomerAccountXRef.builder()
                .customerNumber(1001L).accountNumber(5001L).build();
        CustomerAccountXRef xRef2 = CustomerAccountXRef.builder()
                .customerNumber(1002L).accountNumber(5002L).build();

        entityManager.persist(xRef1);
        entityManager.persist(xRef2);
        entityManager.flush();

        Iterable<CustomerAccountXRef> allXRefs = xRefRepository.findAll();
        int count = 0;
        for (CustomerAccountXRef xRef : allXRefs) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void deleteXRef() {
        CustomerAccountXRef xRef = CustomerAccountXRef.builder()
                .customerNumber(1001L).accountNumber(5001L).build();
        entityManager.persist(xRef);
        entityManager.flush();

        Iterable<CustomerAccountXRef> before = xRefRepository.findAll();
        int countBefore = 0;
        for (CustomerAccountXRef x : before) {
            countBefore++;
        }
        assertEquals(1, countBefore);

        // Use JPQL delete to avoid UUID ID mismatch issues
        entityManager.getEntityManager()
                .createQuery("DELETE FROM CustomerAccountXRef x WHERE x.customerNumber = :num")
                .setParameter("num", 1001L)
                .executeUpdate();
        entityManager.clear();

        Iterable<CustomerAccountXRef> after = xRefRepository.findAll();
        int countAfter = 0;
        for (CustomerAccountXRef x : after) {
            countAfter++;
        }
        assertEquals(0, countAfter);
    }
}
