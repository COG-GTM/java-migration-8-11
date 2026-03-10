package com.coding.exercise.bankapp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

public class ModelTest {

    // ===================== Account =====================

    @Test
    public void account_builderAndGetters() {
        Date now = new Date();
        UUID id = UUID.randomUUID();
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main")
                .branchCode(101)
                .routingNumber(12345)
                .build();

        Account account = Account.builder()
                .id(id)
                .accountNumber(5001L)
                .bankInformation(bankInfo)
                .accountStatus("Active")
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .createDateTime(now)
                .updateDateTime(now)
                .build();

        assertEquals(id, account.getId());
        assertEquals(Long.valueOf(5001L), account.getAccountNumber());
        assertEquals("Active", account.getAccountStatus());
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(1000.0, account.getAccountBalance(), 0.001);
        assertEquals(now, account.getCreateDateTime());
        assertEquals(now, account.getUpdateDateTime());
        assertNotNull(account.getBankInformation());
    }

    @Test
    public void account_noArgsConstructorAndSetters() {
        Account account = new Account();
        account.setAccountNumber(5002L);
        account.setAccountType("CHECKING");
        account.setAccountStatus("Inactive");
        account.setAccountBalance(500.0);

        assertEquals(Long.valueOf(5002L), account.getAccountNumber());
        assertEquals("CHECKING", account.getAccountType());
        assertEquals("Inactive", account.getAccountStatus());
        assertEquals(500.0, account.getAccountBalance(), 0.001);
        assertNull(account.getId());
    }

    @Test
    public void account_allArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date now = new Date();
        Account account = new Account(id, 5001L, null, "Active", "SAVINGS", 1000.0, now, now);

