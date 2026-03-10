package com.coding.exercise.bankapp.service.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

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

/**
 * Pure unit tests for BankingServiceHelper.
 * No Spring context needed.
 *
 * Note: All conversion methods will throw NullPointerException if nested
 * objects (contact, address, bankInfo, branchAddress) are null since the
 * helper does not perform null checks.
 */
public class BankingServiceHelperTest {

    private BankingServiceHelper helper;

    @Before
    public void setUp() {
        helper = new BankingServiceHelper();
    }

    // ---- convertToCustomerDomain ----

    @Test
    public void testConvertToCustomerDomain() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62704")
                .country("US")
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
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(Long.valueOf(1001L), result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
        assertEquals("Apt 4", result.getCustomerAddress().getAddress2());
        assertEquals("Springfield", result.getCustomerAddress().getCity());
        assertEquals("IL", result.getCustomerAddress().getState());
        assertEquals("62704", result.getCustomerAddress().getZip());
        assertEquals("US", result.getCustomerAddress().getCountry());
        assertEquals("john@example.com", result.getContactDetails().getEmailId());
        assertEquals("555-1234", result.getContactDetails().getHomePhone());
        assertEquals("555-5678", result.getContactDetails().getWorkPhone());
    }

    // ---- convertToCustomerEntity ----

    @Test
    public void testConvertToCustomerEntity() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("456 Oak Ave")
                .address2("Suite 10")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("US")
                .build();

        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("jane@example.com")
                .homePhone("555-9876")
                .workPhone("555-4321")
                .build();

        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("Jane")
                .middleName("A")
                .lastName("Smith")
                .customerNumber(2002L)
                .status("Inactive")
                .customerAddress(addressDetails)
                .contactDetails(contactDetails)
                .build();

        Customer result = helper.convertToCustomerEntity(customerDetails);

        assertEquals("Jane", result.getFirstName());
        assertEquals("A", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(Long.valueOf(2002L), result.getCustomerNumber());
        assertEquals("Inactive", result.getStatus());
        assertEquals("456 Oak Ave", result.getCustomerAddress().getAddress1());
        assertEquals("Suite 10", result.getCustomerAddress().getAddress2());
        assertEquals("Chicago", result.getCustomerAddress().getCity());
        assertEquals("IL", result.getCustomerAddress().getState());
        assertEquals("60601", result.getCustomerAddress().getZip());
        assertEquals("US", result.getCustomerAddress().getCountry());
        assertEquals("jane@example.com", result.getContactDetails().getEmailId());
        assertEquals("555-9876", result.getContactDetails().getHomePhone());
        assertEquals("555-4321", result.getContactDetails().getWorkPhone());
    }

    // ---- convertToAccountDomain ----

    @Test
    public void testConvertToAccountDomain() {
        Address branchAddress = Address.builder()
                .address1("789 Bank Blvd")
                .address2("Floor 2")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("US")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchCode(100)
                .branchName("Main Branch")
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        Account account = Account.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountBalance(1500.00)
                .accountStatus("Active")
                .bankInformation(bankInfo)
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(Long.valueOf(5001L), result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals(Double.valueOf(1500.00), result.getAccountBalance());
        assertEquals("Active", result.getAccountStatus());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
        assertEquals(Integer.valueOf(100), result.getBankInformation().getBranchCode());
        assertEquals(Integer.valueOf(123456789), result.getBankInformation().getRoutingNumber());
        assertEquals("789 Bank Blvd", result.getBankInformation().getBranchAddress().getAddress1());
        assertEquals("Floor 2", result.getBankInformation().getBranchAddress().getAddress2());
        assertEquals("New York", result.getBankInformation().getBranchAddress().getCity());
        assertEquals("NY", result.getBankInformation().getBranchAddress().getState());
        assertEquals("10001", result.getBankInformation().getBranchAddress().getZip());
        assertEquals("US", result.getBankInformation().getBranchAddress().getCountry());
    }

    // ---- convertToAccountEntity ----

    @Test
    public void testConvertToAccountEntity() {
        AddressDetails branchAddr = AddressDetails.builder()
                .address1("100 Finance St")
                .address2("")
                .city("Boston")
                .state("MA")
                .zip("02101")
                .country("US")
                .build();

        BankInformation bankInfo = BankInformation.builder()
                .branchCode(200)
                .branchName("Downtown Branch")
                .routingNumber(987654321)
                .branchAddress(branchAddr)
                .build();

        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(6001L)
                .accountType("CHECKING")
                .accountBalance(2500.00)
                .accountStatus("Active")
                .bankInformation(bankInfo)
                .build();

        Account result = helper.convertToAccountEntity(accInfo);

        assertEquals(Long.valueOf(6001L), result.getAccountNumber());
        assertEquals("CHECKING", result.getAccountType());
        assertEquals(Double.valueOf(2500.00), result.getAccountBalance());
        assertEquals("Active", result.getAccountStatus());
        assertEquals("Downtown Branch", result.getBankInformation().getBranchName());
        assertEquals(Integer.valueOf(200), result.getBankInformation().getBranchCode());
        assertEquals(Integer.valueOf(987654321), result.getBankInformation().getRoutingNumber());
        assertEquals("100 Finance St", result.getBankInformation().getBranchAddress().getAddress1());
    }

    // ---- convertToAddressDomain ----

    @Test
    public void testConvertToAddressDomain() {
        Address address = Address.builder()
                .address1("10 Elm St")
                .address2("Unit 5")
                .city("Dallas")
                .state("TX")
                .zip("75201")
                .country("US")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertEquals("10 Elm St", result.getAddress1());
        assertEquals("Unit 5", result.getAddress2());
        assertEquals("Dallas", result.getCity());
        assertEquals("TX", result.getState());
        assertEquals("75201", result.getZip());
        assertEquals("US", result.getCountry());
    }

    // ---- convertToAddressEntity ----

    @Test
    public void testConvertToAddressEntity() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("20 Pine Rd")
                .address2("Bldg A")
                .city("Seattle")
                .state("WA")
                .zip("98101")
                .country("US")
                .build();

        Address result = helper.convertToAddressEntity(addressDetails);

        assertEquals("20 Pine Rd", result.getAddress1());
        assertEquals("Bldg A", result.getAddress2());
        assertEquals("Seattle", result.getCity());
        assertEquals("WA", result.getState());
        assertEquals("98101", result.getZip());
        assertEquals("US", result.getCountry());
    }

