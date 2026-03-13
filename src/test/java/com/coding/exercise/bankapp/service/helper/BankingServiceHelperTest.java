package com.coding.exercise.bankapp.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    // ========== Customer Entity -> CustomerDetails DTO ==========

    @Test
    void convertToCustomerDomain_allFields() {
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(Contact.builder()
                        .emailId("john@example.com")
                        .homePhone("111-222-3333")
                        .workPhone("444-555-6666")
                        .build())
                .customerAddress(Address.builder()
                        .address1("123 Main St")
                        .address2("Apt 4")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("M", result.getMiddleName());
        assertEquals(1001L, result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertEquals("john@example.com", result.getContactDetails().getEmailId());
        assertEquals("111-222-3333", result.getContactDetails().getHomePhone());
        assertEquals("444-555-6666", result.getContactDetails().getWorkPhone());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
        assertEquals("Apt 4", result.getCustomerAddress().getAddress2());
        assertEquals("Springfield", result.getCustomerAddress().getCity());
        assertEquals("IL", result.getCustomerAddress().getState());
        assertEquals("62701", result.getCustomerAddress().getZip());
        assertEquals("US", result.getCustomerAddress().getCountry());
    }

    @Test
    void convertToCustomerDomain_nullMiddleName() {
        Customer customer = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(1002L)
                .status("Active")
                .contactDetails(Contact.builder().emailId("jane@example.com").build())
                .customerAddress(Address.builder().address1("456 Oak").city("Chicago").build())
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("Jane", result.getFirstName());
        assertNull(result.getMiddleName());
    }

    @Test
    void convertToCustomerDomain_emptyStrings() {
        Customer customer = Customer.builder()
                .firstName("")
                .lastName("")
                .middleName("")
                .customerNumber(0L)
                .status("")
                .contactDetails(Contact.builder().emailId("").homePhone("").workPhone("").build())
                .customerAddress(Address.builder().address1("").address2("").city("").state("").zip("").country("").build())
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        assertEquals(0L, result.getCustomerNumber());
    }

    // ========== CustomerDetails DTO -> Customer Entity ==========

    @Test
    void convertToCustomerEntity_allFields() {
        CustomerDetails details = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com")
                        .homePhone("111")
                        .workPhone("222")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main")
                        .address2("Suite 1")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .build();

        Customer result = helper.convertToCustomerEntity(details);

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("M", result.getMiddleName());
        assertEquals(1001L, result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertEquals("john@example.com", result.getContactDetails().getEmailId());
        assertNotNull(result.getCustomerAddress());
        assertEquals("123 Main", result.getCustomerAddress().getAddress1());
    }

    // ========== Account Entity -> AccountInformation DTO ==========

    @Test
    void convertToAccountDomain_allFields() {
        Account account = Account.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(2500.50)
                .bankInformation(BankInfo.builder()
                        .branchName("Main Branch")
                        .branchCode(101)
                        .routingNumber(12345)
                        .branchAddress(Address.builder()
                                .address1("100 Bank St")
                                .city("Chicago")
                                .state("IL")
                                .zip("60601")
                                .country("US")
                                .build())
                        .build())
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(5001L, result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(2500.50, result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
        assertEquals(101, result.getBankInformation().getBranchCode());
        assertEquals(12345, result.getBankInformation().getRoutingNumber());
        assertEquals("100 Bank St", result.getBankInformation().getBranchAddress().getAddress1());
    }

    @Test
    void convertToAccountDomain_zeroBalance() {
        Account account = Account.builder()
                .accountNumber(5002L)
                .accountType("CHECKING")
                .accountStatus("Active")
                .accountBalance(0.0)
                .bankInformation(BankInfo.builder()
                        .branchName("Sub Branch")
                        .branchCode(202)
                        .routingNumber(67890)
                        .branchAddress(Address.builder().address1("200 Side St").city("NYC").build())
                        .build())
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(0.0, result.getAccountBalance());
    }

    // ========== AccountInformation DTO -> Account Entity ==========

    @Test
    void convertToAccountEntity_allFields() {
        AccountInformation info = AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("Main Branch")
                        .branchCode(101)
                        .routingNumber(12345)
                        .branchAddress(AddressDetails.builder()
                                .address1("100 Bank St")
                                .city("Chicago")
                                .build())
                        .build())
                .build();

        Account result = helper.convertToAccountEntity(info);

        assertEquals(5001L, result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(1000.0, result.getAccountBalance());
        assertNotNull(result.getBankInformation());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
    }

    // ========== TransactionDetails DTO <-> Transaction Entity ==========

    @Test
    void convertToTransactionDomain_allFields() {
        Date now = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(5001L)
                .txType("CREDIT")
                .txAmount(500.0)
                .txDateTime(now)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(5001L, result.getAccountNumber());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(500.0, result.getTxAmount());
        assertEquals(now, result.getTxDateTime());
    }

    @Test
    void convertToTransactionEntity_allFields() {
        Date now = new Date();
        TransactionDetails details = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("DEBIT")
                .txAmount(250.0)
                .txDateTime(now)
                .build();

        Transaction result = helper.convertToTransactionEntity(details);

        assertEquals(5001L, result.getAccountNumber());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(250.0, result.getTxAmount());
        assertEquals(now, result.getTxDateTime());
    }

    @Test
    void convertToTransactionDomain_zeroAmount() {
        Transaction transaction = Transaction.builder()
                .accountNumber(5001L)
                .txType("CREDIT")
                .txAmount(0.0)
                .txDateTime(new Date())
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(0.0, result.getTxAmount());
    }

    @Test
    void convertToTransactionDomain_nullTxType() {
        Transaction transaction = Transaction.builder()
                .accountNumber(5001L)
                .txAmount(100.0)
                .txDateTime(new Date())
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertNull(result.getTxType());
    }

    // ========== createTransaction ==========

    @Test
    void createTransaction_debit() {
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 300.0);

        Transaction result = helper.createTransaction(transferDetails, 5001L, "DEBIT");

        assertEquals(5001L, result.getAccountNumber());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(300.0, result.getTxAmount());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    void createTransaction_credit() {
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 300.0);

        Transaction result = helper.createTransaction(transferDetails, 5002L, "CREDIT");

        assertEquals(5002L, result.getAccountNumber());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(300.0, result.getTxAmount());
        assertNotNull(result.getTxDateTime());
    }

    // ========== Address conversions ==========

    @Test
    void convertToAddressDomain_allFields() {
        Address address = Address.builder()
                .address1("123 Main")
                .address2("Apt 1")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertEquals("123 Main", result.getAddress1());
        assertEquals("Apt 1", result.getAddress2());
        assertEquals("Springfield", result.getCity());
        assertEquals("IL", result.getState());
        assertEquals("62701", result.getZip());
        assertEquals("US", result.getCountry());
    }

    @Test
    void convertToAddressEntity_allFields() {
        AddressDetails details = AddressDetails.builder()
                .address1("123 Main")
                .address2("Apt 1")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        Address result = helper.convertToAddressEntity(details);

        assertEquals("123 Main", result.getAddress1());
        assertEquals("Apt 1", result.getAddress2());
        assertEquals("Springfield", result.getCity());
        assertEquals("IL", result.getState());
        assertEquals("62701", result.getZip());
        assertEquals("US", result.getCountry());
    }

    // ========== Contact conversions ==========

    @Test
    void convertToContactDomain_allFields() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("111-222-3333")
                .workPhone("444-555-6666")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertEquals("test@example.com", result.getEmailId());
        assertEquals("111-222-3333", result.getHomePhone());
        assertEquals("444-555-6666", result.getWorkPhone());
    }

    @Test
    void convertToContactEntity_allFields() {
        ContactDetails details = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("111")
                .workPhone("222")
                .build();

        Contact result = helper.convertToContactEntity(details);

        assertEquals("test@example.com", result.getEmailId());
        assertEquals("111", result.getHomePhone());
        assertEquals("222", result.getWorkPhone());
    }

    @Test
    void convertToContactDomain_nullFields() {
        Contact contact = Contact.builder().build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertNull(result.getEmailId());
        assertNull(result.getHomePhone());
        assertNull(result.getWorkPhone());
    }

    // ========== BankInfo conversions ==========

    @Test
    void convertToBankInfoDomain_allFields() {
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main")
                .branchCode(101)
                .routingNumber(12345)
                .branchAddress(Address.builder()
                        .address1("100 Bank St")
                        .city("Chicago")
                        .state("IL")
                        .zip("60601")
                        .country("US")
                        .build())
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertEquals("Main", result.getBranchName());
        assertEquals(101, result.getBranchCode());
        assertEquals(12345, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("100 Bank St", result.getBranchAddress().getAddress1());
    }

    @Test
    void convertToBankInfoEntity_allFields() {
        BankInformation info = BankInformation.builder()
                .branchName("Main")
                .branchCode(101)
                .routingNumber(12345)
                .branchAddress(AddressDetails.builder()
                        .address1("100 Bank St")
                        .city("Chicago")
                        .build())
                .build();

        BankInfo result = helper.convertToBankInfoEntity(info);

        assertEquals("Main", result.getBranchName());
        assertEquals(101, result.getBranchCode());
        assertEquals(12345, result.getRoutingNumber());
        assertNotNull(result.getBranchAddress());
        assertEquals("100 Bank St", result.getBranchAddress().getAddress1());
    }

    // ========== Boundary value tests ==========

    @Test
    void convertToAccountDomain_largeBalance() {
        Account account = Account.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(Double.MAX_VALUE)
                .bankInformation(BankInfo.builder()
                        .branchName("Branch")
                        .branchCode(1)
                        .routingNumber(1)
                        .branchAddress(Address.builder().address1("addr").build())
                        .build())
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(Double.MAX_VALUE, result.getAccountBalance());
    }

    @Test
    void convertToTransactionEntity_largeAmount() {
        TransactionDetails details = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("CREDIT")
                .txAmount(Double.MAX_VALUE)
                .txDateTime(new Date())
                .build();

        Transaction result = helper.convertToTransactionEntity(details);

        assertEquals(Double.MAX_VALUE, result.getTxAmount());
    }
}
