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

public class MapperHelperTest {

    private BankingServiceHelper helper;

    @Before
    public void setUp() {
        helper = new BankingServiceHelper();
    }

    // ===================== Customer mappings =====================

    @Test
    public void convertToCustomerDomain_mapsAllFields() {
        Address address = Address.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();
        Contact contact = Contact.builder()
                .emailId("john@example.com")
                .homePhone("111-222-3333")
                .workPhone("444-555-6666")
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

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Doe", result.getLastName());
        assertEquals(Long.valueOf(1001L), result.getCustomerNumber());
        assertEquals("Active", result.getStatus());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
        assertEquals("Apt 4", result.getCustomerAddress().getAddress2());
        assertEquals("Springfield", result.getCustomerAddress().getCity());
        assertEquals("IL", result.getCustomerAddress().getState());
        assertEquals("62701", result.getCustomerAddress().getZip());
        assertEquals("US", result.getCustomerAddress().getCountry());
        assertEquals("john@example.com", result.getContactDetails().getEmailId());
        assertEquals("111-222-3333", result.getContactDetails().getHomePhone());
        assertEquals("444-555-6666", result.getContactDetails().getWorkPhone());
    }

    @Test
    public void convertToCustomerEntity_mapsAllFields() {
        CustomerDetails details = CustomerDetails.builder()
                .firstName("Jane")
                .middleName("A")
                .lastName("Smith")
                .customerNumber(2002L)
                .status("Inactive")
                .customerAddress(AddressDetails.builder()
                        .address1("456 Oak Ave")
                        .address2("")
                        .city("Shelbyville")
                        .state("IN")
                        .zip("46176")
                        .country("US")
                        .build())
                .contactDetails(ContactDetails.builder()
                        .emailId("jane@example.com")
                        .homePhone("555-111-2222")
                        .workPhone("555-333-4444")
                        .build())
                .build();

        Customer result = helper.convertToCustomerEntity(details);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("A", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(Long.valueOf(2002L), result.getCustomerNumber());
        assertEquals("Inactive", result.getStatus());
        assertEquals("456 Oak Ave", result.getCustomerAddress().getAddress1());
        assertEquals("jane@example.com", result.getContactDetails().getEmailId());
    }

    // ===================== Account mappings =====================

    @Test
    public void convertToAccountDomain_mapsAllFields() {
        Address branchAddr = Address.builder()
                .address1("100 Bank St")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(101)
                .routingNumber(12345)
                .branchAddress(branchAddr)
                .build();
        Account account = Account.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(bankInfo)
                .build();

        AccountInformation result = helper.convertToAccountDomain(account);

        assertNotNull(result);
        assertEquals(Long.valueOf(5001L), result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(1000.0, result.getAccountBalance(), 0.001);
        assertEquals("Main Branch", result.getBankInformation().getBranchName());
        assertEquals(Integer.valueOf(101), result.getBankInformation().getBranchCode());
        assertEquals(Integer.valueOf(12345), result.getBankInformation().getRoutingNumber());
        assertEquals("100 Bank St", result.getBankInformation().getBranchAddress().getAddress1());
    }

    @Test
    public void convertToAccountEntity_mapsAllFields() {
        AccountInformation info = AccountInformation.builder()
                .accountNumber(5002L)
                .accountType("CHECKING")
                .accountStatus("Active")
                .accountBalance(2000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("West Branch")
                        .branchCode(202)
                        .routingNumber(67890)
                        .branchAddress(AddressDetails.builder()
                                .address1("200 West Rd")
                                .city("Shelbyville")
                                .state("IN")
                                .zip("46176")
                                .country("US")
                                .build())
                        .build())
                .build();

        Account result = helper.convertToAccountEntity(info);

        assertNotNull(result);
        assertEquals(Long.valueOf(5002L), result.getAccountNumber());
        assertEquals("CHECKING", result.getAccountType());
        assertEquals("Active", result.getAccountStatus());
        assertEquals(2000.0, result.getAccountBalance(), 0.001);
        assertEquals("West Branch", result.getBankInformation().getBranchName());
    }

    // ===================== Address mappings =====================

    @Test
    public void convertToAddressDomain_mapsAllFields() {
        Address address = Address.builder()
                .address1("10 Elm St")
                .address2("Suite 100")
                .city("Capital City")
                .state("CA")
                .zip("90210")
                .country("US")
                .build();

        AddressDetails result = helper.convertToAddressDomain(address);

        assertEquals("10 Elm St", result.getAddress1());
        assertEquals("Suite 100", result.getAddress2());
        assertEquals("Capital City", result.getCity());
        assertEquals("CA", result.getState());
        assertEquals("90210", result.getZip());
        assertEquals("US", result.getCountry());
    }

    @Test
    public void convertToAddressEntity_mapsAllFields() {
        AddressDetails details = AddressDetails.builder()
                .address1("20 Maple Dr")
                .address2("")
                .city("Ogdenville")
                .state("NY")
                .zip("10001")
                .country("US")
                .build();

        Address result = helper.convertToAddressEntity(details);

        assertEquals("20 Maple Dr", result.getAddress1());
        assertEquals("", result.getAddress2());
        assertEquals("Ogdenville", result.getCity());
        assertEquals("NY", result.getState());
        assertEquals("10001", result.getZip());
        assertEquals("US", result.getCountry());
    }

    // ===================== Contact mappings =====================

    @Test
    public void convertToContactDomain_mapsAllFields() {
        Contact contact = Contact.builder()
                .emailId("test@test.com")
                .homePhone("123-456-7890")
                .workPhone("098-765-4321")
                .build();

        ContactDetails result = helper.convertToContactDomain(contact);

        assertEquals("test@test.com", result.getEmailId());
        assertEquals("123-456-7890", result.getHomePhone());
        assertEquals("098-765-4321", result.getWorkPhone());
    }

    @Test
    public void convertToContactEntity_mapsAllFields() {
        ContactDetails details = ContactDetails.builder()
                .emailId("other@test.com")
                .homePhone("111-111-1111")
                .workPhone("222-222-2222")
                .build();

        Contact result = helper.convertToContactEntity(details);

        assertEquals("other@test.com", result.getEmailId());
        assertEquals("111-111-1111", result.getHomePhone());
        assertEquals("222-222-2222", result.getWorkPhone());
    }

    // ===================== BankInfo mappings =====================

    @Test
    public void convertToBankInfoDomain_mapsAllFields() {
        Address branchAddr = Address.builder()
                .address1("50 Finance Blvd")
                .city("Metropolis")
                .state("NY")
                .zip("10001")
                .country("US")
                .build();
        BankInfo bankInfo = BankInfo.builder()
                .branchName("Downtown Branch")
                .branchCode(303)
                .routingNumber(11111)
                .branchAddress(branchAddr)
                .build();

        BankInformation result = helper.convertToBankInfoDomain(bankInfo);

        assertEquals("Downtown Branch", result.getBranchName());
        assertEquals(Integer.valueOf(303), result.getBranchCode());
        assertEquals(Integer.valueOf(11111), result.getRoutingNumber());
        assertEquals("50 Finance Blvd", result.getBranchAddress().getAddress1());
    }

    @Test
    public void convertToBankInfoEntity_mapsAllFields() {
        BankInformation info = BankInformation.builder()
                .branchName("Uptown Branch")
                .branchCode(404)
                .routingNumber(22222)
                .branchAddress(AddressDetails.builder()
                        .address1("75 Commerce Way")
                        .city("Gotham")
                        .state("NJ")
                        .zip("07001")
                        .country("US")
                        .build())
                .build();

        BankInfo result = helper.convertToBankInfoEntity(info);

        assertEquals("Uptown Branch", result.getBranchName());
        assertEquals(Integer.valueOf(404), result.getBranchCode());
        assertEquals(Integer.valueOf(22222), result.getRoutingNumber());
        assertEquals("75 Commerce Way", result.getBranchAddress().getAddress1());
    }

    // ===================== Transaction mappings =====================

    @Test
    public void convertToTransactionDomain_mapsAllFields() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(5001L)
                .txType("DEBIT")
                .txAmount(150.0)
                .txDateTime(txDate)
                .build();

        TransactionDetails result = helper.convertToTransactionDomain(transaction);

        assertEquals(Long.valueOf(5001L), result.getAccountNumber());
        assertEquals("DEBIT", result.getTxType());
        assertEquals(150.0, result.getTxAmount(), 0.001);
        assertEquals(txDate, result.getTxDateTime());
    }

    @Test
    public void convertToTransactionEntity_mapsAllFields() {
        Date txDate = new Date();
        TransactionDetails details = TransactionDetails.builder()
                .accountNumber(5002L)
                .txType("CREDIT")
                .txAmount(250.0)
                .txDateTime(txDate)
                .build();

        Transaction result = helper.convertToTransactionEntity(details);

        assertEquals(Long.valueOf(5002L), result.getAccountNumber());
        assertEquals("CREDIT", result.getTxType());
        assertEquals(250.0, result.getTxAmount(), 0.001);
        assertEquals(txDate, result.getTxDateTime());
    }

    // ===================== createTransaction =====================

    @Test
    public void createTransaction_debit() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(300.0);

        Transaction result = helper.createTransaction(transfer, 5001L, "DEBIT");

        assertNotNull(result);
        assertEquals(Long.valueOf(5001L), result.getAccountNumber());
        assertEquals(300.0, result.getTxAmount(), 0.001);
        assertEquals("DEBIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }

    @Test
    public void createTransaction_credit() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(300.0);

        Transaction result = helper.createTransaction(transfer, 5002L, "CREDIT");

        assertNotNull(result);
        assertEquals(Long.valueOf(5002L), result.getAccountNumber());
        assertEquals(300.0, result.getTxAmount(), 0.001);
        assertEquals("CREDIT", result.getTxType());
        assertNotNull(result.getTxDateTime());
    }
}
