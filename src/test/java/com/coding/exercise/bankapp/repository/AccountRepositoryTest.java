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

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.BankInfo;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Account createAccount(Long accountNumber, String type, Double balance) {
        return Account.builder()
                .accountNumber(accountNumber)
                .accountType(type)
                .accountStatus("Active")
                .accountBalance(balance)
                .bankInformation(BankInfo.builder()
                        .branchName("Main Branch")
                        .branchCode(101)
                        .routingNumber(12345)
                        .branchAddress(Address.builder()
                                .address1("100 Bank St")
                                .city("Chicago")
                                .state("IL")
                                .zip("60601")
                                .country("US")
                                .build())
                        .build())
                .createDateTime(new Date())
                .build();
    }

    @Test
    void saveAndFindByAccountNumber() {
        Account account = createAccount(5001L, "SAVINGS", 1000.0);
        entityManager.persist(account);
        entityManager.flush();

        Optional<Account> found = accountRepository.findByAccountNumber(5001L);

        assertTrue(found.isPresent());
        assertNotNull(found.get().getId());
        assertEquals(5001L, found.get().getAccountNumber());
        assertEquals("SAVINGS", found.get().getAccountType());
        assertEquals(1000.0, found.get().getAccountBalance());
    }

    @Test
    void findByAccountNumber_found() {
        Account account = createAccount(5001L, "SAVINGS", 1000.0);
        entityManager.persist(account);
        entityManager.flush();

        Optional<Account> result = accountRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(5001L, result.get().getAccountNumber());
        assertEquals("Active", result.get().getAccountStatus());
    }

    @Test
    void findByAccountNumber_notFound() {
        Optional<Account> result = accountRepository.findByAccountNumber(9999L);

        assertFalse(result.isPresent());
    }

    @Test
    void saveAccount_withBankInformation() {
        Account account = createAccount(5001L, "CHECKING", 2500.50);
        entityManager.persistAndFlush(account);

        Optional<Account> result = accountRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getBankInformation());
        assertEquals("Main Branch", result.get().getBankInformation().getBranchName());
        assertEquals(101, result.get().getBankInformation().getBranchCode());
        assertEquals(12345, result.get().getBankInformation().getRoutingNumber());
    }

    @Test
    void updateAccountBalance() {
        Account account = createAccount(5001L, "SAVINGS", 1000.0);
        entityManager.persist(account);
        entityManager.flush();

        // Verify initial balance via JPQL to avoid entity graph issues
        Double initialBalance = entityManager.getEntityManager()
                .createQuery("SELECT a.accountBalance FROM Account a WHERE a.accountNumber = :num", Double.class)
                .setParameter("num", 5001L)
                .getSingleResult();
        assertEquals(1000.0, initialBalance);

        // Update balance via JPQL
        entityManager.getEntityManager()
                .createQuery("UPDATE Account a SET a.accountBalance = :balance WHERE a.accountNumber = :num")
                .setParameter("balance", 1500.0)
                .setParameter("num", 5001L)
                .executeUpdate();

        // Verify updated balance via JPQL
        Double updatedBalance = entityManager.getEntityManager()
                .createQuery("SELECT a.accountBalance FROM Account a WHERE a.accountNumber = :num", Double.class)
                .setParameter("num", 5001L)
                .getSingleResult();
        assertEquals(1500.0, updatedBalance);
    }

    @Test
    void findAll_multipleAccounts() {
        Account a1 = createAccount(5001L, "SAVINGS", 1000.0);
        Account a2 = createAccount(5002L, "CHECKING", 2000.0);
        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.flush();

        Iterable<Account> result = accountRepository.findAll();

        int count = 0;
        for (Account a : result) {
            count++;
        }
        assertEquals(2, count);
    }
}
