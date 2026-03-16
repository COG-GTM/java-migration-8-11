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

    // --- convertToCustomerDomain / convertToCustomerEntity ---

    @Test
    void convertToCustomerDomain_allFieldsMapped() {
        Address address = Address.builder()
                .address1("123 Main St").address2("Apt 4")
                .city("Springfield").state("IL").zip("62701").country("US")
                .build();
        Contact contact = Contact.builder()
                .emailId("john@example.com").homePhone("555-1234").workPhone("555-5678")
                .build();
        Customer customer = Customer.builder()
                .firstName("John").middleName("M").lastName("Doe")
                .customerNumber(1001L).status("ACTIVE")
                .customerAddress(address).contactDetails(contact)
                .build();

        CustomerDetails result = helper.convertToCustomerDomain(customer);

        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(1001L, result.getCustomerNumber());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
        assertEquals("Apt 4", result.getCustomerAddress().getAddress2());
        assertEquals("Springfield", result.getCustomerAddress().getCity());
        assertEquals("IL", result.getCustomerAddress().getState());
        assertEquals("62701", result.getCustomerAddress().getZip());
        assertEquals("US", result.getCustomerAddress().getCountry());
        assertEquals("john@example.com", result.getContactDetails().getEmailId());
        assertEquals("555-1234", result.getContactDetails().getHomePhone());
        assertEquals("555-5678", result.getContactDetails().getWorkPhone());
    }

    @Test
    void convertToCustomerEntity_allFieldsMapped() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("456 Oak Ave").address2("Suite 10")
                .city("Dallas").state("TX").zip("75201").country("US")
                .build();
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("jane@example.com").homePhone("555-9999").workPhone("555-0000")
                .build();
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("Jane").middleName("A").lastName("Smith")
                .customerNumber(2002L).status("INACTIVE")
                .customerAddress(addressDetails).contactDetails(contactDetails)
                .build();

        Customer result = helper.convertToCustomerEntity(customerDetails);

        assertEquals("Jane", result.getFirstName());
        assertEquals("A", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(2002L, result.getCustomerNumber());
        assertEquals("INACTIVE", result.getStatus());
        assertEquals("456 Oak Ave", result.getCustomerAddress().getAddress1());
        assertEquals("Suite 10", result.getCustomerAddress().getAddress2());
        assertEquals("Dallas", result.getCustomerAddress().getCity());
        assertEquals("TX", result.getCustomerAddress().getState());
        assertEquals("75201", result.getCustomerAddress().getZip());
        assertEquals("US", result.getCustomerAddress().getCountry());
        assertEquals("jane@example.com", result.getContactDetails().getEmailId());
        assertEquals("555-9999", result.getContactDetails().getHomePhone());
        assertEquals("555-0000", result.getContactDetails().getWorkPhone());
    }

    // --- convertToAccountDomain / convertToAccountEntity ---

    @Test
    void convertToAccountDomain_allFieldsMapped() {
        Address branchAddr = Address.builder()
                .address1("100 Bank St").address2("").city("NYC").state("NY").zip("10001").country("US")
                .build();
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch").branchCode(101).routingNumber(987654)
                .branchAddress(branchAddr)
                .build();
        Account account = Account.builder()
                .accountNumber(5001L).accountStatus("ACTIVE")
                .accountType("SAVINGS").accountBalance(1500.0)
                .bankInformation(bankInfo)
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertEquals(5001L, result.getAccountNumber());
        assertEquals("ACTIVE", result.getAccountStatus());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals(1500.0, result.getAccountBalance());
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
        assertEquals(101, result.getBankInformation().getBranchCode());
        assertEquals(987654, result.getBankInformation().getRoutingNumber());
        assertEquals("100 Bank St", result.getBankInformation().getBranchAddress().getAddress1());
    }

    @Test
    void convertToAccountEntity_allFieldsMapped() {
        AddressDetails branchAddrDetails = AddressDetails.builder()
                .address1("200 Finance Blvd").address2("Floor 3")
                .city("Chicago").state("IL").zip("60601").country("US")
                .build();
        BankInformation bankInformation = BankInformation.builder()
                .branchName("Downtown Branch").branchCode(202).routingNumber(123456)
                .branchAddress(branchAddrDetails)
                .build();
        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(6001L).accountStatus("ACTIVE")
                .accountType("CHECKING").accountBalance(3000.0)
                .bankInformation(bankInformation)
                .build();

        Account result = helper.convertToAccountEntity(accInfo);

        assertEquals(6001L, result.getAccountNumber());
        assertEquals("ACTIVE", result.getAccountStatus());
        assertEquals("CHECKING", result.getAccountType());
        assertEquals(3000.0, result.getAccountBalance());
        assertEquals("Downtown Branch", result.getBankInformation().getBranchName());
        assertEquals(202, result.getBankInformation().getBranchCode());
        assertEquals(123456, result.getBankInformation().getRoutingNumber());
        assertEquals("200 Finance Blvd", result.getBankInformation().getBranchAddress().getAddress1());
    }

    // --- convertToAddressDomain / convertToAddressEntity ---

    @Test
    void convertToAddressDomain_allFieldsMapped() {
        Address address = Address.builder()
                .address1("10 Elm St").address2("Unit B")
                .city("Boston").state("MA").zip("02101").country("US")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertEquals("10 Elm St", result.getAddress1());
        assertEquals("Unit B", result.getAddress2());
        assertEquals("Boston", result.getCity());
        assertEquals("MA", result.getState());
        assertEquals("02101", result.getZip());
        assertEquals("US", result.getCountry());
    }

    @Test
    void convertToAddressEntity_allFieldsMapped() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("20 Pine Rd").address2("")
                .city("Seattle").state("WA").zip("98101").country("US")
                .build();

        Address result = helper.convertToAddressEntity(addressDetails);

        assertEquals("20 Pine Rd", result.getAddress1());
        assertEquals("", result.getAddress2());
        assertEquals("Seattle", result.getCity());
        assertEquals("WA", result.getState());
        assertEquals("98101", result.getZip());
        assertEquals("US", result.getCountry());
    }

    // --- convertToContactDomain / convertToContactEntity ---

    @Test
    void convertToContactDomain_allFieldsMapped() {
        Contact contact = Contact.builder()
                .emailId("test@test.com").homePhone("111-2222").workPhone("333-4444")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertEquals("test@test.com", result.getEmailId());
        assertEquals("111-2222", result.getHomePhone());
        assertEquals("333-4444", result.getWorkPhone());
    }

    @Test
    void convertToContactEntity_allFieldsMapped() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("user@domain.com").homePhone("777-8888").workPhone("999-0000")
                .build();

        Contact result = helper.convertToContactEntity(contactDetails);

        assertEquals("user@domain.com", result.getEmailId());
        assertEquals("777-8888", result.getHomePhone());
        assertEquals("999-0000", result.getWorkPhone());
    }

    // --- convertToBankInfoDomain / convertToBankInfoEntity ---

    @Test
    void convertToBankInfoDomain_allFieldsMapped() {
        Address branchAddr = Address.builder()
                .address1("Bank Plaza").address2("").city("Miami").state("FL").zip("33101").country("US")
                .build();
        BankInfo bankInfo = BankInfo.builder()
                .branchName("South Branch").branchCode(301).routingNumber(111222)
                .branchAddress(branchAddr)
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertEquals("South Branch", result.getBranchName());
        assertEquals(301, result.getBranchCode());
        assertEquals(111222, result.getRoutingNumber());
        assertEquals("Bank Plaza", result.getBranchAddress().getAddress1());
        assertEquals("Miami", result.getBranchAddress().getCity());
    }

    @Test
    void convertToBankInfoEntity_allFieldsMapped() {
        AddressDetails branchAddrDetails = AddressDetails.builder()
                .address1("Finance Center").address2("").city("Denver").state("CO").zip("80201").country("US")
                .build();
        BankInformation bankInformation = BankInformation.builder()
                .branchName("West Branch").branchCode(401).routingNumber(333444)
                .branchAddress(branchAddrDetails)
                .build();

        BankInfo result = helper.convertToBankInfoEntity(bankInformation);

        assertEquals("West Branch", result.getBranchName());
        assertEquals(401, result.getBranchCode());
        assertEquals(333444, result.getRoutingNumber());
        assertEquals("Finance Center", result.getBranchAddress().getAddress1());
        assertEquals("Denver", result.getBranchAddress().getCity());
    }

    // --- convertToTransactionDomain / convertToTransactionEntity ---

    @Test
    void convertToTransactionDomain_allFieldsMapped() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(7001L).txAmount(250.0).txType("CREDIT").txDateTime(txDate)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(7001L, result.getAccountNumber());
        assertEquals(250.0, result.getTxAmount());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(txDate, result.getTxDateTime());
    }

    @Test
    void convertToTransactionEntity_allFieldsMapped() {
        Date txDate = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(8001L).txAmount(500.0).txType("DEBIT").txDateTime(txDate)
                .build();

        Transaction result = helper.convertToTransactionEntity(transactionDetails);

        assertEquals(8001L, result.getAccountNumber());
        assertEquals(500.0, result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(txDate, result.getTxDateTime());
    }

    // --- createTransaction ---

    @Test
    void createTransaction_setsCorrectFields() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000L);
        transferDetails.setToAccountNumber(2000L);
        transferDetails.setTransferAmount(100.0);

        Transaction result = helper.createTransaction(transferDetails, 1000L, "DEBIT");

        assertEquals(1000L, result.getAccountNumber());
        assertEquals(100.0, result.getTxAmount());
        assertEquals("DEBIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    void createTransaction_setsNonNullDateTime() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setTransferAmount(50.0);

        Date before = new Date();
        Transaction result = helper.createTransaction(transferDetails, 3000L, "CREDIT");
        Date after = new Date();

        assertNotNull(result.getTxDateTime());
        assertTrue(result.getTxDateTime().getTime() >= before.getTime());
        assertTrue(result.getTxDateTime().getTime() <= after.getTime());
    }

    // --- Null-handling edge cases ---

    @Test
    void convertToCustomerDomain_nullContactDetails_throwsNPE() {
        Customer customer = Customer.builder()
                .firstName("John").lastName("Doe").customerNumber(1L).status("ACTIVE")
                .customerAddress(Address.builder().address1("x").city("y").state("z").zip("0").country("US").build())
                .contactDetails(null)
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToCustomerDomain(customer));
    }

    @Test
    void convertToCustomerDomain_nullCustomerAddress_throwsNPE() {
        Customer customer = Customer.builder()
                .firstName("John").lastName("Doe").customerNumber(1L).status("ACTIVE")
                .customerAddress(null)
                .contactDetails(Contact.builder().emailId("a@b.com").homePhone("1").workPhone("2").build())
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToCustomerDomain(customer));
    }

    @Test
    void convertToCustomerEntity_nullContactDetails_throwsNPE() {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("Jane").lastName("Doe").customerNumber(2L).status("ACTIVE")
                .customerAddress(AddressDetails.builder().address1("x").city("y").state("z").zip("0").country("US").build())
                .contactDetails(null)
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToCustomerEntity(customerDetails));
    }

    @Test
    void convertToCustomerEntity_nullCustomerAddress_throwsNPE() {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("Jane").lastName("Doe").customerNumber(2L).status("ACTIVE")
                .customerAddress(null)
                .contactDetails(ContactDetails.builder().emailId("a@b.com").homePhone("1").workPhone("2").build())
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToCustomerEntity(customerDetails));
    }

    @Test
    void convertToAccountDomain_nullBankInformation_throwsNPE() {
        Account account = Account.builder()
                .accountNumber(1L).accountStatus("ACTIVE").accountType("SAVINGS").accountBalance(100.0)
                .bankInformation(null)
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToAccountDomain(account));
    }

    @Test
    void convertToAccountEntity_nullBankInformation_throwsNPE() {
        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(1L).accountStatus("ACTIVE").accountType("SAVINGS").accountBalance(100.0)
                .bankInformation(null)
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToAccountEntity(accInfo));
    }

    @Test
    void convertToBankInfoDomain_nullBranchAddress_throwsNPE() {
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Test").branchCode(1).routingNumber(2)
                .branchAddress(null)
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToBankInfoDomain(bankInfo));
    }

    @Test
    void convertToBankInfoEntity_nullBranchAddress_throwsNPE() {
        BankInformation bankInformation = BankInformation.builder()
                .branchName("Test").branchCode(1).routingNumber(2)
                .branchAddress(null)
                .build();

        assertThrows(NullPointerException.class, () -> helper.convertToBankInfoEntity(bankInformation));
    }
}
