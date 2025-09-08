package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class ModelClassesTest {

    @Test
    void customer_builderAndGetters_workCorrectly() {
        Date createDate = new Date();
        Date updateDate = new Date();
        
        Contact contact = Contact.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();

        Address address = Address.builder()
            .address1("123 Main St")
            .address2("Apt 4B")
            .city("Anytown")
            .state("NY")
            .zip("12345")
            .country("USA")
            .build();

        Customer customer = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName("Q")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contact)
            .customerAddress(address)
            .createDateTime(createDate)
            .updateDateTime(updateDate)
            .build();

        assertEquals(Long.valueOf(123L), customer.getCustomerNumber());
        assertEquals("John", customer.getFirstName());
        assertEquals("Q", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("ACTIVE", customer.getStatus());
        assertEquals(contact, customer.getContactDetails());
        assertEquals(address, customer.getCustomerAddress());
        assertEquals(createDate, customer.getCreateDateTime());
        assertEquals(updateDate, customer.getUpdateDateTime());
    }

    @Test
    void customer_setters_workCorrectly() {
        Customer customer = new Customer();
        Date createDate = new Date();
        Date updateDate = new Date();
        
        Contact contact = Contact.builder()
            .emailId("new@example.com")
            .homePhone("555-1234")
            .workPhone("555-5678")
            .build();

        Address address = Address.builder()
            .address1("456 Oak St")
            .city("Newtown")
            .state("CA")
            .zip("90210")
            .country("USA")
            .build();

        customer.setCustomerNumber(456L);
        customer.setFirstName("Jane");
        customer.setMiddleName("M");
        customer.setLastName("Smith");
        customer.setStatus("INACTIVE");
        customer.setContactDetails(contact);
        customer.setCustomerAddress(address);
        customer.setCreateDateTime(createDate);
        customer.setUpdateDateTime(updateDate);

        assertEquals(Long.valueOf(456L), customer.getCustomerNumber());
        assertEquals("Jane", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("INACTIVE", customer.getStatus());
        assertEquals(contact, customer.getContactDetails());
        assertEquals(address, customer.getCustomerAddress());
        assertEquals(createDate, customer.getCreateDateTime());
        assertEquals(updateDate, customer.getUpdateDateTime());
    }

    @Test
    void account_builderAndGetters_workCorrectly() {
        Date createDate = new Date();
        Date updateDate = new Date();
        
        Address branchAddress = Address.builder()
            .address1("100 Bank St")
            .city("Financial District")
            .state("NY")
            .zip("10001")
            .country("USA")
            .build();

        BankInfo bankInfo = BankInfo.builder()
            .branchCode(1001)
            .branchName("Test Bank")
            .routingNumber(123456789)
            .branchAddress(branchAddress)
            .build();

        Account account = Account.builder()
            .accountNumber(789L)
            .accountType("SAVINGS")
            .accountBalance(2500.0)
            .accountStatus("ACTIVE")
            .bankInformation(bankInfo)
            .createDateTime(createDate)
            .updateDateTime(updateDate)
            .build();

        assertEquals(Long.valueOf(789L), account.getAccountNumber());
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(Double.valueOf(2500.0), account.getAccountBalance());
        assertEquals("ACTIVE", account.getAccountStatus());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals(createDate, account.getCreateDateTime());
        assertEquals(updateDate, account.getUpdateDateTime());
    }

    @Test
    void account_setters_workCorrectly() {
        Account account = new Account();
        Date createDate = new Date();
        Date updateDate = new Date();
        
        Address branchAddress = Address.builder()
            .address1("200 Credit St")
            .city("Banking City")
            .state("TX")
            .zip("75001")
            .country("USA")
            .build();

        BankInfo bankInfo = BankInfo.builder()
            .branchCode(2002)
            .branchName("Credit Bank")
            .routingNumber(987654321)
            .branchAddress(branchAddress)
            .build();

        account.setAccountNumber(999L);
        account.setAccountType("CHECKING");
        account.setAccountBalance(1500.0);
        account.setAccountStatus("INACTIVE");
        account.setBankInformation(bankInfo);
        account.setCreateDateTime(createDate);
        account.setUpdateDateTime(updateDate);

        assertEquals(Long.valueOf(999L), account.getAccountNumber());
        assertEquals("CHECKING", account.getAccountType());
        assertEquals(Double.valueOf(1500.0), account.getAccountBalance());
        assertEquals("INACTIVE", account.getAccountStatus());
        assertEquals(bankInfo, account.getBankInformation());
        assertEquals(createDate, account.getCreateDateTime());
        assertEquals(updateDate, account.getUpdateDateTime());
    }

    @Test
    void transaction_builderAndGetters_workCorrectly() {
        Date txDate = new Date();
        
        Transaction transaction = Transaction.builder()
            .accountNumber(123L)
            .txAmount(250.0)
            .txType("CREDIT")
            .txDateTime(txDate)
            .build();

        assertEquals(Long.valueOf(123L), transaction.getAccountNumber());
        assertEquals(Double.valueOf(250.0), transaction.getTxAmount());
        assertEquals("CREDIT", transaction.getTxType());
        assertEquals(txDate, transaction.getTxDateTime());
    }

    @Test
    void transaction_setters_workCorrectly() {
        Transaction transaction = new Transaction();
        Date txDate = new Date();

        transaction.setAccountNumber(456L);
        transaction.setTxAmount(100.0);
        transaction.setTxType("DEBIT");
        transaction.setTxDateTime(txDate);

        assertEquals(Long.valueOf(456L), transaction.getAccountNumber());
        assertEquals(Double.valueOf(100.0), transaction.getTxAmount());
        assertEquals("DEBIT", transaction.getTxType());
        assertEquals(txDate, transaction.getTxDateTime());
    }

    @Test
    void contact_builderAndGetters_workCorrectly() {
        Contact contact = Contact.builder()
            .emailId("contact@test.com")
            .homePhone("111-222-3333")
            .workPhone("444-555-6666")
            .build();

        assertEquals("contact@test.com", contact.getEmailId());
        assertEquals("111-222-3333", contact.getHomePhone());
        assertEquals("444-555-6666", contact.getWorkPhone());
    }

    @Test
    void contact_setters_workCorrectly() {
        Contact contact = new Contact();
        
        contact.setEmailId("updated@test.com");
        contact.setHomePhone("777-888-9999");
        contact.setWorkPhone("000-111-2222");

        assertEquals("updated@test.com", contact.getEmailId());
        assertEquals("777-888-9999", contact.getHomePhone());
        assertEquals("000-111-2222", contact.getWorkPhone());
    }

    @Test
    void address_builderAndGetters_workCorrectly() {
        Address address = Address.builder()
            .address1("789 Test Ave")
            .address2("Unit 5C")
            .city("Test City")
            .state("FL")
            .zip("33101")
            .country("USA")
            .build();

        assertEquals("789 Test Ave", address.getAddress1());
        assertEquals("Unit 5C", address.getAddress2());
        assertEquals("Test City", address.getCity());
        assertEquals("FL", address.getState());
        assertEquals("33101", address.getZip());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void address_setters_workCorrectly() {
        Address address = new Address();
        
        address.setAddress1("321 New St");
        address.setAddress2("Apt 10B");
        address.setCity("New City");
        address.setState("WA");
        address.setZip("98101");
        address.setCountry("USA");

        assertEquals("321 New St", address.getAddress1());
        assertEquals("Apt 10B", address.getAddress2());
        assertEquals("New City", address.getCity());
        assertEquals("WA", address.getState());
        assertEquals("98101", address.getZip());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void bankInfo_builderAndGetters_workCorrectly() {
        Address branchAddress = Address.builder()
            .address1("500 Bank Plaza")
            .city("Finance City")
            .state("IL")
            .zip("60601")
            .country("USA")
            .build();

        BankInfo bankInfo = BankInfo.builder()
            .branchCode(5005)
            .branchName("Central Bank")
            .routingNumber(555666777)
            .branchAddress(branchAddress)
            .build();

        assertEquals(Integer.valueOf(5005), bankInfo.getBranchCode());
        assertEquals("Central Bank", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(555666777), bankInfo.getRoutingNumber());
        assertEquals(branchAddress, bankInfo.getBranchAddress());
    }

    @Test
    void bankInfo_setters_workCorrectly() {
        BankInfo bankInfo = new BankInfo();
        Address branchAddress = Address.builder()
            .address1("600 Finance Blvd")
            .city("Money City")
            .state("NV")
            .zip("89101")
            .country("USA")
            .build();

        bankInfo.setBranchCode(6006);
        bankInfo.setBranchName("Updated Bank");
        bankInfo.setRoutingNumber(888999000);
        bankInfo.setBranchAddress(branchAddress);

        assertEquals(Integer.valueOf(6006), bankInfo.getBranchCode());
        assertEquals("Updated Bank", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(888999000), bankInfo.getRoutingNumber());
        assertEquals(branchAddress, bankInfo.getBranchAddress());
    }

    @Test
    void customerAccountXRef_builderAndGetters_workCorrectly() {
        CustomerAccountXRef xref = CustomerAccountXRef.builder()
            .customerNumber(111L)
            .accountNumber(222L)
            .build();

        assertEquals(Long.valueOf(111L), xref.getCustomerNumber());
        assertEquals(Long.valueOf(222L), xref.getAccountNumber());
    }

    @Test
    void customerAccountXRef_setters_workCorrectly() {
        CustomerAccountXRef xref = new CustomerAccountXRef();
        
        xref.setCustomerNumber(333L);
        xref.setAccountNumber(444L);

        assertEquals(Long.valueOf(333L), xref.getCustomerNumber());
        assertEquals(Long.valueOf(444L), xref.getAccountNumber());
    }

    @Test
    void customer_equalsAndHashCode_workCorrectly() {
        Contact contact1 = Contact.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();

        Address address1 = Address.builder()
            .address1("123 Main St")
            .city("Anytown")
            .state("NY")
            .zip("12345")
            .country("USA")
            .build();

        Customer customer1 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contact1)
            .customerAddress(address1)
            .build();

        Customer customer2 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contact1)
            .customerAddress(address1)
            .build();

        Customer customer3 = Customer.builder()
            .customerNumber(456L)
            .firstName("Jane")
            .lastName("Smith")
            .status("INACTIVE")
            .contactDetails(contact1)
            .customerAddress(address1)
            .build();

        assertEquals(customer1, customer2);
        assertNotEquals(customer1, customer3);
        assertNotEquals(customer1, null);
        assertNotEquals(customer1, "not a customer");
        
        assertEquals(customer1.hashCode(), customer2.hashCode());
        assertNotEquals(customer1.hashCode(), customer3.hashCode());
        
        assertNotNull(customer1.toString());
        assertTrue(customer1.toString().contains("John"));
        assertTrue(customer1.toString().contains("Doe"));
    }

    @Test
    void account_equalsAndHashCode_workCorrectly() {
        Address branchAddress = Address.builder()
            .address1("100 Bank St")
            .city("Financial District")
            .state("NY")
            .zip("10001")
            .country("USA")
            .build();

        BankInfo bankInfo = BankInfo.builder()
            .branchCode(1001)
            .branchName("Test Bank")
            .routingNumber(123456789)
            .branchAddress(branchAddress)
            .build();

        Account account1 = Account.builder()
            .accountNumber(789L)
            .accountType("SAVINGS")
            .accountBalance(2500.0)
            .accountStatus("ACTIVE")
            .bankInformation(bankInfo)
            .build();

        Account account2 = Account.builder()
            .accountNumber(789L)
            .accountType("SAVINGS")
            .accountBalance(2500.0)
            .accountStatus("ACTIVE")
            .bankInformation(bankInfo)
            .build();

        Account account3 = Account.builder()
            .accountNumber(999L)
            .accountType("CHECKING")
            .accountBalance(1500.0)
            .accountStatus("INACTIVE")
            .bankInformation(bankInfo)
            .build();

        assertEquals(account1, account2);
        assertNotEquals(account1, account3);
        assertNotEquals(account1, null);
        assertNotEquals(account1, "not an account");
        
        assertEquals(account1.hashCode(), account2.hashCode());
        assertNotEquals(account1.hashCode(), account3.hashCode());
        
        assertNotNull(account1.toString());
        assertTrue(account1.toString().contains("789"));
        assertTrue(account1.toString().contains("SAVINGS"));
    }

    @Test
    void transaction_equalsAndHashCode_workCorrectly() {
        Date txDate = new Date();
        
        Transaction transaction1 = Transaction.builder()
            .accountNumber(123L)
            .txAmount(250.0)
            .txType("CREDIT")
            .txDateTime(txDate)
            .build();

        Transaction transaction2 = Transaction.builder()
            .accountNumber(123L)
            .txAmount(250.0)
            .txType("CREDIT")
            .txDateTime(txDate)
            .build();

        Transaction transaction3 = Transaction.builder()
            .accountNumber(456L)
            .txAmount(100.0)
            .txType("DEBIT")
            .txDateTime(txDate)
            .build();

        assertEquals(transaction1, transaction2);
        assertNotEquals(transaction1, transaction3);
        assertNotEquals(transaction1, null);
        assertNotEquals(transaction1, "not a transaction");
        
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
        assertNotEquals(transaction1.hashCode(), transaction3.hashCode());
        
        assertNotNull(transaction1.toString());
        assertTrue(transaction1.toString().contains("123"));
        assertTrue(transaction1.toString().contains("CREDIT"));
    }

    @Test
    void contact_equalsAndHashCode_workCorrectly() {
        Contact contact1 = Contact.builder()
            .emailId("contact@test.com")
            .homePhone("111-222-3333")
            .workPhone("444-555-6666")
            .build();

        Contact contact2 = Contact.builder()
            .emailId("contact@test.com")
            .homePhone("111-222-3333")
            .workPhone("444-555-6666")
            .build();

        Contact contact3 = Contact.builder()
            .emailId("different@test.com")
            .homePhone("777-888-9999")
            .workPhone("000-111-2222")
            .build();

        assertEquals(contact1, contact2);
        assertNotEquals(contact1, contact3);
        assertNotEquals(contact1, null);
        assertNotEquals(contact1, "not a contact");
        
        assertEquals(contact1.hashCode(), contact2.hashCode());
        assertNotEquals(contact1.hashCode(), contact3.hashCode());
        
        assertNotNull(contact1.toString());
        assertTrue(contact1.toString().contains("contact@test.com"));
    }

    @Test
    void address_equalsAndHashCode_workCorrectly() {
        Address address1 = Address.builder()
            .address1("789 Test Ave")
            .address2("Unit 5C")
            .city("Test City")
            .state("FL")
            .zip("33101")
            .country("USA")
            .build();

        Address address2 = Address.builder()
            .address1("789 Test Ave")
            .address2("Unit 5C")
            .city("Test City")
            .state("FL")
            .zip("33101")
            .country("USA")
            .build();

        Address address3 = Address.builder()
            .address1("321 New St")
            .city("New City")
            .state("WA")
            .zip("98101")
            .country("USA")
            .build();

        assertEquals(address1, address2);
        assertNotEquals(address1, address3);
        assertNotEquals(address1, null);
        assertNotEquals(address1, "not an address");
        
        assertEquals(address1.hashCode(), address2.hashCode());
        assertNotEquals(address1.hashCode(), address3.hashCode());
        
        assertNotNull(address1.toString());
        assertTrue(address1.toString().contains("Test City"));
        assertTrue(address1.toString().contains("FL"));
    }

    @Test
    void bankInfo_equalsAndHashCode_workCorrectly() {
        Address branchAddress1 = Address.builder()
            .address1("500 Bank Plaza")
            .city("Finance City")
            .state("IL")
            .zip("60601")
            .country("USA")
            .build();

        Address branchAddress2 = Address.builder()
            .address1("600 Finance Blvd")
            .city("Money City")
            .state("NV")
            .zip("89101")
            .country("USA")
            .build();

        BankInfo bankInfo1 = BankInfo.builder()
            .branchCode(5005)
            .branchName("Central Bank")
            .routingNumber(555666777)
            .branchAddress(branchAddress1)
            .build();

        BankInfo bankInfo2 = BankInfo.builder()
            .branchCode(5005)
            .branchName("Central Bank")
            .routingNumber(555666777)
            .branchAddress(branchAddress1)
            .build();

        BankInfo bankInfo3 = BankInfo.builder()
            .branchCode(6006)
            .branchName("Updated Bank")
            .routingNumber(888999000)
            .branchAddress(branchAddress2)
            .build();

        assertEquals(bankInfo1, bankInfo2);
        assertNotEquals(bankInfo1, bankInfo3);
        assertNotEquals(bankInfo1, null);
        assertNotEquals(bankInfo1, "not a bank info");
        
        assertEquals(bankInfo1.hashCode(), bankInfo2.hashCode());
        assertNotEquals(bankInfo1.hashCode(), bankInfo3.hashCode());
        
        assertNotNull(bankInfo1.toString());
        assertTrue(bankInfo1.toString().contains("Central Bank"));
        assertTrue(bankInfo1.toString().contains("5005"));
    }

    @Test
    void customerAccountXRef_equalsAndHashCode_workCorrectly() {
        CustomerAccountXRef xref1 = CustomerAccountXRef.builder()
            .customerNumber(111L)
            .accountNumber(222L)
            .build();

        CustomerAccountXRef xref2 = CustomerAccountXRef.builder()
            .customerNumber(111L)
            .accountNumber(222L)
            .build();

        CustomerAccountXRef xref3 = CustomerAccountXRef.builder()
            .customerNumber(333L)
            .accountNumber(444L)
            .build();

        assertEquals(xref1, xref2);
        assertNotEquals(xref1, xref3);
        assertNotEquals(xref1, null);
        assertNotEquals(xref1, "not a xref");
        
        assertEquals(xref1.hashCode(), xref2.hashCode());
        assertNotEquals(xref1.hashCode(), xref3.hashCode());
        
        assertNotNull(xref1.toString());
        assertTrue(xref1.toString().contains("111"));
        assertTrue(xref1.toString().contains("222"));
    }

    @Test
    void customer_edgeCases_nullFields() {
        Customer customer1 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(null)
            .customerAddress(null)
            .build();

        Customer customer2 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(null)
            .customerAddress(null)
            .build();

        Customer customer3 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(Contact.builder().emailId("test@test.com").build())
            .customerAddress(null)
            .build();

        assertEquals(customer1, customer2);
        assertNotEquals(customer1, customer3);
        assertEquals(customer1.hashCode(), customer2.hashCode());
        assertNotNull(customer1.toString());
    }

    @Test
    void customer_edgeCases_partialNullFields() {
        Contact contact = Contact.builder()
            .emailId("test@example.com")
            .homePhone(null)
            .workPhone("098-765-4321")
            .build();

        Address address = Address.builder()
            .address1("123 Main St")
            .address2(null)
            .city("Anytown")
            .state(null)
            .zip("12345")
            .country("USA")
            .build();

        Customer customer1 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName(null)
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contact)
            .customerAddress(address)
            .build();

        Customer customer2 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName(null)
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contact)
            .customerAddress(address)
            .build();

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
        assertNotNull(customer1.toString());
    }

    @Test
    void account_edgeCases_nullBankInfo() {
        Account account1 = Account.builder()
            .accountNumber(789L)
            .accountType("SAVINGS")
            .accountBalance(2500.0)
            .accountStatus("ACTIVE")
            .bankInformation(null)
            .build();

        Account account2 = Account.builder()
            .accountNumber(789L)
            .accountType("SAVINGS")
            .accountBalance(2500.0)
            .accountStatus("ACTIVE")
            .bankInformation(null)
            .build();

        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
        assertNotNull(account1.toString());
    }

    @Test
    void bankInfo_edgeCases_nullAddress() {
        BankInfo bankInfo1 = BankInfo.builder()
            .branchCode(5005)
            .branchName("Central Bank")
            .routingNumber(555666777)
            .branchAddress(null)
            .build();

        BankInfo bankInfo2 = BankInfo.builder()
            .branchCode(5005)
            .branchName("Central Bank")
            .routingNumber(555666777)
            .branchAddress(null)
            .build();

        assertEquals(bankInfo1, bankInfo2);
        assertEquals(bankInfo1.hashCode(), bankInfo2.hashCode());
        assertNotNull(bankInfo1.toString());
    }

    @Test
    void contact_edgeCases_allNullFields() {
        Contact contact1 = Contact.builder()
            .emailId(null)
            .homePhone(null)
            .workPhone(null)
            .build();

        Contact contact2 = Contact.builder()
            .emailId(null)
            .homePhone(null)
            .workPhone(null)
            .build();

        assertEquals(contact1, contact2);
        assertEquals(contact1.hashCode(), contact2.hashCode());
        assertNotNull(contact1.toString());
    }

    @Test
    void address_edgeCases_mixedNullFields() {
        Address address1 = Address.builder()
            .address1("789 Test Ave")
            .address2(null)
            .city(null)
            .state("FL")
            .zip(null)
            .country("USA")
            .build();

        Address address2 = Address.builder()
            .address1("789 Test Ave")
            .address2(null)
            .city(null)
            .state("FL")
            .zip(null)
            .country("USA")
            .build();

        Address address3 = Address.builder()
            .address1("789 Test Ave")
            .address2("Unit 1")
            .city(null)
            .state("FL")
            .zip(null)
            .country("USA")
            .build();

        assertEquals(address1, address2);
        assertNotEquals(address1, address3);
        assertEquals(address1.hashCode(), address2.hashCode());
        assertNotNull(address1.toString());
    }

    @Test
    void transaction_edgeCases_nullDateTime() {
        Transaction transaction1 = Transaction.builder()
            .accountNumber(123L)
            .txAmount(250.0)
            .txType("CREDIT")
            .txDateTime(null)
            .build();

        Transaction transaction2 = Transaction.builder()
            .accountNumber(123L)
            .txAmount(250.0)
            .txType("CREDIT")
            .txDateTime(null)
            .build();

        assertEquals(transaction1, transaction2);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
        assertNotNull(transaction1.toString());
    }

    @Test
    void customer_builderEdgeCases() {
        Customer.CustomerBuilder builder = Customer.builder();
        
        Customer customer1 = builder
            .customerNumber(123L)
            .firstName("John")
            .build();
        
        Customer customer2 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName(null)
            .middleName(null)
            .status(null)
            .contactDetails(null)
            .customerAddress(null)
            .createDateTime(null)
            .updateDateTime(null)
            .build();

        assertEquals(customer1.getCustomerNumber(), customer2.getCustomerNumber());
        assertEquals(customer1.getFirstName(), customer2.getFirstName());
        assertNull(customer2.getLastName());
        assertNull(customer2.getMiddleName());
        assertNull(customer2.getStatus());
    }

    @Test
    void account_builderEdgeCases() {
        Account.AccountBuilder builder = Account.builder();
        
        Account account1 = builder
            .accountNumber(789L)
            .accountType("SAVINGS")
            .build();
        
        Account account2 = Account.builder()
            .accountNumber(789L)
            .accountType("SAVINGS")
            .accountBalance(null)
            .accountStatus(null)
            .bankInformation(null)
            .createDateTime(null)
            .updateDateTime(null)
            .build();

        assertEquals(account1.getAccountNumber(), account2.getAccountNumber());
        assertEquals(account1.getAccountType(), account2.getAccountType());
        assertNull(account2.getAccountBalance());
        assertNull(account2.getAccountStatus());
    }

    @Test
    void customer_equalsEdgeCases_sameInstance() {
        Customer customer = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .build();

        assertEquals(customer, customer);
        assertTrue(customer.equals(customer));
    }

    @Test
    void account_equalsEdgeCases_sameInstance() {
        Account account = Account.builder()
            .accountNumber(789L)
            .accountType("SAVINGS")
            .build();

        assertEquals(account, account);
        assertTrue(account.equals(account));
    }

    @Test
    void customer_canEqual_method() {
        Customer customer1 = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .build();
        
        Customer customer2 = Customer.builder()
            .customerNumber(456L)
            .firstName("Jane")
            .build();

        assertTrue(customer1.canEqual(customer2));
        assertTrue(customer1.canEqual(customer1));
        assertFalse(customer1.canEqual("not a customer"));
        assertFalse(customer1.canEqual(null));
    }

    @Test
    void account_canEqual_method() {
        Account account1 = Account.builder()
            .accountNumber(123L)
            .accountType("CHECKING")
            .build();
        
        Account account2 = Account.builder()
            .accountNumber(456L)
            .accountType("SAVINGS")
            .build();

        assertTrue(account1.canEqual(account2));
        assertTrue(account1.canEqual(account1));
        assertFalse(account1.canEqual("not an account"));
        assertFalse(account1.canEqual(null));
    }

    @Test
    void address_canEqual_method() {
        Address address1 = Address.builder()
            .address1("123 Main St")
            .city("Anytown")
            .build();
        
        Address address2 = Address.builder()
            .address1("456 Oak Ave")
            .city("Other City")
            .build();

        assertTrue(address1.canEqual(address2));
        assertTrue(address1.canEqual(address1));
        assertFalse(address1.canEqual("not an address"));
        assertFalse(address1.canEqual(null));
    }

    @Test
    void transaction_canEqual_method() {
        Transaction transaction1 = Transaction.builder()
            .accountNumber(123L)
            .txAmount(100.0)
            .build();
        
        Transaction transaction2 = Transaction.builder()
            .accountNumber(456L)
            .txAmount(200.0)
            .build();

        assertTrue(transaction1.canEqual(transaction2));
        assertTrue(transaction1.canEqual(transaction1));
        assertFalse(transaction1.canEqual("not a transaction"));
        assertFalse(transaction1.canEqual(null));
    }

    @Test
    void contact_canEqual_method() {
        Contact contact1 = Contact.builder()
            .emailId("test1@example.com")
            .build();
        
        Contact contact2 = Contact.builder()
            .emailId("test2@example.com")
            .build();

        assertTrue(contact1.canEqual(contact2));
        assertTrue(contact1.canEqual(contact1));
        assertFalse(contact1.canEqual("not a contact"));
        assertFalse(contact1.canEqual(null));
    }

    @Test
    void bankInfo_canEqual_method() {
        BankInfo bankInfo1 = BankInfo.builder()
            .branchCode(1001)
            .branchName("Branch 1")
            .build();
        
        BankInfo bankInfo2 = BankInfo.builder()
            .branchCode(2002)
            .branchName("Branch 2")
            .build();

        assertTrue(bankInfo1.canEqual(bankInfo2));
        assertTrue(bankInfo1.canEqual(bankInfo1));
        assertFalse(bankInfo1.canEqual("not a bank info"));
        assertFalse(bankInfo1.canEqual(null));
    }
}
