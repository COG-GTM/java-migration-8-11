package com.coding.exercise.bankapp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

public class ModelTest {

    @Test
    public void testCustomerBuilder() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        Date now = new Date();
        Customer customer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(now)
                .updateDateTime(now)
                .build();

        assertEquals("John", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals(Long.valueOf(12345L), customer.getCustomerNumber());
        assertEquals("Active", customer.getStatus());
        assertNotNull(customer.getCustomerAddress());
        assertNotNull(customer.getContactDetails());
        assertEquals(now, customer.getCreateDateTime());
        assertEquals(now, customer.getUpdateDateTime());
    }

    @Test
    public void testCustomerSettersAndGetters() {
        Customer customer = new Customer();
        UUID id = UUID.randomUUID();
        Date now = new Date();

        customer.setId(id);
        customer.setFirstName("Jane");
        customer.setMiddleName("A");
        customer.setLastName("Smith");
        customer.setCustomerNumber(67890L);
        customer.setStatus("Inactive");
        customer.setCreateDateTime(now);
        customer.setUpdateDateTime(now);

        assertEquals(id, customer.getId());
        assertEquals("Jane", customer.getFirstName());
        assertEquals("A", customer.getMiddleName());
        assertEquals("Smith", customer.getLastName());
        assertEquals(Long.valueOf(67890L), customer.getCustomerNumber());
        assertEquals("Inactive", customer.getStatus());
        assertEquals(now, customer.getCreateDateTime());
        assertEquals(now, customer.getUpdateDateTime());
    }

    @Test
    public void testCustomerEqualsAndHashCode() {
        Customer customer1 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .build();

        Customer customer2 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .build();

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    public void testCustomerToString() {
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        String toString = customer.toString();
        assertNotNull(toString);
        assertEquals(true, toString.contains("John"));
        assertEquals(true, toString.contains("Doe"));
    }

    @Test
    public void testCustomerAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date now = new Date();
        Address address = new Address();
        Contact contact = new Contact();

        Customer customer = new Customer(id, "John", "Doe", "M", 12345L, "Active", address, contact, now, now);

        assertEquals(id, customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("M", customer.getMiddleName());
        assertEquals(Long.valueOf(12345L), customer.getCustomerNumber());
        assertEquals("Active", customer.getStatus());
        assertEquals(address, customer.getCustomerAddress());
        assertEquals(contact, customer.getContactDetails());
    }

    @Test
    public void testAccountBuilder() {
        Address branchAddress = Address.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        Date now = new Date();
        Account account = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInfo)
                .createDateTime(now)
                .updateDateTime(now)
                .build();

        assertEquals(Long.valueOf(1000001L), account.getAccountNumber());
        assertEquals("Savings", account.getAccountType());
        assertEquals("Active", account.getAccountStatus());
        assertEquals(Double.valueOf(5000.00), account.getAccountBalance());
        assertNotNull(account.getBankInformation());
        assertEquals(now, account.getCreateDateTime());
        assertEquals(now, account.getUpdateDateTime());
    }

    @Test
    public void testAccountSettersAndGetters() {
        Account account = new Account();
        UUID id = UUID.randomUUID();
        Date now = new Date();
        BankInfo bankInfo = new BankInfo();

        account.setId(id);
        account.setAccountNumber(2000002L);
        account.setAccountType("Checking");
        account.setAccountStatus("Inactive");
        account.setAccountBalance(10000.00);
        account.setBankInformation(bankInfo);
        account.setCreateDateTime(now);
        account.setUpdateDateTime(now);

        assertEquals(id, account.getId());
        assertEquals(Long.valueOf(2000002L), account.getAccountNumber());
        assertEquals("Checking", account.getAccountType());
        assertEquals("Inactive", account.getAccountStatus());
        assertEquals(Double.valueOf(10000.00), account.getAccountBalance());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals(now, account.getCreateDateTime());
        assertEquals(now, account.getUpdateDateTime());
    }

