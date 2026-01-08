package com.coding.exercise.bankapp.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

public class DomainTest {

    @Test
    public void testCustomerDetailsBuilder() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(addressDetails)
                .contactDetails(contactDetails)
                .build();

        assertEquals("John", customerDetails.getFirstName());
        assertEquals("M", customerDetails.getMiddleName());
        assertEquals("Doe", customerDetails.getLastName());
        assertEquals(Long.valueOf(12345L), customerDetails.getCustomerNumber());
        assertEquals("Active", customerDetails.getStatus());
        assertNotNull(customerDetails.getCustomerAddress());
        assertNotNull(customerDetails.getContactDetails());
    }

    @Test
    public void testCustomerDetailsSettersAndGetters() {
        CustomerDetails customerDetails = new CustomerDetails();

        customerDetails.setFirstName("Jane");
        customerDetails.setMiddleName("A");
        customerDetails.setLastName("Smith");
        customerDetails.setCustomerNumber(67890L);
        customerDetails.setStatus("Inactive");

        assertEquals("Jane", customerDetails.getFirstName());
        assertEquals("A", customerDetails.getMiddleName());
        assertEquals("Smith", customerDetails.getLastName());
        assertEquals(Long.valueOf(67890L), customerDetails.getCustomerNumber());
        assertEquals("Inactive", customerDetails.getStatus());
    }

    @Test
    public void testCustomerDetailsAllArgsConstructor() {
        AddressDetails addressDetails = new AddressDetails();
        ContactDetails contactDetails = new ContactDetails();

        CustomerDetails customerDetails = new CustomerDetails("John", "Doe", "M", 12345L, "Active", addressDetails, contactDetails);

        assertEquals("John", customerDetails.getFirstName());
        assertEquals("Doe", customerDetails.getLastName());
        assertEquals("M", customerDetails.getMiddleName());
        assertEquals(Long.valueOf(12345L), customerDetails.getCustomerNumber());
        assertEquals("Active", customerDetails.getStatus());
        assertEquals(addressDetails, customerDetails.getCustomerAddress());
        assertEquals(contactDetails, customerDetails.getContactDetails());
    }

    @Test
    public void testAccountInformationBuilder() {
        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddressDetails)
                .build();

        Date now = new Date();
        AccountInformation accountInformation = AccountInformation.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInformation)
                .accountCreated(now)
                .build();

        assertEquals(Long.valueOf(1000001L), accountInformation.getAccountNumber());
        assertEquals("Savings", accountInformation.getAccountType());
        assertEquals("Active", accountInformation.getAccountStatus());
        assertEquals(Double.valueOf(5000.00), accountInformation.getAccountBalance());
        assertNotNull(accountInformation.getBankInformation());
        assertEquals(now, accountInformation.getAccountCreated());
    }

    @Test
    public void testAccountInformationSettersAndGetters() {
        AccountInformation accountInformation = new AccountInformation();
        Date now = new Date();
        BankInformation bankInformation = new BankInformation();

        accountInformation.setAccountNumber(2000002L);
        accountInformation.setAccountType("Checking");
        accountInformation.setAccountStatus("Inactive");
        accountInformation.setAccountBalance(10000.00);
        accountInformation.setBankInformation(bankInformation);
        accountInformation.setAccountCreated(now);

        assertEquals(Long.valueOf(2000002L), accountInformation.getAccountNumber());
        assertEquals("Checking", accountInformation.getAccountType());
        assertEquals("Inactive", accountInformation.getAccountStatus());
        assertEquals(Double.valueOf(10000.00), accountInformation.getAccountBalance());
        assertEquals(bankInformation, accountInformation.getBankInformation());
        assertEquals(now, accountInformation.getAccountCreated());
    }

    @Test
    public void testAccountInformationAllArgsConstructor() {
        Date now = new Date();
        BankInformation bankInformation = new BankInformation();

        AccountInformation accountInformation = new AccountInformation(1000001L, bankInformation, "Active", "Savings", 5000.00, now);

        assertEquals(Long.valueOf(1000001L), accountInformation.getAccountNumber());
        assertEquals(bankInformation, accountInformation.getBankInformation());
        assertEquals("Active", accountInformation.getAccountStatus());
        assertEquals("Savings", accountInformation.getAccountType());
        assertEquals(Double.valueOf(5000.00), accountInformation.getAccountBalance());
        assertEquals(now, accountInformation.getAccountCreated());
    }

    @Test
    public void testTransactionDetailsBuilder() {
        Date now = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(1000001L)
                .txDateTime(now)
                .txType("DEBIT")
                .txAmount(100.00)
                .build();

        assertEquals(Long.valueOf(1000001L), transactionDetails.getAccountNumber());
        assertEquals(now, transactionDetails.getTxDateTime());
        assertEquals("DEBIT", transactionDetails.getTxType());
        assertEquals(Double.valueOf(100.00), transactionDetails.getTxAmount());
    }

    @Test
    public void testTransactionDetailsSettersAndGetters() {
        TransactionDetails transactionDetails = new TransactionDetails();
        Date now = new Date();

        transactionDetails.setAccountNumber(2000002L);
        transactionDetails.setTxDateTime(now);
        transactionDetails.setTxType("CREDIT");
        transactionDetails.setTxAmount(200.00);

        assertEquals(Long.valueOf(2000002L), transactionDetails.getAccountNumber());
        assertEquals(now, transactionDetails.getTxDateTime());
        assertEquals("CREDIT", transactionDetails.getTxType());
        assertEquals(Double.valueOf(200.00), transactionDetails.getTxAmount());
    }

    @Test
    public void testTransactionDetailsAllArgsConstructor() {
        Date now = new Date();

        TransactionDetails transactionDetails = new TransactionDetails(1000001L, now, "DEBIT", 100.00);

        assertEquals(Long.valueOf(1000001L), transactionDetails.getAccountNumber());
        assertEquals(now, transactionDetails.getTxDateTime());
        assertEquals("DEBIT", transactionDetails.getTxType());
        assertEquals(Double.valueOf(100.00), transactionDetails.getTxAmount());
    }

    @Test
    public void testTransferDetailsSettersAndGetters() {
        TransferDetails transferDetails = new TransferDetails();

        transferDetails.setFromAccountNumber(1000001L);
        transferDetails.setToAccountNumber(2000002L);
        transferDetails.setTransferAmount(500.00);

        assertEquals(Long.valueOf(1000001L), transferDetails.getFromAccountNumber());
        assertEquals(Long.valueOf(2000002L), transferDetails.getToAccountNumber());
        assertEquals(Double.valueOf(500.00), transferDetails.getTransferAmount());
    }

    @Test
    public void testTransferDetailsAllArgsConstructor() {
        TransferDetails transferDetails = new TransferDetails(1000001L, 2000002L, 500.00);

        assertEquals(Long.valueOf(1000001L), transferDetails.getFromAccountNumber());
        assertEquals(Long.valueOf(2000002L), transferDetails.getToAccountNumber());
        assertEquals(Double.valueOf(500.00), transferDetails.getTransferAmount());
    }

    @Test
    public void testTransferDetailsNoArgsConstructor() {
        TransferDetails transferDetails = new TransferDetails();

        assertNull(transferDetails.getFromAccountNumber());
        assertNull(transferDetails.getToAccountNumber());
        assertNull(transferDetails.getTransferAmount());
    }

    @Test
    public void testAddressDetailsBuilder() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        assertEquals("123 Main St", addressDetails.getAddress1());
        assertEquals("Apt 4", addressDetails.getAddress2());
        assertEquals("New York", addressDetails.getCity());
        assertEquals("NY", addressDetails.getState());
        assertEquals("10001", addressDetails.getZip());
        assertEquals("USA", addressDetails.getCountry());
    }

    @Test
    public void testAddressDetailsSettersAndGetters() {
        AddressDetails addressDetails = new AddressDetails();

        addressDetails.setAddress1("456 Oak Ave");
        addressDetails.setAddress2("Suite 100");
        addressDetails.setCity("Los Angeles");
        addressDetails.setState("CA");
        addressDetails.setZip("90001");
        addressDetails.setCountry("USA");

        assertEquals("456 Oak Ave", addressDetails.getAddress1());
        assertEquals("Suite 100", addressDetails.getAddress2());
        assertEquals("Los Angeles", addressDetails.getCity());
        assertEquals("CA", addressDetails.getState());
        assertEquals("90001", addressDetails.getZip());
        assertEquals("USA", addressDetails.getCountry());
    }

    @Test
    public void testAddressDetailsAllArgsConstructor() {
        AddressDetails addressDetails = new AddressDetails("123 Main St", "Apt 4", "New York", "NY", "10001", "USA");

        assertEquals("123 Main St", addressDetails.getAddress1());
        assertEquals("Apt 4", addressDetails.getAddress2());
        assertEquals("New York", addressDetails.getCity());
        assertEquals("NY", addressDetails.getState());
        assertEquals("10001", addressDetails.getZip());
        assertEquals("USA", addressDetails.getCountry());
    }

    @Test
    public void testContactDetailsBuilder() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        assertEquals("test@example.com", contactDetails.getEmailId());
        assertEquals("555-1234", contactDetails.getHomePhone());
        assertEquals("555-5678", contactDetails.getWorkPhone());
    }

    @Test
    public void testContactDetailsSettersAndGetters() {
        ContactDetails contactDetails = new ContactDetails();

        contactDetails.setEmailId("jane@example.com");
        contactDetails.setHomePhone("555-9999");
        contactDetails.setWorkPhone("555-8888");

        assertEquals("jane@example.com", contactDetails.getEmailId());
        assertEquals("555-9999", contactDetails.getHomePhone());
        assertEquals("555-8888", contactDetails.getWorkPhone());
    }

    @Test
    public void testContactDetailsAllArgsConstructor() {
        ContactDetails contactDetails = new ContactDetails("test@example.com", "555-1234", "555-5678");

        assertEquals("test@example.com", contactDetails.getEmailId());
        assertEquals("555-1234", contactDetails.getHomePhone());
        assertEquals("555-5678", contactDetails.getWorkPhone());
    }

    @Test
    public void testBankInformationBuilder() {
        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddressDetails)
                .build();

        assertEquals("Main Branch", bankInformation.getBranchName());
        assertEquals(Integer.valueOf(1001), bankInformation.getBranchCode());
        assertEquals(Integer.valueOf(123456789), bankInformation.getRoutingNumber());
        assertNotNull(bankInformation.getBranchAddress());
    }

    @Test
    public void testBankInformationSettersAndGetters() {
        BankInformation bankInformation = new BankInformation();
        AddressDetails branchAddressDetails = new AddressDetails();

        bankInformation.setBranchName("Downtown Branch");
        bankInformation.setBranchCode(2002);
        bankInformation.setRoutingNumber(987654321);
        bankInformation.setBranchAddress(branchAddressDetails);

        assertEquals("Downtown Branch", bankInformation.getBranchName());
        assertEquals(Integer.valueOf(2002), bankInformation.getBranchCode());
        assertEquals(Integer.valueOf(987654321), bankInformation.getRoutingNumber());
        assertEquals(branchAddressDetails, bankInformation.getBranchAddress());
    }

    @Test
    public void testBankInformationAllArgsConstructor() {
        AddressDetails branchAddressDetails = new AddressDetails();

        BankInformation bankInformation = new BankInformation("Main Branch", 1001, branchAddressDetails, 123456789);

        assertEquals("Main Branch", bankInformation.getBranchName());
        assertEquals(Integer.valueOf(1001), bankInformation.getBranchCode());
        assertEquals(branchAddressDetails, bankInformation.getBranchAddress());
        assertEquals(Integer.valueOf(123456789), bankInformation.getRoutingNumber());
    }

    @Test
    public void testCustomerDetailsNoArgsConstructor() {
        CustomerDetails customerDetails = new CustomerDetails();

        assertNull(customerDetails.getFirstName());
        assertNull(customerDetails.getLastName());
        assertNull(customerDetails.getMiddleName());
        assertNull(customerDetails.getCustomerNumber());
        assertNull(customerDetails.getStatus());
        assertNull(customerDetails.getCustomerAddress());
        assertNull(customerDetails.getContactDetails());
    }

    @Test
    public void testAccountInformationNoArgsConstructor() {
        AccountInformation accountInformation = new AccountInformation();

        assertNull(accountInformation.getAccountNumber());
        assertNull(accountInformation.getAccountType());
        assertNull(accountInformation.getAccountStatus());
        assertNull(accountInformation.getAccountBalance());
        assertNull(accountInformation.getBankInformation());
        assertNull(accountInformation.getAccountCreated());
    }

    @Test
    public void testTransactionDetailsNoArgsConstructor() {
        TransactionDetails transactionDetails = new TransactionDetails();

        assertNull(transactionDetails.getAccountNumber());
        assertNull(transactionDetails.getTxDateTime());
        assertNull(transactionDetails.getTxType());
        assertNull(transactionDetails.getTxAmount());
    }

    @Test
    public void testAddressDetailsNoArgsConstructor() {
        AddressDetails addressDetails = new AddressDetails();

        assertNull(addressDetails.getAddress1());
        assertNull(addressDetails.getAddress2());
        assertNull(addressDetails.getCity());
        assertNull(addressDetails.getState());
        assertNull(addressDetails.getZip());
        assertNull(addressDetails.getCountry());
    }

    @Test
    public void testContactDetailsNoArgsConstructor() {
        ContactDetails contactDetails = new ContactDetails();

        assertNull(contactDetails.getEmailId());
        assertNull(contactDetails.getHomePhone());
        assertNull(contactDetails.getWorkPhone());
    }

    @Test
    public void testBankInformationNoArgsConstructor() {
        BankInformation bankInformation = new BankInformation();

        assertNull(bankInformation.getBranchName());
        assertNull(bankInformation.getBranchCode());
        assertNull(bankInformation.getRoutingNumber());
        assertNull(bankInformation.getBranchAddress());
    }
}
