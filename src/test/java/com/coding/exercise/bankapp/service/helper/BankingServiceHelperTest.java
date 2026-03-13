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

    // ========== Customer Conversion ==========

    @Test
    void convertToCustomerDomain_mapsAllFields() {
        Customer customer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(100L)
                .status("ACTIVE")
                .contactDetails(Contact.builder()
                        .emailId("john@example.com")
                        .homePhone("555-1234")
                        .workPhone("555-5678")
                        .build())
                .customerAddress(Address.builder()
                        .address1("123 Main St")
                        .address2("Apt 4B")
                        .city("New York")
                        .state("NY")
                        .zip("10001")
                        .country("USA")
                        .build())
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(100L, result.getCustomerNumber());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertEquals("john@example.com", result.getContactDetails().getEmailId());
        assertEquals("555-1234", result.getContactDetails().getHomePhone());
        assertEquals("555-5678", result.getContactDetails().getWorkPhone());
        assertNotNull(result.getCustomerAddress());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
        assertEquals("Apt 4B", result.getCustomerAddress().getAddress2());
        assertEquals("New York", result.getCustomerAddress().getCity());
        assertEquals("NY", result.getCustomerAddress().getState());
        assertEquals("10001", result.getCustomerAddress().getZip());
        assertEquals("USA", result.getCustomerAddress().getCountry());
    }

    @Test
    void convertToCustomerEntity_mapsAllFields() {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("Jane")
                .middleName("A")
                .lastName("Smith")
                .customerNumber(200L)
                .status("INACTIVE")
                .contactDetails(ContactDetails.builder()
                        .emailId("jane@example.com")
                        .homePhone("555-0001")
                        .workPhone("555-0002")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("456 Elm St")
                        .address2("Suite 10")
                        .city("Boston")
                        .state("MA")
                        .zip("02101")
                        .country("USA")
                        .build())
                .build();

        Customer result = helper.convertToCustomerEntity(customerDetails);

        assertEquals("Jane", result.getFirstName());
        assertEquals("A", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(200L, result.getCustomerNumber());
        assertEquals("INACTIVE", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertEquals("jane@example.com", result.getContactDetails().getEmailId());
        assertEquals("555-0001", result.getContactDetails().getHomePhone());
        assertEquals("555-0002", result.getContactDetails().getWorkPhone());
        assertNotNull(result.getCustomerAddress());
        assertEquals("456 Elm St", result.getCustomerAddress().getAddress1());
        assertEquals("Suite 10", result.getCustomerAddress().getAddress2());
        assertEquals("Boston", result.getCustomerAddress().getCity());
        assertEquals("MA", result.getCustomerAddress().getState());
        assertEquals("02101", result.getCustomerAddress().getZip());
        assertEquals("USA", result.getCustomerAddress().getCountry());
    }

    // ========== Account Conversion ==========

    @Test
    void convertToAccountDomain_mapsAllFields() {
        Account account = Account.builder()
                .accountNumber(1001L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(5000.0)
                .bankInformation(BankInfo.builder()
                        .branchName("Main Branch")
                        .branchCode(1001)
                        .routingNumber(12345)
                        .branchAddress(Address.builder()
                                .address1("789 Bank Ave")
                                .address2("Floor 2")
                                .city("Chicago")
                                .state("IL")
                                .zip("60601")
                                .country("USA")
                                .build())
                        .build())
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(1001L, result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals("ACTIVE", result.getAccountStatus());
        assertEquals(5000.0, result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
        assertEquals(1001, result.getBankInformation().getBranchCode());
        assertEquals(12345, result.getBankInformation().getRoutingNumber());
        assertNotNull(result.getBankInformation().getBranchAddress());
        assertEquals("789 Bank Ave", result.getBankInformation().getBranchAddress().getAddress1());
        assertEquals("Chicago", result.getBankInformation().getBranchAddress().getCity());
    }

    @Test
    void convertToAccountEntity_mapsAllFields() {
        AccountInformation accountInfo = AccountInformation.builder()
                .accountNumber(2002L)
                .accountType("CHECKING")
                .accountStatus("ACTIVE")
                .accountBalance(10000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("West Branch")
                        .branchCode(2002)
                        .routingNumber(67890)
                        .branchAddress(AddressDetails.builder()
                                .address1("321 West Ave")
                                .address2("")
                                .city("Los Angeles")
                                .state("CA")
                                .zip("90001")
                                .country("USA")
                                .build())
                        .build())
                .build();

        Account result = helper.convertToAccountEntity(accountInfo);

        assertEquals(2002L, result.getAccountNumber());
        assertEquals("CHECKING", result.getAccountType());
        assertEquals("ACTIVE", result.getAccountStatus());
        assertEquals(10000.0, result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("West Branch", result.getBankInformation().getBranchName());
        assertEquals(2002, result.getBankInformation().getBranchCode());
        assertEquals(67890, result.getBankInformation().getRoutingNumber());
        assertNotNull(result.getBankInformation().getBranchAddress());
        assertEquals("321 West Ave", result.getBankInformation().getBranchAddress().getAddress1());
        assertEquals("Los Angeles", result.getBankInformation().getBranchAddress().getCity());
    }

    // ========== Address Conversion ==========

    @Test
    void convertToAddressDomain_mapsAllFields() {
        Address address = Address.builder()
                .address1("100 Test St")
                .address2("Unit 5")
                .city("Seattle")
                .state("WA")
                .zip("98101")
                .country("USA")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertEquals("100 Test St", result.getAddress1());
        assertEquals("Unit 5", result.getAddress2());
        assertEquals("Seattle", result.getCity());
        assertEquals("WA", result.getState());
        assertEquals("98101", result.getZip());
        assertEquals("USA", result.getCountry());
    }

    @Test
    void convertToAddressEntity_mapsAllFields() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("200 Test Ave")
                .address2("Suite 3")
                .city("Portland")
                .state("OR")
                .zip("97201")
                .country("USA")
                .build();

        Address result = helper.convertToAddressEntity(addressDetails);

        assertEquals("200 Test Ave", result.getAddress1());
        assertEquals("Suite 3", result.getAddress2());
        assertEquals("Portland", result.getCity());
        assertEquals("OR", result.getState());
        assertEquals("97201", result.getZip());
        assertEquals("USA", result.getCountry());
    }

    // ========== Contact Conversion ==========

    @Test
    void convertToContactDomain_mapsAllFields() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1111")
                .workPhone("555-2222")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertEquals("test@example.com", result.getEmailId());
        assertEquals("555-1111", result.getHomePhone());
        assertEquals("555-2222", result.getWorkPhone());
    }

    @Test
    void convertToContactEntity_mapsAllFields() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("contact@example.com")
                .homePhone("555-3333")
                .workPhone("555-4444")
                .build();

        Contact result = helper.convertToContactEntity(contactDetails);

        assertEquals("contact@example.com", result.getEmailId());
        assertEquals("555-3333", result.getHomePhone());
        assertEquals("555-4444", result.getWorkPhone());
    }

    // ========== BankInfo Conversion ==========

    @Test
    void convertToBankInfoDomain_mapsAllFields() {
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Central Branch")
                .branchCode(3003)
                .routingNumber(11111)
                .branchAddress(Address.builder()
                        .address1("500 Finance Blvd")
                        .address2("")
                        .city("Dallas")
                        .state("TX")
                        .zip("75201")
                        .country("USA")
                        .build())
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertEquals("Central Branch", result.getBranchName());
        assertEquals(3003, result.getBranchCode());
        assertEquals(11111, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("500 Finance Blvd", result.getBranchAddress().getAddress1());
        assertEquals("Dallas", result.getBranchAddress().getCity());
    }

    @Test
    void convertToBankInfoEntity_mapsAllFields() {
        BankInformation bankInformation = BankInformation.builder()
                .branchName("East Branch")
                .branchCode(4004)
                .routingNumber(22222)
                .branchAddress(AddressDetails.builder()
                        .address1("600 Commerce St")
                        .address2("Bldg A")
                        .city("Miami")
                        .state("FL")
                        .zip("33101")
                        .country("USA")
                        .build())
                .build();

        BankInfo result = helper.convertToBankInfoEntity(bankInformation);

        assertEquals("East Branch", result.getBranchName());
        assertEquals(4004, result.getBranchCode());
        assertEquals(22222, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("600 Commerce St", result.getBranchAddress().getAddress1());
        assertEquals("Miami", result.getBranchAddress().getCity());
    }

    // ========== Transaction Conversion ==========

    @Test
    void convertToTransactionDomain_mapsAllFields() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(1001L)
                .txType("CREDIT")
                .txAmount(500.0)
                .txDateTime(txDate)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(1001L, result.getAccountNumber());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(500.0, result.getTxAmount());
        assertEquals(txDate, result.getTxDateTime());
    }

    @Test
    void convertToTransactionEntity_mapsAllFields() {
        Date txDate = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(2002L)
                .txType("DEBIT")
                .txAmount(300.0)
                .txDateTime(txDate)
                .build();

        Transaction result = helper.convertToTransactionEntity(transactionDetails);

        assertEquals(2002L, result.getAccountNumber());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(300.0, result.getTxAmount());
        assertEquals(txDate, result.getTxDateTime());
    }

    // ========== createTransaction ==========

    @Test
    void createTransaction_debit_createsCorrectTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(750.0);

        Transaction result = helper.createTransaction(transferDetails, 1001L, "DEBIT");

        assertEquals(1001L, result.getAccountNumber());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(750.0, result.getTxAmount());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    void createTransaction_credit_createsCorrectTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(750.0);

        Transaction result = helper.createTransaction(transferDetails, 1002L, "CREDIT");

        assertEquals(1002L, result.getAccountNumber());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(750.0, result.getTxAmount());
        assertNotNull(result.getTxDateTime());
    }

    // ========== Round-trip Conversion Tests ==========

    @Test
    void customerRoundTrip_entityToDomainToEntity_preservesFields() {
        Customer original = Customer.builder()
                .firstName("Test")
                .middleName("T")
                .lastName("User")
                .customerNumber(999L)
                .status("ACTIVE")
                .contactDetails(Contact.builder()
                        .emailId("roundtrip@test.com")
                        .homePhone("555-9999")
                        .workPhone("555-8888")
                        .build())
                .customerAddress(Address.builder()
                        .address1("Round Trip Ln")
                        .address2("")
                        .city("TestCity")
                        .state("TS")
                        .zip("00000")
                        .country("USA")
                        .build())
                .build();

        CustomerDetails domain = helper.convertToCustomerDomain(original);
        Customer result = helper.convertToCustomerEntity(domain);

        assertEquals(original.getFirstName(), result.getFirstName());
        assertEquals(original.getMiddleName(), result.getMiddleName());
        assertEquals(original.getLastName(), result.getLastName());
        assertEquals(original.getCustomerNumber(), result.getCustomerNumber());
        assertEquals(original.getStatus(), result.getStatus());
    }

    @Test
    void accountRoundTrip_entityToDomainToEntity_preservesFields() {
        Account original = Account.builder()
                .accountNumber(5555L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(25000.0)
                .bankInformation(BankInfo.builder()
                        .branchName("RT Branch")
                        .branchCode(7777)
                        .routingNumber(88888)
                        .branchAddress(Address.builder()
                                .address1("RT Bank St")
                                .address2("")
                                .city("RTCity")
                                .state("RT")
                                .zip("99999")
                                .country("USA")
                                .build())
                        .build())
                .build();

        AccountInformation domain = helper.convertToAccountDomain(original);
        Account result = helper.convertToAccountEntity(domain);

        assertEquals(original.getAccountNumber(), result.getAccountNumber());
        assertEquals(original.getAccountType(), result.getAccountType());
        assertEquals(original.getAccountStatus(), result.getAccountStatus());
        assertEquals(original.getAccountBalance(), result.getAccountBalance());
    }

    @Test
    void transactionRoundTrip_entityToDomainToEntity_preservesFields() {
        Date txDate = new Date();
        Transaction original = Transaction.builder()
                .accountNumber(3333L)
                .txType("CREDIT")
                .txAmount(1500.0)
                .txDateTime(txDate)
                .build();

        TransactionDetails domain = helper.convertToTransactionDomain(original);
        Transaction result = helper.convertToTransactionEntity(domain);

        assertEquals(original.getAccountNumber(), result.getAccountNumber());
        assertEquals(original.getTxType(), result.getTxType());
        assertEquals(original.getTxAmount(), result.getTxAmount());
        assertEquals(original.getTxDateTime(), result.getTxDateTime());
    }
}