    // ---- convertToContactDomain ----

    @Test
    public void testConvertToContactDomain() {
        Contact contact = Contact.builder()
                .emailId("test@mail.com")
                .homePhone("111-2222")
                .workPhone("333-4444")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertEquals("test@mail.com", result.getEmailId());
        assertEquals("111-2222", result.getHomePhone());
        assertEquals("333-4444", result.getWorkPhone());
    }

    // ---- convertToContactEntity ----

    @Test
    public void testConvertToContactEntity() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("user@domain.com")
                .homePhone("555-0001")
                .workPhone("555-0002")
                .build();

        Contact result = helper.convertToContactEntity(contactDetails);

        assertEquals("user@domain.com", result.getEmailId());
        assertEquals("555-0001", result.getHomePhone());
        assertEquals("555-0002", result.getWorkPhone());
    }

    // ---- convertToBankInfoDomain ----

    @Test
    public void testConvertToBankInfoDomain() {
        Address branchAddress = Address.builder()
                .address1("500 Wall St")
                .address2("")
                .city("New York")
                .state("NY")
                .zip("10005")
                .country("US")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchCode(300)
                .branchName("Wall Street Branch")
                .routingNumber(111222333)
                .branchAddress(branchAddress)
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertEquals(Integer.valueOf(300), result.getBranchCode());
        assertEquals("Wall Street Branch", result.getBranchName());
        assertEquals(Integer.valueOf(111222333), result.getRoutingNumber());
        assertEquals("500 Wall St", result.getBranchAddress().getAddress1());
        assertEquals("New York", result.getBranchAddress().getCity());
        assertEquals("NY", result.getBranchAddress().getState());
    }

    // ---- convertToBankInfoEntity ----

    @Test
    public void testConvertToBankInfoEntity() {
        AddressDetails branchAddr = AddressDetails.builder()
                .address1("600 Market St")
                .address2("Suite 100")
                .city("San Francisco")
                .state("CA")
                .zip("94105")
                .country("US")
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchCode(400)
                .branchName("Market Branch")
                .routingNumber(444555666)
                .branchAddress(branchAddr)
                .build();

        BankInfo result = helper.convertToBankInfoEntity(bankInformation);

        assertEquals(Integer.valueOf(400), result.getBranchCode());
        assertEquals("Market Branch", result.getBranchName());
        assertEquals(Integer.valueOf(444555666), result.getRoutingNumber());
        assertEquals("600 Market St", result.getBranchAddress().getAddress1());
        assertEquals("Suite 100", result.getBranchAddress().getAddress2());
        assertEquals("San Francisco", result.getBranchAddress().getCity());
    }

    // ---- convertToTransactionDomain ----

    @Test
    public void testConvertToTransactionDomain() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .txAmount(250.00)
                .txDateTime(txDate)
                .txType("DEBIT")
                .accountNumber(7001L)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(Double.valueOf(250.00), result.getTxAmount());
        assertEquals(txDate, result.getTxDateTime());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(Long.valueOf(7001L), result.getAccountNumber());
    }

    // ---- convertToTransactionEntity ----

    @Test
    public void testConvertToTransactionEntity() {
        Date txDate = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .txAmount(500.00)
                .txDateTime(txDate)
                .txType("CREDIT")
                .accountNumber(8001L)
                .build();

        Transaction result = helper.convertToTransactionEntity(transactionDetails);

        assertEquals(Double.valueOf(500.00), result.getTxAmount());
        assertEquals(txDate, result.getTxDateTime());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(Long.valueOf(8001L), result.getAccountNumber());
    }

    // ---- createTransaction ----

    @Test
    public void testCreateTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000L);
        transferDetails.setToAccountNumber(2000L);
        transferDetails.setTransferAmount(750.00);

        Transaction result = helper.createTransaction(transferDetails, 1000L, "DEBIT");

        assertEquals(Long.valueOf(1000L), result.getAccountNumber());
        assertEquals(Double.valueOf(750.00), result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }
}
