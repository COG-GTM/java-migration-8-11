package com.coding.exercise.bankapp.service.helper;

import static org.junit.jupiter.api.Assertions.*;

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

    private Address createAddress() {
        return Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();
    }

    private AddressDetails createAddressDetails() {
        return AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();
    }

    @Test
    void convertToAddressDomain_shouldMapAllFields() {
        Address address = createAddress();
        AddressDetails result = helper.convertToAddressDomain(address);

        assertEquals("123 Main St", result.getAddress1());
        assertEquals("Apt 4", result.getAddress2());
        assertEquals("Springfield", result.getCity());
        assertEquals("IL", result.getState());
        assertEquals("62701", result.getZip());
        assertEquals("US", result.getCountry());
    }

    @Test
    void convertToAddressEntity_shouldMapAllFields() {
        AddressDetails details = createAddressDetails();
        Address result = helper.convertToAddressEntity(details);

        assertEquals("123 Main St", result.getAddress1());
        assertEquals("Apt 4", result.getAddress2());
        assertEquals("Springfield", result.getCity());
        assertEquals("IL", result.getState());
        assertEquals("62701", result.getZip());
        assertEquals("US", result.getCountry());
    }

    // --- Contact conversion tests ---

    private Contact createContact() {
        return Contact.builder()
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();
    }

    private ContactDetails createContactDetails() {
        return ContactDetails.builder()
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();
    }

    @Test
    void convertToContactDomain_shouldMapAllFields() {
        Contact contact = createContact();
        ContactDetails result = helper.convertToContactDomain(contact);

        assertEquals("john@example.com", result.getEmailId());
        assertEquals("555-1234", result.getHomePhone());
        assertEquals("555-5678", result.getWorkPhone());
    }

    @Test
    void convertToContactEntity_shouldMapAllFields() {
        ContactDetails details = createContactDetails();
        Contact result = helper.convertToContactEntity(details);

        assertEquals("john@example.com", result.getEmailId());
        assertEquals("555-1234", result.getHomePhone());
        assertEquals("555-5678", result.getWorkPhone());
    }

    // --- BankInfo conversion tests ---

    private BankInfo createBankInfo() {
        return BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456)
                .branchAddress(createAddress())
                .build();
    }

    private BankInformation createBankInformation() {
        return BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456)
                .branchAddress(createAddressDetails())
                .build();
    }

    @Test
    void convertToBankInfoDomain_shouldMapAllFields() {
        BankInfo bankInfo = createBankInfo();
        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertEquals("Main Branch", result.getBranchName());
        assertEquals(1001, result.getBranchCode());
        assertEquals(123456, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("Springfield", result.getBranchAddress().getCity());
    }

    @Test
    void convertToBankInfoEntity_shouldMapAllFields() {
        BankInformation info = createBankInformation();
        BankInfo result = helper.convertToBankInfoEntity(info);

        assertEquals("Main Branch", result.getBranchName());
        assertEquals(1001, result.getBranchCode());
        assertEquals(123456, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("Springfield", result.getBranchAddress().getCity());
    }

    // --- Customer conversion tests ---

    @Test
    void convertToCustomerDomain_shouldMapAllFields() {
        Customer customer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .contactDetails(createContact())
                .customerAddress(createAddress())
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(12345L, result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertNotNull(result.getCustomerAddress());
    }

    @Test
    void convertToCustomerEntity_shouldMapAllFields() {
        CustomerDetails details = CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .contactDetails(createContactDetails())
                .customerAddress(createAddressDetails())
                .build();

        Customer result = helper.convertToCustomerEntity(details);

        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(12345L, result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertNotNull(result.getCustomerAddress());
    }

    // --- Account conversion tests ---

    @Test
    void convertToAccountDomain_shouldMapAllFields() {
        Account account = Account.builder()
                .accountNumber(99001L)
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .accountStatus("Active")
                .bankInformation(createBankInfo())
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(99001L, result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals(5000.0, result.getAccountBalance());
        assertEquals("Active", result.getAccountStatus());
        assertNotNull(result.getBankInformation());
    }

    @Test
    void convertToAccountEntity_shouldMapAllFields() {
        AccountInformation info = AccountInformation.builder()
                .accountNumber(99001L)
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .accountStatus("Active")
                .bankInformation(createBankInformation())
                .build();

        Account result = helper.convertToAccountEntity(info);

        assertEquals(99001L, result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals(5000.0, result.getAccountBalance());
        assertEquals("Active", result.getAccountStatus());
        assertNotNull(result.getBankInformation());
    }

    // --- Transaction conversion tests ---

    @Test
    void convertToTransactionDomain_shouldMapAllFields() {
        Date now = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(99001L)
                .txAmount(250.0)
                .txType("DEBIT")
                .txDateTime(now)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(99001L, result.getAccountNumber());
        assertEquals(250.0, result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(now, result.getTxDateTime());
    }

    @Test
    void convertToTransactionEntity_shouldMapAllFields() {
        Date now = new Date();
        TransactionDetails details = TransactionDetails.builder()
                .accountNumber(99001L)
                .txAmount(250.0)
                .txType("CREDIT")
                .txDateTime(now)
                .build();

        Transaction result = helper.convertToTransactionEntity(details);

        assertEquals(99001L, result.getAccountNumber());
        assertEquals(250.0, result.getTxAmount());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(now, result.getTxDateTime());
    }

    // --- createTransaction test ---

    @Test
    void createTransaction_shouldCreateDebitTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(500.0);

        Transaction result = helper.createTransaction(transferDetails, 1001L, "DEBIT");

        assertEquals(1001L, result.getAccountNumber());
        assertEquals(500.0, result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    void createTransaction_shouldCreateCreditTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(300.0);

        Transaction result = helper.createTransaction(transferDetails, 1002L, "CREDIT");

        assertEquals(1002L, result.getAccountNumber());
        assertEquals(300.0, result.getTxAmount());
        assertEquals("CREDIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }
}
