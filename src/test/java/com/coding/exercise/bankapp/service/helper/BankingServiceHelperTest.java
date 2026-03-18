package com.coding.exercise.bankapp.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.BankInformation;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.BankInfo;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.Transaction;

class BankingServiceHelperTest {

    private BankingServiceHelper helper;

    @BeforeEach
    void setUp() {
        helper = new BankingServiceHelper();
    }

    // --- Address conversion tests ---

    @Test
    void convertToAddressDomain_shouldMapAllFields() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62704")
                .country("US")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertEquals("123 Main St", result.getAddress1());
        assertEquals("Apt 4", result.getAddress2());
        assertEquals("Springfield", result.getCity());
        assertEquals("IL", result.getState());
        assertEquals("62704", result.getZip());
        assertEquals("US", result.getCountry());
    }

    @Test
    void convertToAddressEntity_shouldMapAllFields() {
        AddressDetails details = AddressDetails.builder()
                .address1("456 Oak Ave")
                .address2("Suite 100")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("US")
                .build();

        Address result = helper.convertToAddressEntity(details);

        assertEquals("456 Oak Ave", result.getAddress1());
        assertEquals("Suite 100", result.getAddress2());
        assertEquals("Chicago", result.getCity());
        assertEquals("IL", result.getState());
        assertEquals("60601", result.getZip());
        assertEquals("US", result.getCountry());
    }

    // --- Contact conversion tests ---

    @Test
    void convertToContactDomain_shouldMapAllFields() {
        Contact contact = Contact.builder()
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertEquals("john@example.com", result.getEmailId());
        assertEquals("555-1234", result.getHomePhone());
        assertEquals("555-5678", result.getWorkPhone());
    }

    @Test
    void convertToContactEntity_shouldMapAllFields() {
        ContactDetails details = ContactDetails.builder()
                .emailId("jane@example.com")
                .homePhone("555-9999")
                .workPhone("555-0000")
                .build();

        Contact result = helper.convertToContactEntity(details);

        assertEquals("jane@example.com", result.getEmailId());
        assertEquals("555-9999", result.getHomePhone());
        assertEquals("555-0000", result.getWorkPhone());
    }

    // --- BankInfo conversion tests ---

    @Test
    void convertToBankInfoDomain_shouldMapAllFields() {
        Address branchAddr = Address.builder()
                .address1("789 Bank St")
                .address2("")
                .city("Dallas")
                .state("TX")
                .zip("75001")
                .country("US")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Downtown Branch")
                .branchCode(101)
                .routingNumber(123456789)
                .branchAddress(branchAddr)
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertEquals("Downtown Branch", result.getBranchName());
        assertEquals(101, result.getBranchCode());
        assertEquals(123456789, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("789 Bank St", result.getBranchAddress().getAddress1());
        assertEquals("Dallas", result.getBranchAddress().getCity());
    }

    @Test
    void convertToBankInfoEntity_shouldMapAllFields() {
        AddressDetails branchAddr = AddressDetails.builder()
                .address1("100 Finance Blvd")
                .address2("Floor 2")
                .city("Austin")
                .state("TX")
                .zip("73301")
                .country("US")
                .build();

        BankInformation bankInfo = BankInformation.builder()
                .branchName("Uptown Branch")
                .branchCode(202)
                .routingNumber(987654321)
                .branchAddress(branchAddr)
                .build();

        BankInfo result = helper.convertToBankInfoEntity(bankInfo);

        assertEquals("Uptown Branch", result.getBranchName());
        assertEquals(202, result.getBranchCode());
        assertEquals(987654321, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("100 Finance Blvd", result.getBranchAddress().getAddress1());
        assertEquals("Austin", result.getBranchAddress().getCity());
    }

    // --- Customer conversion tests ---

    @Test
    void convertToCustomerDomain_shouldMapAllFields() {
        Contact contact = Contact.builder()
                .emailId("customer@test.com")
                .homePhone("111-1111")
                .workPhone("222-2222")
                .build();

        Address address = Address.builder()
                .address1("10 Customer Ln")
                .address2("")
                .city("Boston")
                .state("MA")
                .zip("02101")
                .country("US")
                .build();

        Customer customer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("ACTIVE")
                .contactDetails(contact)
                .customerAddress(address)
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(12345L, result.getCustomerNumber());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertEquals("customer@test.com", result.getContactDetails().getEmailId());
        assertNotNull(result.getCustomerAddress());
        assertEquals("10 Customer Ln", result.getCustomerAddress().getAddress1());
    }

    @Test
    void convertToCustomerEntity_shouldMapAllFields() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("entity@test.com")
                .homePhone("333-3333")
                .workPhone("444-4444")
                .build();

        AddressDetails addressDetails = AddressDetails.builder()
                .address1("20 Entity Ave")
                .address2("Unit 5")
                .city("Seattle")
                .state("WA")
                .zip("98101")
                .country("US")
                .build();

        CustomerDetails details = CustomerDetails.builder()
                .firstName("Jane")
                .middleName("A")
                .lastName("Smith")
                .customerNumber(67890L)
                .status("INACTIVE")
                .contactDetails(contactDetails)
                .customerAddress(addressDetails)
                .build();

        Customer result = helper.convertToCustomerEntity(details);

        assertEquals("Jane", result.getFirstName());
        assertEquals("A", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(67890L, result.getCustomerNumber());
        assertEquals("INACTIVE", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertEquals("entity@test.com", result.getContactDetails().getEmailId());
        assertNotNull(result.getCustomerAddress());
        assertEquals("20 Entity Ave", result.getCustomerAddress().getAddress1());
    }

    // --- Account conversion tests ---

    @Test
    void convertToAccountDomain_shouldMapAllFields() {
        Address branchAddr = Address.builder()
                .address1("Bank Rd")
                .address2("")
                .city("NYC")
                .state("NY")
                .zip("10001")
                .country("US")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(300)
                .routingNumber(111222333)
                .branchAddress(branchAddr)
                .build();

        Account account = Account.builder()
                .accountNumber(100001L)
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .accountStatus("ACTIVE")
                .bankInformation(bankInfo)
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(100001L, result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals(5000.0, result.getAccountBalance());
        assertEquals("ACTIVE", result.getAccountStatus());
        assertNotNull(result.getBankInformation());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
    }

    @Test
    void convertToAccountEntity_shouldMapAllFields() {
        AddressDetails branchAddr = AddressDetails.builder()
                .address1("Branch Rd")
                .address2("")
                .city("LA")
                .state("CA")
                .zip("90001")
                .country("US")
                .build();

        BankInformation bankInfo = BankInformation.builder()
                .branchName("West Branch")
                .branchCode(400)
                .routingNumber(444555666)
                .branchAddress(branchAddr)
                .build();

        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(200002L)
                .accountType("CHECKING")
                .accountBalance(10000.0)
                .accountStatus("ACTIVE")
                .bankInformation(bankInfo)
                .build();

        Account result = helper.convertToAccountEntity(accInfo);

        assertEquals(200002L, result.getAccountNumber());
        assertEquals("CHECKING", result.getAccountType());
        assertEquals(10000.0, result.getAccountBalance());
        assertEquals("ACTIVE", result.getAccountStatus());
        assertNotNull(result.getBankInformation());
        assertEquals("West Branch", result.getBankInformation().getBranchName());
    }

    // --- Transaction conversion tests ---

    @Test
    void convertToTransactionDomain_shouldMapAllFields() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(100001L)
                .txAmount(250.0)
                .txType("DEBIT")
                .txDateTime(txDate)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(100001L, result.getAccountNumber());
        assertEquals(250.0, result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(txDate, result.getTxDateTime());
    }

    @Test
    void convertToTransactionEntity_shouldMapAllFields() {
        Date txDate = new Date();
        TransactionDetails details = TransactionDetails.builder()
                .accountNumber(200002L)
                .txAmount(500.0)
                .txType("CREDIT")
                .txDateTime(txDate)
                .build();

        Transaction result = helper.convertToTransactionEntity(details);

        assertEquals(200002L, result.getAccountNumber());
        assertEquals(500.0, result.getTxAmount());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(txDate, result.getTxDateTime());
    }

    // --- createTransaction test ---

    @Test
    void createTransaction_shouldCreateTransactionWithCorrectFields() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(200002L);
        transferDetails.setTransferAmount(300.0);

        Transaction result = helper.createTransaction(transferDetails, 100001L, "DEBIT");

        assertEquals(100001L, result.getAccountNumber());
        assertEquals(300.0, result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    void createTransaction_shouldSetNonNullDateTime() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setTransferAmount(150.0);

        Transaction result = helper.createTransaction(transferDetails, 999L, "CREDIT");

        assertEquals(999L, result.getAccountNumber());
        assertEquals(150.0, result.getTxAmount());
        assertEquals("CREDIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }
}