        assertEquals(id, account.getId());
        assertEquals(Long.valueOf(5001L), account.getAccountNumber());
    }

    @Test
    public void account_equalsAndHashCode() {
        UUID id = UUID.randomUUID();
        Account a1 = Account.builder().id(id).accountNumber(5001L).build();
        Account a2 = Account.builder().id(id).accountNumber(5001L).build();
        Account a3 = Account.builder().id(UUID.randomUUID()).accountNumber(5002L).build();

        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    public void account_toString() {
        Account account = Account.builder().accountNumber(5001L).accountType("SAVINGS").build();
        String str = account.toString();
        assertNotNull(str);
        // @Data generates toString
        assertTrue(str.contains("5001"));
    }

    // ===================== Customer =====================

    @Test
    public void customer_builderAndGetters() {
        UUID id = UUID.randomUUID();
        Date now = new Date();
        Customer customer = Customer.builder()
                .id(id)
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .createDateTime(now)
                .updateDateTime(now)
                .build();

        assertEquals(id, customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals(Long.valueOf(1001L), customer.getCustomerNumber());
        assertEquals("Active", customer.getStatus());
    }

    @Test
    public void customer_noArgsConstructorAndSetters() {
        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setCustomerNumber(1002L);
        customer.setStatus("Inactive");

        assertEquals("Jane", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals(Long.valueOf(1002L), customer.getCustomerNumber());
        assertEquals("Inactive", customer.getStatus());
    }

    @Test
    public void customer_equalsAndHashCode() {
        UUID id = UUID.randomUUID();
        Customer c1 = Customer.builder().id(id).customerNumber(1001L).firstName("John").lastName("Doe").build();
        Customer c2 = Customer.builder().id(id).customerNumber(1001L).firstName("John").lastName("Doe").build();

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    // ===================== Transaction =====================

    @Test
    public void transaction_builderAndGetters() {
        UUID id = UUID.randomUUID();
        Date now = new Date();
        Transaction tx = Transaction.builder()
                .id(id)
                .accountNumber(5001L)
                .txDateTime(now)
                .txType("DEBIT")
                .txAmount(100.0)
                .build();

        assertEquals(id, tx.getId());
        assertEquals(Long.valueOf(5001L), tx.getAccountNumber());
        assertEquals(now, tx.getTxDateTime());
        assertEquals("DEBIT", tx.getTxType());
        assertEquals(100.0, tx.getTxAmount(), 0.001);
    }

    @Test
    public void transaction_noArgsConstructorAndSetters() {
        Transaction tx = new Transaction();
        tx.setAccountNumber(5002L);
        tx.setTxType("CREDIT");
        tx.setTxAmount(200.0);

        assertEquals(Long.valueOf(5002L), tx.getAccountNumber());
        assertEquals("CREDIT", tx.getTxType());
        assertEquals(200.0, tx.getTxAmount(), 0.001);
    }

    @Test
    public void transaction_equalsAndHashCode() {
        UUID id = UUID.randomUUID();
        Transaction t1 = Transaction.builder().id(id).accountNumber(5001L).txType("DEBIT").txAmount(100.0).build();
        Transaction t2 = Transaction.builder().id(id).accountNumber(5001L).txType("DEBIT").txAmount(100.0).build();

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    // ===================== CustomerAccountXRef =====================

    @Test
    public void customerAccountXRef_builderAndGetters() {
        UUID id = UUID.randomUUID();
        CustomerAccountXRef xRef = CustomerAccountXRef.builder()
                .id(id)
                .accountNumber(5001L)
                .customerNumber(1001L)
                .build();

        assertEquals(id, xRef.getId());
        assertEquals(Long.valueOf(5001L), xRef.getAccountNumber());
        assertEquals(Long.valueOf(1001L), xRef.getCustomerNumber());
    }

    @Test
    public void customerAccountXRef_noArgsConstructorAndSetters() {
        CustomerAccountXRef xRef = new CustomerAccountXRef();
        xRef.setAccountNumber(5002L);
        xRef.setCustomerNumber(1002L);

        assertEquals(Long.valueOf(5002L), xRef.getAccountNumber());
        assertEquals(Long.valueOf(1002L), xRef.getCustomerNumber());
    }

    @Test
    public void customerAccountXRef_equalsAndHashCode() {
        UUID id = UUID.randomUUID();
        CustomerAccountXRef x1 = CustomerAccountXRef.builder().id(id).accountNumber(5001L).customerNumber(1001L).build();
        CustomerAccountXRef x2 = CustomerAccountXRef.builder().id(id).accountNumber(5001L).customerNumber(1001L).build();

        assertEquals(x1, x2);
        assertEquals(x1.hashCode(), x2.hashCode());
    }

    // ===================== Address =====================

    @Test
    public void address_builderAndGetters() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        assertEquals("123 Main St", address.getAddress1());
        assertEquals("Apt 4", address.getAddress2());
        assertEquals("Springfield", address.getCity());
        assertEquals("IL", address.getState());
        assertEquals("62701", address.getZip());
        assertEquals("US", address.getCountry());
    }

    @Test
    public void address_noArgsConstructorAndSetters() {
        Address address = new Address();
        address.setAddress1("456 Oak Ave");
        address.setCity("Shelbyville");

        assertEquals("456 Oak Ave", address.getAddress1());
        assertEquals("Shelbyville", address.getCity());
    }

    @Test
    public void address_equalsAndHashCode() {
        UUID id = UUID.randomUUID();
        Address a1 = Address.builder().id(id).address1("123 Main St").city("Springfield").build();
        Address a2 = Address.builder().id(id).address1("123 Main St").city("Springfield").build();

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    // ===================== BankInfo =====================

    @Test
    public void bankInfo_builderAndGetters() {
        BankInfo info = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(101)
                .routingNumber(12345)
                .build();

        assertEquals("Main Branch", info.getBranchName());
        assertEquals(Integer.valueOf(101), info.getBranchCode());
        assertEquals(Integer.valueOf(12345), info.getRoutingNumber());
    }

    @Test
    public void bankInfo_noArgsConstructorAndSetters() {
        BankInfo info = new BankInfo();
        info.setBranchName("West Branch");
        info.setBranchCode(202);
        info.setRoutingNumber(67890);

        assertEquals("West Branch", info.getBranchName());
        assertEquals(Integer.valueOf(202), info.getBranchCode());
        assertEquals(Integer.valueOf(67890), info.getRoutingNumber());
    }

    @Test
    public void bankInfo_withBranchAddress() {
        Address branchAddr = Address.builder()
                .address1("100 Bank St")
                .city("Springfield")
                .build();
        BankInfo info = BankInfo.builder()
                .branchName("Downtown")
                .branchAddress(branchAddr)
                .build();

        assertNotNull(info.getBranchAddress());
        assertEquals("100 Bank St", info.getBranchAddress().getAddress1());
    }

    // ===================== Contact =====================

    @Test
    public void contact_builderAndGetters() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("111-222-3333")
                .workPhone("444-555-6666")
                .build();

        assertEquals("test@example.com", contact.getEmailId());
        assertEquals("111-222-3333", contact.getHomePhone());
        assertEquals("444-555-6666", contact.getWorkPhone());
    }

    @Test
    public void contact_noArgsConstructorAndSetters() {
        Contact contact = new Contact();
        contact.setEmailId("other@example.com");
        contact.setHomePhone("777-888-9999");
        contact.setWorkPhone("000-111-2222");

        assertEquals("other@example.com", contact.getEmailId());
        assertEquals("777-888-9999", contact.getHomePhone());
        assertEquals("000-111-2222", contact.getWorkPhone());
    }

    @Test
    public void contact_equalsAndHashCode() {
        UUID id = UUID.randomUUID();
        Contact c1 = Contact.builder().id(id).emailId("test@test.com").build();
        Contact c2 = Contact.builder().id(id).emailId("test@test.com").build();

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }
}
