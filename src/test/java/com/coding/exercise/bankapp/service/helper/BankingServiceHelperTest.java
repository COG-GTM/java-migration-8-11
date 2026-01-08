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

public class BankingServiceHelperTest {

    private BankingServiceHelper bankingServiceHelper;

    @Before
    public void setUp() {
        bankingServiceHelper = new BankingServiceHelper();
    }

    @Test
    public void testConvertToCustomerDomain() {
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

        Customer customer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .build();

        CustomerDetails result = bankingServiceHelper.convertToCustomerDomain(customer);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(Long.valueOf(12345L), result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertNotNull(result.getCustomerAddress());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
        assertNotNull(result.getContactDetails());
        assertEquals("test@example.com", result.getContactDetails().getEmailId());
    }

    @Test
    public void testConvertToCustomerEntity() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("456 Oak Ave")
                .address2("Suite 100")
                .city("Los Angeles")
                .state("CA")
                .zip("90001")
                .country("USA")
                .build();

        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("jane@example.com")
                .homePhone("555-9999")
                .workPhone("555-8888")
                .build();

        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("Jane")
                .middleName("A")
                .lastName("Smith")
                .customerNumber(67890L)
                .status("Inactive")
                .customerAddress(addressDetails)
                .contactDetails(contactDetails)
                .build();

        Customer result = bankingServiceHelper.convertToCustomerEntity(customerDetails);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("A", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(Long.valueOf(67890L), result.getCustomerNumber());
        assertEquals("Inactive", result.getStatus());
        assertNotNull(result.getCustomerAddress());
        assertEquals("456 Oak Ave", result.getCustomerAddress().getAddress1());
        assertNotNull(result.getContactDetails());
        assertEquals("jane@example.com", result.getContactDetails().getEmailId());
    }

    @Test
    public void testConvertToAccountDomain() {
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

        Account account = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInfo)
                .build();

        AccountInformation result = bankingServiceHelper.convertToAccountDomain(account);

        assertNotNull(result);
        assertEquals(Long.valueOf(1000001L), result.getAccountNumber());
        assertEquals("Savings", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(Double.valueOf(5000.00), result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
    }

    @Test
    public void testConvertToAccountEntity() {
        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("100 Finance Ave")
                .city("Boston")
                .state("MA")
                .zip("02101")
                .country("USA")
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("Downtown Branch")
                .branchCode(2002)
                .routingNumber(987654321)
                .branchAddress(branchAddressDetails)
                .build();

        AccountInformation accountInformation = AccountInformation.builder()
                .accountNumber(2000002L)
                .accountType("Checking")
                .accountStatus("Active")
                .accountBalance(10000.00)
                .bankInformation(bankInformation)
                .build();

        Account result = bankingServiceHelper.convertToAccountEntity(accountInformation);

        assertNotNull(result);
        assertEquals(Long.valueOf(2000002L), result.getAccountNumber());
        assertEquals("Checking", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(Double.valueOf(10000.00), result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("Downtown Branch", result.getBankInformation().getBranchName());
    }

    @Test
    public void testConvertToAddressDomain() {
        Address address = Address.builder()
                .address1("111 Test St")
                .address2("Floor 2")
                .city("Seattle")
                .state("WA")
                .zip("98101")
                .country("USA")
                .build();

        AddressDetails result = bankingServiceHelper.convertToAddressDomain(address);

        assertNotNull(result);
        assertEquals("111 Test St", result.getAddress1());
        assertEquals("Floor 2", result.getAddress2());
        assertEquals("Seattle", result.getCity());
        assertEquals("WA", result.getState());
        assertEquals("98101", result.getZip());
        assertEquals("USA", result.getCountry());
    }

    @Test
    public void testConvertToAddressEntity() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("222 Sample Ave")
                .address2("Unit 5")
                .city("Denver")
                .state("CO")
                .zip("80201")
                .country("USA")
                .build();

        Address result = bankingServiceHelper.convertToAddressEntity(addressDetails);

        assertNotNull(result);
        assertEquals("222 Sample Ave", result.getAddress1());
        assertEquals("Unit 5", result.getAddress2());
        assertEquals("Denver", result.getCity());
        assertEquals("CO", result.getState());
        assertEquals("80201", result.getZip());
        assertEquals("USA", result.getCountry());
    }

    @Test
    public void testConvertToContactDomain() {
        Contact contact = Contact.builder()
                .emailId("contact@test.com")
                .homePhone("111-222-3333")
                .workPhone("444-555-6666")
                .build();

        ContactDetails result = bankingServiceHelper.convertToContactDomain(contact);

        assertNotNull(result);
        assertEquals("contact@test.com", result.getEmailId());
        assertEquals("111-222-3333", result.getHomePhone());
        assertEquals("444-555-6666", result.getWorkPhone());
    }

    @Test
    public void testConvertToContactEntity() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("entity@test.com")
                .homePhone("777-888-9999")
                .workPhone("000-111-2222")
                .build();

        Contact result = bankingServiceHelper.convertToContactEntity(contactDetails);

        assertNotNull(result);
        assertEquals("entity@test.com", result.getEmailId());
        assertEquals("777-888-9999", result.getHomePhone());
        assertEquals("000-111-2222", result.getWorkPhone());
    }

    @Test
    public void testConvertToBankInfoDomain() {
        Address branchAddress = Address.builder()
                .address1("333 Bank Blvd")
                .city("Miami")
                .state("FL")
                .zip("33101")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("South Branch")
                .branchCode(3003)
                .routingNumber(111222333)
                .branchAddress(branchAddress)
                .build();

        BankInformation result = bankingServiceHelper.convertToBankInfoDomain(bankInfo);

        assertNotNull(result);
        assertEquals("South Branch", result.getBranchName());
        assertEquals(Integer.valueOf(3003), result.getBranchCode());
        assertEquals(Integer.valueOf(111222333), result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("333 Bank Blvd", result.getBranchAddress().getAddress1());
    }

    @Test
    public void testConvertToBankInfoEntity() {
        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("444 Finance St")
                .city("Phoenix")
                .state("AZ")
                .zip("85001")
                .country("USA")
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("West Branch")
                .branchCode(4004)
                .routingNumber(444555666)
                .branchAddress(branchAddressDetails)
                .build();

        BankInfo result = bankingServiceHelper.convertToBankInfoEntity(bankInformation);

        assertNotNull(result);
        assertEquals("West Branch", result.getBranchName());
        assertEquals(Integer.valueOf(4004), result.getBranchCode());
        assertEquals(Integer.valueOf(444555666), result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("444 Finance St", result.getBranchAddress().getAddress1());
    }

    @Test
    public void testConvertToTransactionDomain() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(5000005L)
                .txDateTime(txDate)
                .txType("DEBIT")
                .txAmount(250.00)
                .build();

        TransactionDetails result = bankingServiceHelper.convertToTransactionDomain(transaction);

        assertNotNull(result);
        assertEquals(Long.valueOf(5000005L), result.getAccountNumber());
        assertEquals(txDate, result.getTxDateTime());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(Double.valueOf(250.00), result.getTxAmount());
    }

    @Test
    public void testConvertToTransactionEntity() {
        Date txDate = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(6000006L)
                .txDateTime(txDate)
                .txType("CREDIT")
                .txAmount(500.00)
                .build();

        Transaction result = bankingServiceHelper.convertToTransactionEntity(transactionDetails);

        assertNotNull(result);
        assertEquals(Long.valueOf(6000006L), result.getAccountNumber());
        assertEquals(txDate, result.getTxDateTime());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(Double.valueOf(500.00), result.getTxAmount());
    }

    @Test
    public void testCreateTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000L);
        transferDetails.setToAccountNumber(2000L);
        transferDetails.setTransferAmount(100.00);

        Transaction result = bankingServiceHelper.createTransaction(transferDetails, 1000L, "DEBIT");

        assertNotNull(result);
        assertEquals(Long.valueOf(1000L), result.getAccountNumber());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(Double.valueOf(100.00), result.getTxAmount());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    public void testCreateTransactionCredit() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000L);
        transferDetails.setToAccountNumber(2000L);
        transferDetails.setTransferAmount(200.00);

        Transaction result = bankingServiceHelper.createTransaction(transferDetails, 2000L, "CREDIT");

        assertNotNull(result);
        assertEquals(Long.valueOf(2000L), result.getAccountNumber());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(Double.valueOf(200.00), result.getTxAmount());
        assertNotNull(result.getTxDateTime());
    }
}
