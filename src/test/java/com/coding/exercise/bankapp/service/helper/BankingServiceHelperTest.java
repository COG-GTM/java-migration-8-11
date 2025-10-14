package com.coding.exercise.bankapp.service.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class BankingServiceHelperTest {

    private BankingServiceHelper helper;

    @BeforeEach
    void setUp() {
        helper = new BankingServiceHelper();
    }

    @Test
    @DisplayName("convertToCustomerDomain converts Customer entity to CustomerDetails DTO")
    void convertToCustomerDomain() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-0100")
                .workPhone("555-0101")
                .build();

        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4B")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("USA")
                .build();

        Customer customer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .middleName("Q")
                .lastName("Doe")
                .status("ACTIVE")
                .contactDetails(contact)
                .customerAddress(address)
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerNumber()).isEqualTo(12345L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getMiddleName()).isEqualTo("Q");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getContactDetails().getEmailId()).isEqualTo("test@example.com");
        assertThat(result.getCustomerAddress().getCity()).isEqualTo("Springfield");
    }

    @Test
    @DisplayName("convertToCustomerEntity converts CustomerDetails DTO to Customer entity")
    void convertToCustomerEntity() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("555-0100")
                .workPhone("555-0101")
                .build();

        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4B")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("USA")
                .build();

        CustomerDetails customerDetails = CustomerDetails.builder()
                .customerNumber(12345L)
                .firstName("John")
                .middleName("Q")
                .lastName("Doe")
                .status("ACTIVE")
                .contactDetails(contactDetails)
                .customerAddress(addressDetails)
                .build();

        Customer result = helper.convertToCustomerEntity(customerDetails);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerNumber()).isEqualTo(12345L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getMiddleName()).isEqualTo("Q");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getContactDetails().getEmailId()).isEqualTo("test@example.com");
        assertThat(result.getCustomerAddress().getCity()).isEqualTo("Springfield");
    }

    @Test
    @DisplayName("convertToAccountDomain converts Account entity to AccountInformation DTO")
    void convertToAccountDomain() {
        Address branchAddress = Address.builder()
                .address1("456 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchCode(1001)
                .branchName("Main Branch")
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        Account account = Account.builder()
                .accountNumber(98765L)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountStatus("ACTIVE")
                .bankInformation(bankInfo)
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo(98765L);
        assertThat(result.getAccountType()).isEqualTo("SAVINGS");
        assertThat(result.getAccountBalance()).isEqualTo(1000.0);
        assertThat(result.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(result.getBankInformation().getBranchCode()).isEqualTo(1001);
    }

    @Test
    @DisplayName("convertToAccountEntity converts AccountInformation DTO to Account entity")
    void convertToAccountEntity() {
        AddressDetails branchAddress = AddressDetails.builder()
                .address1("456 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInformation bankInfo = BankInformation.builder()
                .branchCode(1001)
                .branchName("Main Branch")
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        AccountInformation accountInfo = AccountInformation.builder()
                .accountNumber(98765L)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountStatus("ACTIVE")
                .bankInformation(bankInfo)
                .build();

        Account result = helper.convertToAccountEntity(accountInfo);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo(98765L);
        assertThat(result.getAccountType()).isEqualTo("SAVINGS");
        assertThat(result.getAccountBalance()).isEqualTo(1000.0);
        assertThat(result.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(result.getBankInformation().getBranchCode()).isEqualTo(1001);
    }

    @Test
    @DisplayName("convertToAddressDomain converts Address entity to AddressDetails DTO")
    void convertToAddressDomain() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4B")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("USA")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertThat(result).isNotNull();
        assertThat(result.getAddress1()).isEqualTo("123 Main St");
        assertThat(result.getAddress2()).isEqualTo("Apt 4B");
        assertThat(result.getCity()).isEqualTo("Springfield");
        assertThat(result.getState()).isEqualTo("IL");
        assertThat(result.getZip()).isEqualTo("62701");
        assertThat(result.getCountry()).isEqualTo("USA");
    }

    @Test
    @DisplayName("convertToAddressEntity converts AddressDetails DTO to Address entity")
    void convertToAddressEntity() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4B")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("USA")
                .build();

        Address result = helper.convertToAddressEntity(addressDetails);

        assertThat(result).isNotNull();
        assertThat(result.getAddress1()).isEqualTo("123 Main St");
        assertThat(result.getAddress2()).isEqualTo("Apt 4B");
        assertThat(result.getCity()).isEqualTo("Springfield");
        assertThat(result.getState()).isEqualTo("IL");
        assertThat(result.getZip()).isEqualTo("62701");
        assertThat(result.getCountry()).isEqualTo("USA");
    }

    @Test
    @DisplayName("convertToContactDomain converts Contact entity to ContactDetails DTO")
    void convertToContactDomain() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-0100")
                .workPhone("555-0101")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertThat(result).isNotNull();
        assertThat(result.getEmailId()).isEqualTo("test@example.com");
        assertThat(result.getHomePhone()).isEqualTo("555-0100");
        assertThat(result.getWorkPhone()).isEqualTo("555-0101");
    }

    @Test
    @DisplayName("convertToContactEntity converts ContactDetails DTO to Contact entity")
    void convertToContactEntity() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("555-0100")
                .workPhone("555-0101")
                .build();

        Contact result = helper.convertToContactEntity(contactDetails);

        assertThat(result).isNotNull();
        assertThat(result.getEmailId()).isEqualTo("test@example.com");
        assertThat(result.getHomePhone()).isEqualTo("555-0100");
        assertThat(result.getWorkPhone()).isEqualTo("555-0101");
    }

    @Test
    @DisplayName("convertToBankInfoDomain converts BankInfo entity to BankInformation DTO")
    void convertToBankInfoDomain() {
        Address branchAddress = Address.builder()
                .address1("456 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchCode(1001)
                .branchName("Main Branch")
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertThat(result).isNotNull();
        assertThat(result.getBranchCode()).isEqualTo(1001);
        assertThat(result.getBranchName()).isEqualTo("Main Branch");
        assertThat(result.getRoutingNumber()).isEqualTo(123456789);
        assertThat(result.getBranchAddress().getCity()).isEqualTo("Chicago");
    }

    @Test
    @DisplayName("convertToBankInfoEntity converts BankInformation DTO to BankInfo entity")
    void convertToBankInfoEntity() {
        AddressDetails branchAddress = AddressDetails.builder()
                .address1("456 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInformation bankInfo = BankInformation.builder()
                .branchCode(1001)
                .branchName("Main Branch")
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        BankInfo result = helper.convertToBankInfoEntity(bankInfo);

        assertThat(result).isNotNull();
        assertThat(result.getBranchCode()).isEqualTo(1001);
        assertThat(result.getBranchName()).isEqualTo("Main Branch");
        assertThat(result.getRoutingNumber()).isEqualTo(123456789);
        assertThat(result.getBranchAddress().getCity()).isEqualTo("Chicago");
    }

    @Test
    @DisplayName("convertToTransactionDomain converts Transaction entity to TransactionDetails DTO")
    void convertToTransactionDomain() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(12345L)
                .txAmount(500.0)
                .txDateTime(txDate)
                .txType("DEBIT")
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo(12345L);
        assertThat(result.getTxAmount()).isEqualTo(500.0);
        assertThat(result.getTxDateTime()).isEqualTo(txDate);
        assertThat(result.getTxType()).isEqualTo("DEBIT");
    }

    @Test
    @DisplayName("convertToTransactionEntity converts TransactionDetails DTO to Transaction entity")
    void convertToTransactionEntity() {
        Date txDate = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(12345L)
                .txAmount(500.0)
                .txDateTime(txDate)
                .txType("CREDIT")
                .build();

        Transaction result = helper.convertToTransactionEntity(transactionDetails);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo(12345L);
        assertThat(result.getTxAmount()).isEqualTo(500.0);
        assertThat(result.getTxDateTime()).isEqualTo(txDate);
        assertThat(result.getTxType()).isEqualTo("CREDIT");
    }

    @Test
    @DisplayName("createTransaction creates Transaction from TransferDetails")
    void createTransaction() {
        TransferDetails transferDetails = new TransferDetails(11111L, 22222L, 250.0);

        Transaction result = helper.createTransaction(transferDetails, 11111L, "DEBIT");

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo(11111L);
        assertThat(result.getTxAmount()).isEqualTo(250.0);
        assertThat(result.getTxType()).isEqualTo("DEBIT");
        assertThat(result.getTxDateTime()).isNotNull();
    }
}