    @Test
    public void testAccountEqualsAndHashCode() {
        Account account1 = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .build();

        Account account2 = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .build();

        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    public void testAccountToString() {
        Account account = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .build();

        String toString = account.toString();
        assertNotNull(toString);
        assertEquals(true, toString.contains("1000001"));
        assertEquals(true, toString.contains("Savings"));
    }

    @Test
    public void testAccountAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date now = new Date();
        BankInfo bankInfo = new BankInfo();

        Account account = new Account(id, 1000001L, bankInfo, "Active", "Savings", 5000.00, now, now);

        assertEquals(id, account.getId());
        assertEquals(Long.valueOf(1000001L), account.getAccountNumber());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals("Active", account.getAccountStatus());
        assertEquals("Savings", account.getAccountType());
        assertEquals(Double.valueOf(5000.00), account.getAccountBalance());
    }

    @Test
    public void testTransactionBuilder() {
        Date now = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(1000001L)
                .txDateTime(now)
                .txType("DEBIT")
                .txAmount(100.00)
                .build();

        assertEquals(Long.valueOf(1000001L), transaction.getAccountNumber());
        assertEquals(now, transaction.getTxDateTime());
        assertEquals("DEBIT", transaction.getTxType());
        assertEquals(Double.valueOf(100.00), transaction.getTxAmount());
    }

    @Test
    public void testTransactionSettersAndGetters() {
        Transaction transaction = new Transaction();
        UUID id = UUID.randomUUID();
        Date now = new Date();

        transaction.setId(id);
        transaction.setAccountNumber(2000002L);
        transaction.setTxDateTime(now);
        transaction.setTxType("CREDIT");
        transaction.setTxAmount(200.00);

        assertEquals(id, transaction.getId());
        assertEquals(Long.valueOf(2000002L), transaction.getAccountNumber());
        assertEquals(now, transaction.getTxDateTime());
        assertEquals("CREDIT", transaction.getTxType());
        assertEquals(Double.valueOf(200.00), transaction.getTxAmount());
    }

    @Test
    public void testTransactionEqualsAndHashCode() {
        Date now = new Date();
        Transaction transaction1 = Transaction.builder()
                .accountNumber(1000001L)
                .txType("DEBIT")
                .txAmount(100.00)
                .txDateTime(now)
                .build();

        Transaction transaction2 = Transaction.builder()
                .accountNumber(1000001L)
                .txType("DEBIT")
                .txAmount(100.00)
                .txDateTime(now)
                .build();

        assertEquals(transaction1, transaction2);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    public void testTransactionToString() {
        Transaction transaction = Transaction.builder()
                .accountNumber(1000001L)
                .txType("DEBIT")
                .build();

        String toString = transaction.toString();
        assertNotNull(toString);
        assertEquals(true, toString.contains("1000001"));
        assertEquals(true, toString.contains("DEBIT"));
    }

    @Test
    public void testTransactionAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        Transaction transaction = new Transaction(id, 1000001L, now, "DEBIT", 100.00);

        assertEquals(id, transaction.getId());
        assertEquals(Long.valueOf(1000001L), transaction.getAccountNumber());
        assertEquals(now, transaction.getTxDateTime());
        assertEquals("DEBIT", transaction.getTxType());
        assertEquals(Double.valueOf(100.00), transaction.getTxAmount());
    }

