package com.coding.exercise.bankapp.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DomainDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ===================== CustomerDetails =====================

    @Test
    public void customerDetails_builderAndGetters() {
        CustomerDetails cd = CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .build();

        assertEquals("John", cd.getFirstName());
        assertEquals("M", cd.getMiddleName());
        assertEquals("Doe", cd.getLastName());
        assertEquals(Long.valueOf(1001L), cd.getCustomerNumber());
        assertEquals("Active", cd.getStatus());
    }

    @Test
    public void customerDetails_noArgsConstructorAndSetters() {
        CustomerDetails cd = new CustomerDetails();
        cd.setFirstName("Jane");
        cd.setLastName("Smith");
        cd.setCustomerNumber(1002L);

        assertEquals("Jane", cd.getFirstName());
        assertEquals("Smith", cd.getLastName());
        assertEquals(Long.valueOf(1002L), cd.getCustomerNumber());
    }

    @Test
    public void customerDetails_allArgsConstructor() {
        AddressDetails addr = AddressDetails.builder().address1("123 St").build();
        ContactDetails contact = ContactDetails.builder().emailId("a@b.com").build();
        CustomerDetails cd = new CustomerDetails("John", "Doe", "M", 1001L, "Active", addr, contact);

        assertEquals("John", cd.getFirstName());
        assertEquals(Long.valueOf(1001L), cd.getCustomerNumber());
        assertNotNull(cd.getCustomerAddress());
        assertNotNull(cd.getContactDetails());
    }

    @Test
    public void customerDetails_withNestedObjects() {
        ContactDetails contact = ContactDetails.builder()
                .emailId("john@example.com")
                .homePhone("111-222-3333")
                .workPhone("444-555-6666")
                .build();
        AddressDetails address = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();
        CustomerDetails cd = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .contactDetails(contact)
                .customerAddress(address)
                .build();

        assertEquals("john@example.com", cd.getContactDetails().getEmailId());
        assertEquals("123 Main St", cd.getCustomerAddress().getAddress1());
    }

    @Test
    public void customerDetails_serialization() throws Exception {
        CustomerDetails cd = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .build();

        String json = objectMapper.writeValueAsString(cd);
        assertNotNull(json);

        CustomerDetails deserialized = objectMapper.readValue(json, CustomerDetails.class);
        assertEquals("John", deserialized.getFirstName());
        assertEquals("Doe", deserialized.getLastName());
        assertEquals(Long.valueOf(1001L), deserialized.getCustomerNumber());
    }

    // ===================== AccountInformation =====================

    @Test
    public void accountInformation_builderAndGetters() {
        Date now = new Date();
        AccountInformation ai = AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .accountCreated(now)
                .build();

        assertEquals(Long.valueOf(5001L), ai.getAccountNumber());
        assertEquals("SAVINGS", ai.getAccountType());
        assertEquals("Active", ai.getAccountStatus());
        assertEquals(1000.0, ai.getAccountBalance(), 0.001);
        assertEquals(now, ai.getAccountCreated());
    }

    @Test
    public void accountInformation_noArgsConstructorAndSetters() {
        AccountInformation ai = new AccountInformation();
        ai.setAccountNumber(5002L);
        ai.setAccountType("CHECKING");
        ai.setAccountBalance(2000.0);

        assertEquals(Long.valueOf(5002L), ai.getAccountNumber());
        assertEquals("CHECKING", ai.getAccountType());
        assertEquals(2000.0, ai.getAccountBalance(), 0.001);
    }

    @Test
    public void accountInformation_withBankInformation() {
        BankInformation bi = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(101)
                .routingNumber(12345)
                .build();
        AccountInformation ai = AccountInformation.builder()
                .accountNumber(5001L)
                .bankInformation(bi)
                .build();

        assertNotNull(ai.getBankInformation());
        assertEquals("Main Branch", ai.getBankInformation().getBranchName());
    }

    @Test
    public void accountInformation_serialization() throws Exception {
        AccountInformation ai = AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .build();

        String json = objectMapper.writeValueAsString(ai);
        assertNotNull(json);

        AccountInformation deserialized = objectMapper.readValue(json, AccountInformation.class);
        assertEquals(Long.valueOf(5001L), deserialized.getAccountNumber());
        assertEquals("SAVINGS", deserialized.getAccountType());
    }

    // ===================== TransactionDetails =====================

    @Test
    public void transactionDetails_builderAndGetters() {
        Date now = new Date();
        TransactionDetails td = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("DEBIT")
                .txAmount(100.0)
                .txDateTime(now)
                .build();

        assertEquals(Long.valueOf(5001L), td.getAccountNumber());
        assertEquals("DEBIT", td.getTxType());
        assertEquals(100.0, td.getTxAmount(), 0.001);
        assertEquals(now, td.getTxDateTime());
    }

    @Test
    public void transactionDetails_noArgsConstructorAndSetters() {
        TransactionDetails td = new TransactionDetails();
        td.setAccountNumber(5002L);
        td.setTxType("CREDIT");
        td.setTxAmount(200.0);

        assertEquals(Long.valueOf(5002L), td.getAccountNumber());
        assertEquals("CREDIT", td.getTxType());
        assertEquals(200.0, td.getTxAmount(), 0.001);
    }

    @Test
    public void transactionDetails_serialization() throws Exception {
        TransactionDetails td = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("DEBIT")
                .txAmount(100.0)
                .build();

        String json = objectMapper.writeValueAsString(td);
        assertNotNull(json);

        TransactionDetails deserialized = objectMapper.readValue(json, TransactionDetails.class);
        assertEquals(Long.valueOf(5001L), deserialized.getAccountNumber());
        assertEquals("DEBIT", deserialized.getTxType());
    }

    // ===================== TransferDetails =====================

    @Test
    public void transferDetails_gettersAndSetters() {
        TransferDetails td = new TransferDetails();
        td.setFromAccountNumber(5001L);
        td.setToAccountNumber(5002L);
        td.setTransferAmount(500.0);

        assertEquals(Long.valueOf(5001L), td.getFromAccountNumber());
        assertEquals(Long.valueOf(5002L), td.getToAccountNumber());
        assertEquals(500.0, td.getTransferAmount(), 0.001);
    }

    @Test
    public void transferDetails_allArgsConstructor() {
        TransferDetails td = new TransferDetails(5001L, 5002L, 250.0);

        assertEquals(Long.valueOf(5001L), td.getFromAccountNumber());
        assertEquals(Long.valueOf(5002L), td.getToAccountNumber());
        assertEquals(250.0, td.getTransferAmount(), 0.001);
    }

    @Test
    public void transferDetails_serialization() throws Exception {
        TransferDetails td = new TransferDetails(5001L, 5002L, 100.0);

        String json = objectMapper.writeValueAsString(td);
        assertNotNull(json);

        TransferDetails deserialized = objectMapper.readValue(json, TransferDetails.class);
        assertEquals(Long.valueOf(5001L), deserialized.getFromAccountNumber());
        assertEquals(Long.valueOf(5002L), deserialized.getToAccountNumber());
        assertEquals(100.0, deserialized.getTransferAmount(), 0.001);
    }

    // ===================== BankInformation =====================

    @Test
    public void bankInformation_builderAndGetters() {
        BankInformation bi = BankInformation.builder()
                .branchName("Downtown")
                .branchCode(303)
                .routingNumber(11111)
                .build();

        assertEquals("Downtown", bi.getBranchName());
        assertEquals(Integer.valueOf(303), bi.getBranchCode());
        assertEquals(Integer.valueOf(11111), bi.getRoutingNumber());
    }

    @Test
    public void bankInformation_withAddress() {
        AddressDetails addr = AddressDetails.builder()
                .address1("50 Finance Blvd")
                .city("Metropolis")
                .build();
        BankInformation bi = BankInformation.builder()
                .branchName("Central")
                .branchAddress(addr)
                .build();

        assertNotNull(bi.getBranchAddress());
        assertEquals("50 Finance Blvd", bi.getBranchAddress().getAddress1());
    }

    @Test
    public void bankInformation_serialization() throws Exception {
        BankInformation bi = BankInformation.builder()
                .branchName("Main")
                .branchCode(101)
                .routingNumber(12345)
                .build();

        String json = objectMapper.writeValueAsString(bi);
        assertNotNull(json);

        BankInformation deserialized = objectMapper.readValue(json, BankInformation.class);
        assertEquals("Main", deserialized.getBranchName());
    }

    // ===================== ContactDetails =====================

    @Test
    public void contactDetails_builderAndGetters() {
        ContactDetails cd = ContactDetails.builder()
                .emailId("test@test.com")
                .homePhone("123-456-7890")
                .workPhone("098-765-4321")
                .build();

        assertEquals("test@test.com", cd.getEmailId());
        assertEquals("123-456-7890", cd.getHomePhone());
        assertEquals("098-765-4321", cd.getWorkPhone());
    }

    @Test
    public void contactDetails_noArgsConstructorAndSetters() {
        ContactDetails cd = new ContactDetails();
        cd.setEmailId("other@test.com");
        cd.setHomePhone("111-111-1111");

        assertEquals("other@test.com", cd.getEmailId());
        assertEquals("111-111-1111", cd.getHomePhone());
        assertNull(cd.getWorkPhone());
    }

    @Test
    public void contactDetails_serialization() throws Exception {
        ContactDetails cd = ContactDetails.builder()
                .emailId("a@b.com")
                .homePhone("555-555-5555")
                .workPhone("666-666-6666")
                .build();

        String json = objectMapper.writeValueAsString(cd);
        assertNotNull(json);

        ContactDetails deserialized = objectMapper.readValue(json, ContactDetails.class);
        assertEquals("a@b.com", deserialized.getEmailId());
    }

    // ===================== AddressDetails =====================

    @Test
    public void addressDetails_builderAndGetters() {
        AddressDetails ad = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        assertEquals("123 Main St", ad.getAddress1());
        assertEquals("Apt 4", ad.getAddress2());
        assertEquals("Springfield", ad.getCity());
        assertEquals("IL", ad.getState());
        assertEquals("62701", ad.getZip());
        assertEquals("US", ad.getCountry());
    }

    @Test
    public void addressDetails_noArgsConstructorAndSetters() {
        AddressDetails ad = new AddressDetails();
        ad.setAddress1("456 Oak Ave");
        ad.setCity("Shelbyville");

        assertEquals("456 Oak Ave", ad.getAddress1());
        assertEquals("Shelbyville", ad.getCity());
        assertNull(ad.getState());
    }

    @Test
    public void addressDetails_serialization() throws Exception {
        AddressDetails ad = AddressDetails.builder()
                .address1("789 Pine Rd")
                .city("Capital City")
                .state("CA")
                .zip("90210")
                .country("US")
                .build();

        String json = objectMapper.writeValueAsString(ad);
        assertNotNull(json);

        AddressDetails deserialized = objectMapper.readValue(json, AddressDetails.class);
        assertEquals("789 Pine Rd", deserialized.getAddress1());
        assertEquals("Capital City", deserialized.getCity());
    }
}
