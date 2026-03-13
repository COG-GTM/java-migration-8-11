package com.coding.exercise.bankapp.domain;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DomainModelTest {

    // ========== CustomerDetails Tests ==========

    @Test
    public void testCustomerDetails_Builder() {
        CustomerDetails customer = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(12345L)
                .status("Active")
                .build();

        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("M", customer.getMiddleName());
        assertEquals(Long.valueOf(12345L), customer.getCustomerNumber());
        assertEquals("Active", customer.getStatus());
    }

    @Test
    public void testCustomerDetails_SettersAndGetters() {
        CustomerDetails customer = new CustomerDetails();
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setMiddleName("A");
        customer.setCustomerNumber(67890L);
        customer.setStatus("Inactive");

        assertEquals("Jane", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("A", customer.getMiddleName());
        assertEquals(Long.valueOf(67890L), customer.getCustomerNumber());
        assertEquals("Inactive", customer.getStatus());
    }

    @Test
    public void testCustomerDetails_WithAddressAndContact() {
        AddressDetails address = AddressDetails.builder()
                .address1("123 Main St")
                .city("Springfield")
                .build();
        ContactDetails contact = ContactDetails.builder()
                .emailId("test@test.com")
                .build();

        CustomerDetails customer = CustomerDetails.builder()
                .firstName("John")
                .customerAddress(address)
                .contactDetails(contact)
                .build();

        assertNotNull(customer.getCustomerAddress());
        assertNotNull(customer.getContactDetails());
        assertEquals("123 Main St", customer.getCustomerAddress().getAddress1());
        assertEquals("test@test.com", customer.getContactDetails().getEmailId());
    }

    @Test
    public void testCustomerDetails_NoArgsConstructor() {
        CustomerDetails customer = new CustomerDetails();
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getCustomerNumber());
    }

    @Test
    public void testCustomerDetails_AllArgsConstructor() {
        AddressDetails address = new AddressDetails();
        ContactDetails contact = new ContactDetails();
        CustomerDetails customer = new CustomerDetails("John", "Doe", "M", 12345L, "Active", address, contact);
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
    }

    // ========== AccountInformation Tests ==========

    @Test
    public void testAccountInformation_Builder() {
        AccountInformation account = AccountInformation.builder()
                .accountNumber(100001L)
                .accountStatus("Active")
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .build();

        assertEquals(Long.valueOf(100001L), account.getAccountNumber());
        assertEquals("Active", account.getAccountStatus());
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(5000.0, account.getAccountBalance(), 0.01);
    }

    @Test
    public void testAccountInformation_SettersAndGetters() {
        AccountInformation account = new AccountInformation();
        account.setAccountNumber(200001L);
        account.setAccountStatus("Closed");
        account.setAccountType("CHECKING");
        account.setAccountBalance(0.0);
        Date now = new Date();
        account.setAccountCreated(now);

        assertEquals(Long.valueOf(200001L), account.getAccountNumber());
        assertEquals("Closed", account.getAccountStatus());
        assertEquals("CHECKING", account.getAccountType());
        assertEquals(0.0, account.getAccountBalance(), 0.01);
        assertEquals(now, account.getAccountCreated());
    }

    @Test
    public void testAccountInformation_WithBankInfo() {
        BankInformation bankInfo = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .build();

        AccountInformation account = AccountInformation.builder()
                .accountNumber(100001L)
                .bankInformation(bankInfo)
                .build();

        assertNotNull(account.getBankInformation());
        assertEquals("Main Branch", account.getBankInformation().getBranchName());
    }

    // ========== TransactionDetails Tests ==========

    @Test
    public void testTransactionDetails_Builder() {
        Date now = new Date();
        TransactionDetails tx = TransactionDetails.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(1000.0)
                .txDateTime(now)
                .build();

        assertEquals(Long.valueOf(100001L), tx.getAccountNumber());
        assertEquals("CREDIT", tx.getTxType());
        assertEquals(1000.0, tx.getTxAmount(), 0.01);
        assertEquals(now, tx.getTxDateTime());
    }

    @Test
    public void testTransactionDetails_SettersAndGetters() {
        TransactionDetails tx = new TransactionDetails();
        Date now = new Date();
        tx.setAccountNumber(100001L);
        tx.setTxType("DEBIT");
        tx.setTxAmount(500.0);
        tx.setTxDateTime(now);

        assertEquals(Long.valueOf(100001L), tx.getAccountNumber());
        assertEquals("DEBIT", tx.getTxType());
        assertEquals(500.0, tx.getTxAmount(), 0.01);
    }

    // ========== TransferDetails Tests ==========

    @Test
    public void testTransferDetails_SettersAndGetters() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100001L);
        transfer.setToAccountNumber(100002L);
        transfer.setTransferAmount(2000.0);

        assertEquals(Long.valueOf(100001L), transfer.getFromAccountNumber());
        assertEquals(Long.valueOf(100002L), transfer.getToAccountNumber());
        assertEquals(2000.0, transfer.getTransferAmount(), 0.01);
    }

    @Test
    public void testTransferDetails_AllArgsConstructor() {
        TransferDetails transfer = new TransferDetails(100001L, 100002L, 1500.0);

        assertEquals(Long.valueOf(100001L), transfer.getFromAccountNumber());
        assertEquals(Long.valueOf(100002L), transfer.getToAccountNumber());
        assertEquals(1500.0, transfer.getTransferAmount(), 0.01);
    }

    @Test
    public void testTransferDetails_NoArgsConstructor() {
        TransferDetails transfer = new TransferDetails();
        assertNull(transfer.getFromAccountNumber());
        assertNull(transfer.getToAccountNumber());
        assertNull(transfer.getTransferAmount());
    }

    // ========== AddressDetails Tests ==========

    @Test
    public void testAddressDetails_Builder() {
        AddressDetails address = AddressDetails.builder()
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
    public void testAddressDetails_SettersAndGetters() {
        AddressDetails address = new AddressDetails();
        address.setAddress1("456 Oak Ave");
        address.setCity("Chicago");
        address.setState("IL");
        address.setZip("60601");
        address.setCountry("US");

        assertEquals("456 Oak Ave", address.getAddress1());
        assertEquals("Chicago", address.getCity());
    }

    // ========== ContactDetails Tests ==========

    @Test
    public void testContactDetails_Builder() {
        ContactDetails contact = ContactDetails.builder()
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        assertEquals("john@example.com", contact.getEmailId());
        assertEquals("555-1234", contact.getHomePhone());
        assertEquals("555-5678", contact.getWorkPhone());
    }

    @Test
    public void testContactDetails_SettersAndGetters() {
        ContactDetails contact = new ContactDetails();
        contact.setEmailId("jane@example.com");
        contact.setHomePhone("555-9999");
        contact.setWorkPhone("555-0000");

        assertEquals("jane@example.com", contact.getEmailId());
        assertEquals("555-9999", contact.getHomePhone());
        assertEquals("555-0000", contact.getWorkPhone());
    }

    // ========== BankInformation Tests ==========

    @Test
    public void testBankInformation_Builder() {
        BankInformation bankInfo = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .build();

        assertEquals("Main Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(1001), bankInfo.getBranchCode());
        assertEquals(Integer.valueOf(123456789), bankInfo.getRoutingNumber());
    }

    @Test
    public void testBankInformation_WithBranchAddress() {
        AddressDetails branchAddress = AddressDetails.builder()
                .address1("789 Bank St")
                .city("Springfield")
                .build();

        BankInformation bankInfo = BankInformation.builder()
                .branchName("Downtown Branch")
                .branchAddress(branchAddress)
                .build();

        assertNotNull(bankInfo.getBranchAddress());
        assertEquals("789 Bank St", bankInfo.getBranchAddress().getAddress1());
    }

    @Test
    public void testBankInformation_SettersAndGetters() {
        BankInformation bankInfo = new BankInformation();
        bankInfo.setBranchName("West Branch");
        bankInfo.setBranchCode(2002);
        bankInfo.setRoutingNumber(987654321);

        assertEquals("West Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(2002), bankInfo.getBranchCode());
        assertEquals(Integer.valueOf(987654321), bankInfo.getRoutingNumber());
    }
}