    @Test
    public void testAddressBuilder() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        assertEquals("123 Main St", address.getAddress1());
        assertEquals("Apt 4", address.getAddress2());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZip());
        assertEquals("USA", address.getCountry());
    }

    @Test
    public void testAddressSettersAndGetters() {
        Address address = new Address();
        UUID id = UUID.randomUUID();

        address.setId(id);
        address.setAddress1("456 Oak Ave");
        address.setAddress2("Suite 100");
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZip("90001");
        address.setCountry("USA");

        assertEquals(id, address.getId());
        assertEquals("456 Oak Ave", address.getAddress1());
        assertEquals("Suite 100", address.getAddress2());
        assertEquals("Los Angeles", address.getCity());
        assertEquals("CA", address.getState());
        assertEquals("90001", address.getZip());
        assertEquals("USA", address.getCountry());
    }

    @Test
    public void testAddressEqualsAndHashCode() {
        Address address1 = Address.builder()
                .address1("123 Main St")
                .city("New York")
                .build();

        Address address2 = Address.builder()
                .address1("123 Main St")
                .city("New York")
                .build();

        assertEquals(address1, address2);
        assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    public void testAddressToString() {
        Address address = Address.builder()
                .address1("123 Main St")
                .city("New York")
                .build();

        String toString = address.toString();
        assertNotNull(toString);
        assertEquals(true, toString.contains("123 Main St"));
        assertEquals(true, toString.contains("New York"));
    }

    @Test
    public void testAddressAllArgsConstructor() {
        UUID id = UUID.randomUUID();

        Address address = new Address(id, "123 Main St", "Apt 4", "New York", "NY", "10001", "USA");

        assertEquals(id, address.getId());
        assertEquals("123 Main St", address.getAddress1());
        assertEquals("Apt 4", address.getAddress2());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZip());
        assertEquals("USA", address.getCountry());
    }

    @Test
    public void testContactBuilder() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        assertEquals("test@example.com", contact.getEmailId());
        assertEquals("555-1234", contact.getHomePhone());
        assertEquals("555-5678", contact.getWorkPhone());
    }

    @Test
    public void testContactSettersAndGetters() {
        Contact contact = new Contact();
        UUID id = UUID.randomUUID();

        contact.setId(id);
        contact.setEmailId("jane@example.com");
        contact.setHomePhone("555-9999");
        contact.setWorkPhone("555-8888");

        assertEquals(id, contact.getId());
        assertEquals("jane@example.com", contact.getEmailId());
        assertEquals("555-9999", contact.getHomePhone());
        assertEquals("555-8888", contact.getWorkPhone());
    }

    @Test
    public void testContactEqualsAndHashCode() {
        Contact contact1 = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();

        Contact contact2 = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();

        assertEquals(contact1, contact2);
        assertEquals(contact1.hashCode(), contact2.hashCode());
    }

    @Test
    public void testContactToString() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();

        String toString = contact.toString();
        assertNotNull(toString);
        assertEquals(true, toString.contains("test@example.com"));
        assertEquals(true, toString.contains("555-1234"));
    }

    @Test
    public void testContactAllArgsConstructor() {
        UUID id = UUID.randomUUID();

        Contact contact = new Contact(id, "test@example.com", "555-1234", "555-5678");

        assertEquals(id, contact.getId());
        assertEquals("test@example.com", contact.getEmailId());
        assertEquals("555-1234", contact.getHomePhone());
        assertEquals("555-5678", contact.getWorkPhone());
    }

    @Test
    public void testBankInfoBuilder() {
        Address branchAddress = Address.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        assertEquals("Main Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(1001), bankInfo.getBranchCode());
        assertEquals(Integer.valueOf(123456789), bankInfo.getRoutingNumber());
        assertNotNull(bankInfo.getBranchAddress());
    }

    @Test
    public void testBankInfoSettersAndGetters() {
        BankInfo bankInfo = new BankInfo();
        UUID id = UUID.randomUUID();
        Address branchAddress = new Address();

        bankInfo.setId(id);
        bankInfo.setBranchName("Downtown Branch");
        bankInfo.setBranchCode(2002);
        bankInfo.setRoutingNumber(987654321);
        bankInfo.setBranchAddress(branchAddress);

        assertEquals(id, bankInfo.getId());
        assertEquals("Downtown Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(2002), bankInfo.getBranchCode());
        assertEquals(Integer.valueOf(987654321), bankInfo.getRoutingNumber());
        assertEquals(branchAddress, bankInfo.getBranchAddress());
    }

    @Test
    public void testBankInfoEqualsAndHashCode() {
        BankInfo bankInfo1 = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .build();

        BankInfo bankInfo2 = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .build();

        assertEquals(bankInfo1, bankInfo2);
        assertEquals(bankInfo1.hashCode(), bankInfo2.hashCode());
    }

    @Test
    public void testBankInfoToString() {
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .build();

        String toString = bankInfo.toString();
        assertNotNull(toString);
        assertEquals(true, toString.contains("Main Branch"));
        assertEquals(true, toString.contains("1001"));
    }

    @Test
    public void testBankInfoAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Address branchAddress = new Address();

        BankInfo bankInfo = new BankInfo(id, "Main Branch", 1001, branchAddress, 123456789);

        assertEquals(id, bankInfo.getId());
        assertEquals("Main Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(1001), bankInfo.getBranchCode());
        assertEquals(branchAddress, bankInfo.getBranchAddress());
        assertEquals(Integer.valueOf(123456789), bankInfo.getRoutingNumber());
    }

    @Test
    public void testCustomerAccountXRefBuilder() {
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
                .accountNumber(1000001L)
                .customerNumber(12345L)
                .build();

        assertEquals(Long.valueOf(1000001L), xref.getAccountNumber());
        assertEquals(Long.valueOf(12345L), xref.getCustomerNumber());
    }

    @Test
    public void testCustomerAccountXRefSettersAndGetters() {
        CustomerAccountXRef xref = new CustomerAccountXRef();
        UUID id = UUID.randomUUID();

        xref.setId(id);
        xref.setAccountNumber(2000002L);
        xref.setCustomerNumber(67890L);

        assertEquals(id, xref.getId());
        assertEquals(Long.valueOf(2000002L), xref.getAccountNumber());
        assertEquals(Long.valueOf(67890L), xref.getCustomerNumber());
    }

    @Test
    public void testCustomerAccountXRefEqualsAndHashCode() {
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
                .accountNumber(1000001L)
                .customerNumber(12345L)
                .build();

        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
                .accountNumber(1000001L)
                .customerNumber(12345L)
                .build();

        assertEquals(xref1, xref2);
        assertEquals(xref1.hashCode(), xref2.hashCode());
    }

    @Test
    public void testCustomerAccountXRefToString() {
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
                .accountNumber(1000001L)
                .customerNumber(12345L)
                .build();

        String toString = xref.toString();
        assertNotNull(toString);
        assertEquals(true, toString.contains("1000001"));
        assertEquals(true, toString.contains("12345"));
    }

    @Test
    public void testCustomerAccountXRefAllArgsConstructor() {
        UUID id = UUID.randomUUID();

        CustomerAccountXRef xref = new CustomerAccountXRef(id, 1000001L, 12345L);

        assertEquals(id, xref.getId());
        assertEquals(Long.valueOf(1000001L), xref.getAccountNumber());
        assertEquals(Long.valueOf(12345L), xref.getCustomerNumber());
    }

    @Test
    public void testCustomerNotEquals() {
        Customer customer1 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .build();

        Customer customer2 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(67890L)
                .build();

        assertNotEquals(customer1, customer2);
    }

    @Test
    public void testAccountNotEquals() {
        Account account1 = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .build();

        Account account2 = Account.builder()
                .accountNumber(2000002L)
                .accountType("Checking")
                .build();

        assertNotEquals(account1, account2);
    }

    @Test
    public void testAddressNotEquals() {
        Address address1 = Address.builder()
                .address1("123 Main St")
                .city("New York")
                .build();

        Address address2 = Address.builder()
                .address1("456 Oak Ave")
                .city("Los Angeles")
                .build();

        assertNotEquals(address1, address2);
    }

    @Test
    public void testContactNotEquals() {
        Contact contact1 = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();

        Contact contact2 = Contact.builder()
                .emailId("jane@example.com")
                .homePhone("555-9999")
                .build();

        assertNotEquals(contact1, contact2);
    }

    @Test
    public void testBankInfoNotEquals() {
        BankInfo bankInfo1 = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .build();

        BankInfo bankInfo2 = BankInfo.builder()
                .branchName("Downtown Branch")
                .branchCode(2002)
                .build();

        assertNotEquals(bankInfo1, bankInfo2);
    }

    @Test
    public void testTransactionNotEquals() {
        Transaction transaction1 = Transaction.builder()
                .accountNumber(1000001L)
                .txType("DEBIT")
                .txAmount(100.00)
                .build();

        Transaction transaction2 = Transaction.builder()
                .accountNumber(2000002L)
                .txType("CREDIT")
                .txAmount(200.00)
                .build();

        assertNotEquals(transaction1, transaction2);
    }

    @Test
    public void testCustomerAccountXRefNotEquals() {
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
                .accountNumber(1000001L)
                .customerNumber(12345L)
                .build();

        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
                .accountNumber(2000002L)
                .customerNumber(67890L)
                .build();

        assertNotEquals(xref1, xref2);
    }
}
