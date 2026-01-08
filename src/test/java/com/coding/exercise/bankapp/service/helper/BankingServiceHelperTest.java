package com.coding.exercise.bankapp.service.helper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

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

    @Test
    void testConvertToCustomerDomain() {
        Address address = Address.builder()
                .id(UUID.randomUUID())
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        Contact contact = Contact.builder()
                .id(UUID.randomUUID())
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .updateDateTime(new Date())
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(12345L, result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertNotNull(result.getCustomerAddress());
        assertNotNull(result.getContactDetails());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
        assertEquals("test@example.com", result.getContactDetails().getEmailId());
    }

    @Test
    void testConvertToCustomerEntity() {
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

        Customer result = helper.convertToCustomerEntity(customerDetails);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("A", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(67890L, result.getCustomerNumber());
        assertEquals("Inactive", result.getStatus());
        assertNotNull(result.getCustomerAddress());
        assertNotNull(result.getContactDetails());
        assertEquals("456 Oak Ave", result.getCustomerAddress().getAddress1());
        assertEquals("jane@example.com", result.getContactDetails().getEmailId());
    }

    @Test
    void testConvertToAccountDomain() {
        Address branchAddress = Address.builder()
                .address1("789 Bank St")
                .address2("")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .id(UUID.randomUUID())
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
                .updateDateTime(new Date())
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertNotNull(result);
        assertEquals(1000001L, result.getAccountNumber());
        assertEquals("Savings", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(5000.00, result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
        assertEquals(1001, result.getBankInformation().getBranchCode());
    }

    @Test
    void testConvertToAccountEntity() {
        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("100 Finance Blvd")
                .address2("Floor 5")
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

        AccountInformation accountInfo = AccountInformation.builder()
                .accountNumber(2000002L)
                .accountType("Checking")
                .accountStatus("Active")
                .accountBalance(10000.00)
                .bankInformation(bankInformation)
                .accountCreated(new Date())
                .build();

        Account result = helper.convertToAccountEntity(accountInfo);

        assertNotNull(result);
        assertEquals(2000002L, result.getAccountNumber());
        assertEquals("Checking", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(10000.00, result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("Downtown Branch", result.getBankInformation().getBranchName());
    }

    @Test
    void testConvertToAddressDomain() {
        Address address = Address.builder()
                .id(UUID.randomUUID())
                .address1("111 Test Lane")
                .address2("Unit B")
                .city("Seattle")
                .state("WA")
                .zip("98101")
                .country("USA")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertNotNull(result);
        assertEquals("111 Test Lane", result.getAddress1());
        assertEquals("Unit B", result.getAddress2());
        assertEquals("Seattle", result.getCity());
        assertEquals("WA", result.getState());
        assertEquals("98101", result.getZip());
        assertEquals("USA", result.getCountry());
    }

    @Test
    void testConvertToAddressEntity() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("222 Sample Rd")
                .address2("Apt 10")
                .city("Denver")
                .state("CO")
                .zip("80201")
                .country("USA")
                .build();

        Address result = helper.convertToAddressEntity(addressDetails);

        assertNotNull(result);
        assertEquals("222 Sample Rd", result.getAddress1());
        assertEquals("Apt 10", result.getAddress2());
        assertEquals("Denver", result.getCity());
        assertEquals("CO", result.getState());
        assertEquals("80201", result.getZip());
        assertEquals("USA", result.getCountry());
    }

    @Test
    void testConvertToContactDomain() {
        Contact contact = Contact.builder()
                .id(UUID.randomUUID())
                .emailId("contact@test.com")
                .homePhone("111-222-3333")
                .workPhone("444-555-6666")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertNotNull(result);
        assertEquals("contact@test.com", result.getEmailId());
        assertEquals("111-222-3333", result.getHomePhone());
        assertEquals("444-555-6666", result.getWorkPhone());
    }

    @Test
    void testConvertToContactEntity() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("entity@test.com")
                .homePhone("777-888-9999")
                .workPhone("000-111-2222")
                .build();

        Contact result = helper.convertToContactEntity(contactDetails);

        assertNotNull(result);
        assertEquals("entity@test.com", result.getEmailId());
        assertEquals("777-888-9999", result.getHomePhone());
        assertEquals("000-111-2222", result.getWorkPhone());
    }

    @Test
    void testConvertToBankInfoDomain() {
        Address branchAddress = Address.builder()
                .address1("333 Bank Plaza")
                .address2("")
                .city("Miami")
                .state("FL")
                .zip("33101")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .id(UUID.randomUUID())
                .branchName("South Branch")
                .branchCode(3003)
                .routingNumber(111222333)
                .branchAddress(branchAddress)
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertNotNull(result);
        assertEquals("South Branch", result.getBranchName());
        assertEquals(3003, result.getBranchCode());
        assertEquals(111222333, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("333 Bank Plaza", result.getBranchAddress().getAddress1());
    }

    @Test
    void testConvertToBankInfoEntity() {
        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("444 Finance Center")
                .address2("Suite 200")
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

        BankInfo result = helper.convertToBankInfoEntity(bankInformation);

        assertNotNull(result);
        assertEquals("West Branch", result.getBranchName());
        assertEquals(4004, result.getBranchCode());
        assertEquals(444555666, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("444 Finance Center", result.getBranchAddress().getAddress1());
    }

    @Test
    void testConvertToTransactionDomain() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .accountNumber(5000005L)
                .txDateTime(txDate)
                .txType("DEBIT")
                .txAmount(250.00)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertNotNull(result);
        assertEquals(5000005L, result.getAccountNumber());
        assertEquals(txDate, result.getTxDateTime());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(250.00, result.getTxAmount());
    }

    @Test
    void testConvertToTransactionEntity() {
        Date txDate = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(6000006L)
                .txDateTime(txDate)
                .txType("CREDIT")
                .txAmount(500.00)
                .build();

        Transaction result = helper.convertToTransactionEntity(transactionDetails);

        assertNotNull(result);
        assertEquals(6000006L, result.getAccountNumber());
        assertEquals(txDate, result.getTxDateTime());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(500.00, result.getTxAmount());
    }

    @Test
    void testCreateTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(100.00);

        Transaction debitTransaction = helper.createTransaction(transferDetails, 1001L, "DEBIT");

        assertNotNull(debitTransaction);
        assertEquals(1001L, debitTransaction.getAccountNumber());
        assertEquals("DEBIT", debitTransaction.getTxType());
        assertEquals(100.00, debitTransaction.getTxAmount());
        assertNotNull(debitTransaction.getTxDateTime());

        Transaction creditTransaction = helper.createTransaction(transferDetails, 1002L, "CREDIT");

        assertNotNull(creditTransaction);
        assertEquals(1002L, creditTransaction.getAccountNumber());
        assertEquals("CREDIT", creditTransaction.getTxType());
        assertEquals(100.00, creditTransaction.getTxAmount());
        assertNotNull(creditTransaction.getTxDateTime());
    }
}
