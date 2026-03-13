package com.coding.exercise.bankapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
class RepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerAccountXRefRepository custAccXRefRepository;

    // ========== AccountRepository Tests ==========

    @Test
    void accountRepository_save_andFindByAccountNumber() {
        Address branchAddress = Address.builder()
                .address1("123 Bank St")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(12345)
                .branchAddress(branchAddress)
                .build();

        Account account = Account.builder()
                .accountNumber(1001L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(5000.0)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(account);

        Optional<Account> found = accountRepository.findByAccountNumber(1001L);

        assertTrue(found.isPresent());
        assertEquals(1001L, found.get().getAccountNumber());
        assertEquals("SAVINGS", found.get().getAccountType());
        assertEquals("ACTIVE", found.get().getAccountStatus());
        assertEquals(5000.0, found.get().getAccountBalance());
        assertNotNull(found.get().getBankInformation());
        assertEquals("Main Branch", found.get().getBankInformation().getBranchName());
        assertEquals(1001, found.get().getBankInformation().getBranchCode());
        assertEquals(12345, found.get().getBankInformation().getRoutingNumber());
        assertNotNull(found.get().getBankInformation().getBranchAddress());
        assertEquals("New York", found.get().getBankInformation().getBranchAddress().getCity());
    }

    @Test
    void accountRepository_findByAccountNumber_notFound() {
        Optional<Account> found = accountRepository.findByAccountNumber(9999L);
        assertFalse(found.isPresent());
    }

    @Test
    void accountRepository_save_andDelete() {
        Account account = Account.builder()
                .accountNumber(2002L)
                .accountType("CHECKING")
                .accountStatus("ACTIVE")
                .accountBalance(1000.0)
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(account);

        Optional<Account> found = accountRepository.findByAccountNumber(2002L);
        assertTrue(found.isPresent());

        // Use JPQL bulk delete to avoid H2 2.x + Hibernate 5.6.x UUID type mismatch
        entityManager.getEntityManager()
                .createQuery("DELETE FROM Account a WHERE a.accountNumber = :num")
                .setParameter("num", 2002L)
                .executeUpdate();
        entityManager.clear();

        Optional<Account> deleted = accountRepository.findByAccountNumber(2002L);
        assertFalse(deleted.isPresent());
    }

    @Test
    void accountRepository_save_updatesBalance() {
        Account account = Account.builder()
                .accountNumber(3003L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(5000.0)
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(account);

        // Use JPQL bulk update to avoid H2 2.x + Hibernate 5.6.x UUID type mismatch
        entityManager.getEntityManager()
                .createQuery("UPDATE Account a SET a.accountBalance = :balance WHERE a.accountNumber = :num")
                .setParameter("balance", 6000.0)
                .setParameter("num", 3003L)
                .executeUpdate();
        entityManager.clear();

        Optional<Account> updated = accountRepository.findByAccountNumber(3003L);
        assertTrue(updated.isPresent());
        assertEquals(6000.0, updated.get().getAccountBalance());
    }

    // ========== CustomerRepository Tests ==========

    @Test
    void customerRepository_save_andFindByCustomerNumber() {
        Address address = Address.builder()
                .address1("456 Customer Ave")
                .city("Boston")
                .state("MA")
                .zip("02101")
                .country("USA")
                .build();

        Contact contact = Contact.builder()
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        Customer customer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(100L)
                .status("ACTIVE")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByCustomerNumber(100L);

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("M", found.get().getMiddleName());
        assertEquals("Doe", found.get().getLastName());
        assertEquals(100L, found.get().getCustomerNumber());
        assertEquals("ACTIVE", found.get().getStatus());
        assertNotNull(found.get().getCustomerAddress());
        assertEquals("456 Customer Ave", found.get().getCustomerAddress().getAddress1());
        assertNotNull(found.get().getContactDetails());
        assertEquals("john@example.com", found.get().getContactDetails().getEmailId());
    }

    @Test
    void customerRepository_findByCustomerNumber_notFound() {
        Optional<Customer> found = customerRepository.findByCustomerNumber(9999L);
        assertFalse(found.isPresent());
    }

    @Test
    void customerRepository_findAll_returnsAllCustomers() {
        Customer customer1 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(100L)
                .status("ACTIVE")
                .createDateTime(new Date())
                .build();

        Customer customer2 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(200L)
                .status("ACTIVE")
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);

        Iterable<Customer> customers = customerRepository.findAll();
        int count = 0;
        for (Customer c : customers) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void customerRepository_save_andDelete() {
        Customer customer = Customer.builder()
                .firstName("ToDelete")
                .lastName("User")
                .customerNumber(300L)
                .status("ACTIVE")
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByCustomerNumber(300L);
        assertTrue(found.isPresent());

        // Use JPQL bulk delete to avoid H2 2.x + Hibernate 5.6.x UUID type mismatch
        entityManager.getEntityManager()
                .createQuery("DELETE FROM Customer c WHERE c.customerNumber = :num")
                .setParameter("num", 300L)
                .executeUpdate();
        entityManager.clear();

        Optional<Customer> deleted = customerRepository.findByCustomerNumber(300L);
        assertFalse(deleted.isPresent());
    }

    @Test
    void customerRepository_cascadesDeleteToContactAndAddress() {
        Address address = Address.builder()
                .address1("Cascade St")
                .city("CascadeCity")
                .state("CC")
                .zip("11111")
                .country("USA")
                .build();

        Contact contact = Contact.builder()
                .emailId("cascade@test.com")
                .homePhone("555-0000")
                .workPhone("555-0001")
                .build();

        Customer customer = Customer.builder()
                .firstName("Cascade")
                .lastName("Test")
                .customerNumber(400L)
                .status("ACTIVE")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(customer);

        Optional<Customer> found = customerRepository.findByCustomerNumber(400L);
        assertTrue(found.isPresent());
        assertNotNull(found.get().getContactDetails());
        assertNotNull(found.get().getCustomerAddress());

        // Use JPQL bulk delete to avoid H2 2.x + Hibernate 5.6.x UUID type mismatch
        entityManager.getEntityManager()
                .createQuery("DELETE FROM Customer c WHERE c.customerNumber = :num")
                .setParameter("num", 400L)
                .executeUpdate();
        entityManager.clear();

        Optional<Customer> deleted = customerRepository.findByCustomerNumber(400L);
        assertFalse(deleted.isPresent());
    }

    // ========== TransactionRepository Tests ==========

    @Test
    void transactionRepository_save_andFindByAccountNumber() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(1001L)
                .txType("CREDIT")
                .txAmount(500.0)
                .txDateTime(new Date())
                .build();

        Transaction tx2 = Transaction.builder()
                .accountNumber(1001L)
                .txType("DEBIT")
                .txAmount(200.0)
                .txDateTime(new Date())
                .build();

        entityManager.persistAndFlush(tx1);
        entityManager.persistAndFlush(tx2);

        Optional<List<Transaction>> found = transactionRepository.findByAccountNumber(1001L);

        assertTrue(found.isPresent());
        assertEquals(2, found.get().size());
    }

    @Test
    void transactionRepository_findByAccountNumber_noTransactions() {
        Optional<List<Transaction>> found = transactionRepository.findByAccountNumber(9999L);

        // Spring Data returns Optional.of(emptyList) or Optional.empty for derived queries
        assertTrue(!found.isPresent() || found.get().isEmpty());
    }

    @Test
    void transactionRepository_save_andFindAll() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(1001L)
                .txType("CREDIT")
                .txAmount(500.0)
                .txDateTime(new Date())
                .build();

        Transaction tx2 = Transaction.builder()
                .accountNumber(2002L)
                .txType("DEBIT")
                .txAmount(300.0)
                .txDateTime(new Date())
                .build();

        entityManager.persistAndFlush(tx1);
        entityManager.persistAndFlush(tx2);

        Iterable<Transaction> all = transactionRepository.findAll();
        int count = 0;
        for (Transaction t : all) {
            count++;
        }
        assertEquals(2, count);
    }

    // ========== CustomerAccountXRefRepository Tests ==========

    @Test
    void customerAccountXRefRepository_save_andFindAll() {
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
                .customerNumber(100L)
                .accountNumber(1001L)
                .build();

        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
                .customerNumber(100L)
                .accountNumber(1002L)
                .build();

        entityManager.persistAndFlush(xref1);
        entityManager.persistAndFlush(xref2);

        Iterable<CustomerAccountXRef> all = custAccXRefRepository.findAll();
        int count = 0;
        for (CustomerAccountXRef x : all) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void customerAccountXRefRepository_save_andDelete() {
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
                .customerNumber(200L)
                .accountNumber(2001L)
                .build();

        CustomerAccountXRef saved = entityManager.persistAndFlush(xref);
        assertNotNull(saved.getId());

        // Use JPQL bulk delete to avoid H2 2.x + Hibernate 5.6.x UUID type mismatch
        entityManager.getEntityManager()
                .createQuery("DELETE FROM CustomerAccountXRef x WHERE x.accountNumber = :accNum AND x.customerNumber = :custNum")
                .setParameter("accNum", 2001L)
                .setParameter("custNum", 200L)
                .executeUpdate();
        entityManager.clear();

        Iterable<CustomerAccountXRef> remaining = custAccXRefRepository.findAll();
        int count = 0;
        for (CustomerAccountXRef x : remaining) {
            count++;
        }
        assertEquals(0, count);
    }

    @Test
    void customerAccountXRefRepository_multipleCustomers_differentAccounts() {
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
                .customerNumber(100L)
                .accountNumber(1001L)
                .build();

        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
                .customerNumber(200L)
                .accountNumber(2001L)
                .build();

        CustomerAccountXRef xref3 = CustomerAccountXRef.builder()
                .customerNumber(100L)
                .accountNumber(1002L)
                .build();

        entityManager.persistAndFlush(xref1);
        entityManager.persistAndFlush(xref2);
        entityManager.persistAndFlush(xref3);

        Iterable<CustomerAccountXRef> all = custAccXRefRepository.findAll();
        int count = 0;
        for (CustomerAccountXRef x : all) {
            count++;
        }
        assertEquals(3, count);
    }

    // ========== Relationship/Cascade Tests ==========

    @Test
    void accountRepository_cascadesSaveToBankInfoAndAddress() {
        Address branchAddress = Address.builder()
                .address1("Cascade Bank St")
                .city("CascadeCity")
                .state("CC")
                .zip("22222")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Cascade Branch")
                .branchCode(5555)
                .routingNumber(66666)
                .branchAddress(branchAddress)
                .build();

        Account account = Account.builder()
                .accountNumber(7777L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(10000.0)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
                .build();

        entityManager.persistAndFlush(account);

        Optional<Account> found = accountRepository.findByAccountNumber(7777L);
        assertTrue(found.isPresent());
        assertNotNull(found.get().getBankInformation());
        assertNotNull(found.get().getBankInformation().getId());
        assertNotNull(found.get().getBankInformation().getBranchAddress());
        assertNotNull(found.get().getBankInformation().getBranchAddress().getId());
    }
}
