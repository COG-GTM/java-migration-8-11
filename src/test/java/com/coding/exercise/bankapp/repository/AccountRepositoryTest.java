package com.coding.exercise.bankapp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

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

    @Test
    public void testFindByAccountNumber_found() {
        Address branchAddress = Address.builder()
                .address1("789 Bank Blvd")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("US")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchCode(100)
                .branchName("Main Branch")
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        Account account = Account.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountBalance(1500.00)
                .accountStatus("Active")
                .bankInformation(bankInfo)
                .build();

        entityManager.persistAndFlush(account);

        Optional<Account> result = accountRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(Long.valueOf(5001L), result.get().getAccountNumber());
        assertEquals("SAVINGS", result.get().getAccountType());
        assertEquals(Double.valueOf(1500.00), result.get().getAccountBalance());
        assertEquals("Main Branch", result.get().getBankInformation().getBranchName());
    }

    @Test
    public void testFindByAccountNumber_notFound() {
        Optional<Account> result = accountRepository.findByAccountNumber(99999L);

        assertFalse(result.isPresent());
    }
}
