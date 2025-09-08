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
    private Customer testCustomer;
    private CustomerDetails testCustomerDetails;
    private Account testAccount;
    private AccountInformation testAccountInfo;

    @BeforeEach
    void setUp() {
        helper = new BankingServiceHelper();
        
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

        testCustomer = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName("Q")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contact)
            .customerAddress(address)
            .createDateTime(new Date())
            .updateDateTime(new Date())
            .build();

        ContactDetails contactDetails = ContactDetails.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();

        AddressDetails addressDetails = AddressDetails.builder()
            .address1("123 Main St")
            .address2("Apt 4B")
            .city("Anytown")
            .state("NY")
            .zip("12345")
            .country("USA")
            .build();

        testCustomerDetails = CustomerDetails.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName("Q")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contactDetails)
            .customerAddress(addressDetails)
            .build();

        Address branchAddress = Address.builder()
            .address1("100 Bank St")
            .address2("Suite 200")
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

        testAccount = Account.builder()
            .accountNumber(456L)
            .accountType("CHECKING")
            .accountBalance(1000.0)
            .accountStatus("ACTIVE")
            .bankInformation(bankInfo)
            .createDateTime(new Date())
            .updateDateTime(new Date())
            .build();

        AddressDetails branchAddressDetails = AddressDetails.builder()
            .address1("100 Bank St")
            .address2("Suite 200")
            .city("Financial District")
            .state("NY")
            .zip("10001")
            .country("USA")
            .build();

        BankInformation bankInformation = BankInformation.builder()
            .branchCode(1001)
            .branchName("Test Bank")
            .routingNumber(123456789)
            .branchAddress(branchAddressDetails)
            .build();

        testAccountInfo = AccountInformation.builder()
            .accountNumber(456L)
            .accountType("CHECKING")
            .accountBalance(1000.0)
            .accountStatus("ACTIVE")
            .bankInformation(bankInformation)
            .build();
    }

    @Test
    void convertToCustomerDomain_mapsAllFields() {
        CustomerDetails result = helper.convertToCustomerDomain(testCustomer);

        assertEquals(testCustomer.getCustomerNumber(), result.getCustomerNumber());
        assertEquals(testCustomer.getFirstName(), result.getFirstName());
        assertEquals(testCustomer.getMiddleName(), result.getMiddleName());
        assertEquals(testCustomer.getLastName(), result.getLastName());
        assertEquals(testCustomer.getStatus(), result.getStatus());
        
        assertNotNull(result.getContactDetails());
        assertEquals(testCustomer.getContactDetails().getEmailId(), result.getContactDetails().getEmailId());
        assertEquals(testCustomer.getContactDetails().getHomePhone(), result.getContactDetails().getHomePhone());
        assertEquals(testCustomer.getContactDetails().getWorkPhone(), result.getContactDetails().getWorkPhone());

        assertNotNull(result.getCustomerAddress());
        assertEquals(testCustomer.getCustomerAddress().getAddress1(), result.getCustomerAddress().getAddress1());
        assertEquals(testCustomer.getCustomerAddress().getAddress2(), result.getCustomerAddress().getAddress2());
        assertEquals(testCustomer.getCustomerAddress().getCity(), result.getCustomerAddress().getCity());
        assertEquals(testCustomer.getCustomerAddress().getState(), result.getCustomerAddress().getState());
        assertEquals(testCustomer.getCustomerAddress().getZip(), result.getCustomerAddress().getZip());
        assertEquals(testCustomer.getCustomerAddress().getCountry(), result.getCustomerAddress().getCountry());
    }

    @Test
    void convertToCustomerDomain_handlesNullContactAndAddress() {
        Customer customerWithNulls = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName("Q")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(null)
            .customerAddress(null)
            .build();

        assertThrows(NullPointerException.class, () -> {
            helper.convertToCustomerDomain(customerWithNulls);
        });
    }

    @Test
    void convertToCustomerEntity_mapsAllFields() {
        Customer result = helper.convertToCustomerEntity(testCustomerDetails);

        assertEquals(testCustomerDetails.getCustomerNumber(), result.getCustomerNumber());
        assertEquals(testCustomerDetails.getFirstName(), result.getFirstName());
        assertEquals(testCustomerDetails.getMiddleName(), result.getMiddleName());
        assertEquals(testCustomerDetails.getLastName(), result.getLastName());
        assertEquals(testCustomerDetails.getStatus(), result.getStatus());

        assertNotNull(result.getContactDetails());
        assertEquals(testCustomerDetails.getContactDetails().getEmailId(), result.getContactDetails().getEmailId());
        assertEquals(testCustomerDetails.getContactDetails().getHomePhone(), result.getContactDetails().getHomePhone());
        assertEquals(testCustomerDetails.getContactDetails().getWorkPhone(), result.getContactDetails().getWorkPhone());
        
        assertNotNull(result.getCustomerAddress());
        assertEquals(testCustomerDetails.getCustomerAddress().getAddress1(), result.getCustomerAddress().getAddress1());
        assertEquals(testCustomerDetails.getCustomerAddress().getAddress2(), result.getCustomerAddress().getAddress2());
        assertEquals(testCustomerDetails.getCustomerAddress().getCity(), result.getCustomerAddress().getCity());
        assertEquals(testCustomerDetails.getCustomerAddress().getState(), result.getCustomerAddress().getState());
        assertEquals(testCustomerDetails.getCustomerAddress().getZip(), result.getCustomerAddress().getZip());
        assertEquals(testCustomerDetails.getCustomerAddress().getCountry(), result.getCustomerAddress().getCountry());
    }

    @Test
    void convertToAccountDomain_mapsAllFields() {
        AccountInformation result = helper.convertToAccountDomain(testAccount);

        assertEquals(testAccount.getAccountNumber(), result.getAccountNumber());
        assertEquals(testAccount.getAccountType(), result.getAccountType());
        assertEquals(testAccount.getAccountBalance(), result.getAccountBalance());
        assertEquals(testAccount.getAccountStatus(), result.getAccountStatus());
        
        assertNotNull(result.getBankInformation());
        assertEquals(testAccount.getBankInformation().getBranchCode(), result.getBankInformation().getBranchCode());
        assertEquals(testAccount.getBankInformation().getBranchName(), result.getBankInformation().getBranchName());
        assertEquals(testAccount.getBankInformation().getRoutingNumber(), result.getBankInformation().getRoutingNumber());
        
        assertNotNull(result.getBankInformation().getBranchAddress());
        assertEquals(testAccount.getBankInformation().getBranchAddress().getAddress1(), 
                     result.getBankInformation().getBranchAddress().getAddress1());
        assertEquals(testAccount.getBankInformation().getBranchAddress().getCity(), 
                     result.getBankInformation().getBranchAddress().getCity());
    }

    @Test
    void convertToAccountEntity_mapsAllFields() {
        Account result = helper.convertToAccountEntity(testAccountInfo);

        assertEquals(testAccountInfo.getAccountNumber(), result.getAccountNumber());
        assertEquals(testAccountInfo.getAccountType(), result.getAccountType());
        assertEquals(testAccountInfo.getAccountBalance(), result.getAccountBalance());
        assertEquals(testAccountInfo.getAccountStatus(), result.getAccountStatus());
        
        assertNotNull(result.getBankInformation());
        assertEquals(testAccountInfo.getBankInformation().getBranchCode(), result.getBankInformation().getBranchCode());
        assertEquals(testAccountInfo.getBankInformation().getBranchName(), result.getBankInformation().getBranchName());
        assertEquals(testAccountInfo.getBankInformation().getRoutingNumber(), result.getBankInformation().getRoutingNumber());
        
        assertNotNull(result.getBankInformation().getBranchAddress());
        assertEquals(testAccountInfo.getBankInformation().getBranchAddress().getAddress1(), 
                     result.getBankInformation().getBranchAddress().getAddress1());
        assertEquals(testAccountInfo.getBankInformation().getBranchAddress().getCity(), 
                     result.getBankInformation().getBranchAddress().getCity());
    }

    @Test
    void convertToTransactionDomain_mapsAllFields() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
            .accountNumber(456L)
            .txAmount(100.0)
            .txType("DEBIT")
            .txDateTime(txDate)
            .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(transaction.getAccountNumber(), result.getAccountNumber());
        assertEquals(transaction.getTxAmount(), result.getTxAmount());
        assertEquals(transaction.getTxType(), result.getTxType());
        assertEquals(transaction.getTxDateTime(), result.getTxDateTime());
    }

    @Test
    void createTransaction_debit_createsCorrectTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setTransferAmount(100.0);

        Transaction result = helper.createTransaction(transferDetails, 456L, "DEBIT");

        assertEquals(Long.valueOf(456L), result.getAccountNumber());
        assertEquals(Double.valueOf(100.0), result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    void createTransaction_credit_createsCorrectTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setTransferAmount(200.0);

        Transaction result = helper.createTransaction(transferDetails, 789L, "CREDIT");

        assertEquals(Long.valueOf(789L), result.getAccountNumber());
        assertEquals(Double.valueOf(200.0), result.getTxAmount());
        assertEquals("CREDIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }
}
