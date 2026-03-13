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

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.BankInfo;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Account sampleAccount;

    @Before
    public void setUp() {
        Address branchAddress = Address.builder()
                .address1("456 Bank St")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        sampleAccount = Account.builder()
                .accountNumber(100001L)
                .bankInformation(bankInfo)
                .accountStatus("Active")
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .createDateTime(new Date())
                .build();
    }

    @Test
    public void testSaveAndFindByAccountNumber() {
        entityManager.persistAndFlush(sampleAccount);

        Optional<Account> found = accountRepository.findByAccountNumber(100001L);

        assertTrue(found.isPresent());
        assertEquals(Long.valueOf(100001L), found.get().getAccountNumber());
        assertEquals("SAVINGS", found.get().getAccountType());
        assertEquals(5000.0, found.get().getAccountBalance(), 0.01);
    }

    @Test
    public void testFindByAccountNumber_NotFound() {
        Optional<Account> found = accountRepository.findByAccountNumber(99999L);

        assertFalse(found.isPresent());
    }

    @Test
    public void testSaveAccount() {
        Account saved = accountRepository.save(sampleAccount);

        assertNotNull(saved.getId());
        assertEquals(Long.valueOf(100001L), saved.getAccountNumber());
    }

    @Test
    public void testUpdateAccountBalance() {
        Account persisted = entityManager.persistAndFlush(sampleAccount);

        persisted.setAccountBalance(7500.0);
        persisted.setUpdateDateTime(new Date());
        accountRepository.save(persisted);
        entityManager.flush();

        Optional<Account> found = accountRepository.findByAccountNumber(100001L);
        assertTrue(found.isPresent());
        assertEquals(7500.0, found.get().getAccountBalance(), 0.01);
    }

    @Test
    public void testFindAllAccounts() {
        entityManager.persistAndFlush(sampleAccount);

        Account account2 = Account.builder()
                .accountNumber(100002L)
                .accountStatus("Active")
                .accountType("CHECKING")
                .accountBalance(1000.0)
                .createDateTime(new Date())
                .build();
        entityManager.persistAndFlush(account2);

        Iterable<Account> accounts = accountRepository.findAll();

        int count = 0;
        for (Account a : accounts) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testDeleteAccount() {
        Account persisted = entityManager.persistAndFlush(sampleAccount);

        accountRepository.delete(persisted);
        entityManager.flush();

        Optional<Account> found = accountRepository.findByAccountNumber(100001L);
        assertFalse(found.isPresent());
    }
}
