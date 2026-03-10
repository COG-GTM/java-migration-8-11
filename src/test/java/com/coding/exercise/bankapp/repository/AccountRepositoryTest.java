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

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.BankInfo;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account buildAccount(Long accountNumber, Double balance) {
        Address branchAddr = Address.builder()
                .address1("100 Bank St")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(101)
                .routingNumber(12345)
                .branchAddress(branchAddr)
                .build();
        return Account.builder()
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(balance)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
                .build();
    }

    @Test
    public void save_and_findByAccountNumber() {
        Account account = buildAccount(5001L, 1000.0);
        Account saved = accountRepository.save(account);

        assertNotNull(saved.getId());

        Optional<Account> result = accountRepository.findByAccountNumber(5001L);
        assertTrue(result.isPresent());
        assertEquals(Long.valueOf(5001L), result.get().getAccountNumber());
        assertEquals(1000.0, result.get().getAccountBalance(), 0.001);
    }

    @Test
    public void findByAccountNumber_notFound() {
        Optional<Account> result = accountRepository.findByAccountNumber(9999L);
        assertFalse(result.isPresent());
    }

    @Test
    public void findAll_returnsAllAccounts() {
        accountRepository.save(buildAccount(6001L, 500.0));
        accountRepository.save(buildAccount(6002L, 1500.0));

        Iterable<Account> all = accountRepository.findAll();
        int count = 0;
        for (Account a : all) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void update_modifiesBalance() {
        Account account = buildAccount(7001L, 1000.0);
        Account saved = accountRepository.save(account);

        saved.setAccountBalance(2000.0);
        saved.setUpdateDateTime(new Date());
        accountRepository.save(saved);

        Optional<Account> result = accountRepository.findByAccountNumber(7001L);
        assertTrue(result.isPresent());
        assertEquals(2000.0, result.get().getAccountBalance(), 0.001);
    }

    @Test
    public void delete_removesAccount() {
        Account account = buildAccount(8001L, 500.0);
        Account saved = accountRepository.save(account);

        accountRepository.delete(saved);

        Optional<Account> result = accountRepository.findByAccountNumber(8001L);
        assertFalse(result.isPresent());
    }
}
