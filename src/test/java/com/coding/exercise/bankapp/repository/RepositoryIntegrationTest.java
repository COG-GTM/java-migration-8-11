package com.coding.exercise.bankapp.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.BankInfo;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;

@DataJpaTest
class RepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerAccountXRefRepository customerAccountXRefRepository;

    // ---- CustomerRepository ----

    @Test
    void findByCustomerNumber_found() {
        Address address = Address.builder()
                .address1("123 Main St").city("NYC").state("NY").zip("10001").country("US")
                .build();
        Contact contact = Contact.builder()
                .emailId("test@test.com").homePhone("111").workPhone("222")
                .build();
        Customer customer = Customer.builder()
                .firstName("John").lastName("Doe").customerNumber(1001L).status("ACTIVE")
                .customerAddress(address).contactDetails(contact)
                .createDateTime(new Date())
                .build();
        entityManager.persistAndFlush(customer);

        Optional<Customer> result = customerRepository.findByCustomerNumber(1001L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals(1001L, result.get().getCustomerNumber());
    }

    @Test
    void findByCustomerNumber_notFound() {
        Optional<Customer> result = customerRepository.findByCustomerNumber(9999L);

        assertFalse(result.isPresent());
    }

    // ---- AccountRepository ----

    @Test
    void findByAccountNumber_found() {
        Address branchAddr = Address.builder()
                .address1("100 Bank St").city("NYC").state("NY").zip("10001").country("US")
                .build();
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch").branchCode(101).routingNumber(987654)
                .branchAddress(branchAddr)
                .build();
        Account account = Account.builder()
                .accountNumber(5001L).accountStatus("ACTIVE")
                .accountType("SAVINGS").accountBalance(1500.0)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
                .build();
        entityManager.persistAndFlush(account);

        Optional<Account> result = accountRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(5001L, result.get().getAccountNumber());
        assertEquals("ACTIVE", result.get().getAccountStatus());
        assertEquals(1500.0, result.get().getAccountBalance());
    }

    @Test
    void findByAccountNumber_notFound() {
        Optional<Account> result = accountRepository.findByAccountNumber(9999L);

        assertFalse(result.isPresent());
    }

    // ---- TransactionRepository ----

    @Test
    void findByAccountNumber_withTransactions() {
        Transaction t1 = Transaction.builder()
                .accountNumber(5001L).txAmount(100.0).txType("CREDIT").txDateTime(new Date())
                .build();
        Transaction t2 = Transaction.builder()
                .accountNumber(5001L).txAmount(50.0).txType("DEBIT").txDateTime(new Date())
                .build();
        entityManager.persistAndFlush(t1);
        entityManager.persistAndFlush(t2);

        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(5001L);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
    }

    @Test
    void findByAccountNumber_emptyTransactions() {
        Optional<List<Transaction>> result = transactionRepository.findByAccountNumber(9999L);

        assertTrue(result.isEmpty() || result.get().isEmpty());
    }

    // ---- CustomerAccountXRefRepository ----

    @Test
    void saveAndFind_xref() {
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
                .accountNumber(5001L).customerNumber(1001L)
                .build();
        CustomerAccountXRef saved = customerAccountXRefRepository.save(xref);
        entityManager.flush();

        assertNotNull(saved.getId());
        assertEquals(5001L, saved.getAccountNumber());
        assertEquals(1001L, saved.getCustomerNumber());

        // Verify the entity exists in the database
        assertTrue(customerAccountXRefRepository.count() > 0);
    }

    @Test
    void saveMultiple_xrefs() {
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
                .accountNumber(5001L).customerNumber(1001L)
                .build();
        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
                .accountNumber(5002L).customerNumber(1001L)
                .build();
        customerAccountXRefRepository.save(xref1);
        customerAccountXRefRepository.save(xref2);

        assertEquals(2, customerAccountXRefRepository.count());
    }
}
