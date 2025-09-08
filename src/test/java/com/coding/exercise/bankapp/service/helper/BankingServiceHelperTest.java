package com.coding.exercise.bankapp.service.helper;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BankingServiceHelperTest {

    private BankingServiceHelper bankingServiceHelper;
    
    @BeforeEach
    void setUp() {
        bankingServiceHelper = new BankingServiceHelper();
    }

    @Test
    void testConvertToCustomerEntity_ValidCustomerDetails_ShouldReturnCustomer() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("john.doe@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();
        
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4B")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();
        
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("ACTIVE")
                .contactDetails(contactDetails)
                .customerAddress(addressDetails)
                .build();
        
        Customer customer = bankingServiceHelper.convertToCustomerEntity(customerDetails);
        
        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals(Long.valueOf(1001L), customer.getCustomerNumber());
        assertEquals("ACTIVE", customer.getStatus());
        assertNotNull(customer.getContactDetails());
        assertNotNull(customer.getCustomerAddress());
    }
    
    @Test
    void testConvertToCustomerDomain_ValidCustomer_ShouldReturnCustomerDetails() {
        Contact contact = Contact.builder()
                .emailId("jane.doe@example.com")
                .homePhone("555-9876")
                .workPhone("555-4321")
                .build();
        
        Address address = Address.builder()
                .address1("456 Oak Ave")
                .address2("Suite 200")
                .city("Los Angeles")
                .state("CA")
                .zip("90210")
                .country("USA")
                .build();
        
        Customer customer = Customer.builder()
                .firstName("Jane")
                .middleName("A")
                .lastName("Doe")
                .customerNumber(1002L)
                .status("ACTIVE")
                .contactDetails(contact)
                .customerAddress(address)
                .build();
        
        CustomerDetails customerDetails = bankingServiceHelper.convertToCustomerDomain(customer);
        
        assertNotNull(customerDetails);
        assertEquals("Jane", customerDetails.getFirstName());
        assertEquals("A", customerDetails.getMiddleName());
        assertEquals("Doe", customerDetails.getLastName());
        assertEquals(Long.valueOf(1002L), customerDetails.getCustomerNumber());
        assertEquals("ACTIVE", customerDetails.getStatus());
        assertNotNull(customerDetails.getContactDetails());
        assertNotNull(customerDetails.getCustomerAddress());
    }
    
    @Test
    void testConvertToAccountEntity_ValidAccountInformation_ShouldReturnAccount() {
        AddressDetails branchAddress = AddressDetails.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();
        
        BankInformation bankInfo = BankInformation.builder()
                .branchCode(1001)
                .branchName("Chicago Main Branch")
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();
        
        AccountInformation accountInfo = AccountInformation.builder()
                .accountNumber(12345L)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountStatus("ACTIVE")
                .bankInformation(bankInfo)
                .build();
        
        Account account = bankingServiceHelper.convertToAccountEntity(accountInfo);
        
        assertNotNull(account);
        assertEquals(Long.valueOf(12345L), account.getAccountNumber());
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(Double.valueOf(1000.0), account.getAccountBalance());
        assertEquals("ACTIVE", account.getAccountStatus());
        assertNotNull(account.getBankInformation());
    }
    
    @Test
    void testConvertToAccountDomain_ValidAccount_ShouldReturnAccountInformation() {
        Address branchAddress = Address.builder()
                .address1("321 Finance Blvd")
                .city("Miami")
                .state("FL")
                .zip("33101")
                .country("USA")
                .build();
        
        BankInfo bankInfo = BankInfo.builder()
                .branchCode(1002)
                .branchName("Miami Downtown Branch")
                .routingNumber(987654321)
                .branchAddress(branchAddress)
                .build();
        
        Account account = Account.builder()
                .accountNumber(67890L)
                .accountType("CHECKING")
                .accountBalance(2500.0)
                .accountStatus("ACTIVE")
                .bankInformation(bankInfo)
                .build();
        
        AccountInformation accountInfo = bankingServiceHelper.convertToAccountDomain(account);
        
        assertNotNull(accountInfo);
        assertEquals(Long.valueOf(67890L), accountInfo.getAccountNumber());
        assertEquals("CHECKING", accountInfo.getAccountType());
        assertEquals(Double.valueOf(2500.0), accountInfo.getAccountBalance());
        assertEquals("ACTIVE", accountInfo.getAccountStatus());
        assertNotNull(accountInfo.getBankInformation());
    }
    
    @Test
    void testConvertToTransactionDomain_ValidTransaction_ShouldReturnTransactionDetails() {
        Date txDate = new Date();
        Transaction transaction = Transaction.builder()
                .accountNumber(12345L)
                .txAmount(100.0)
                .txType("DEBIT")
                .txDateTime(txDate)
                .build();
        
        TransactionDetails transactionDetails = bankingServiceHelper.convertToTransactionDomain(transaction);
        
        assertNotNull(transactionDetails);
        assertEquals(Long.valueOf(12345L), transactionDetails.getAccountNumber());
        assertEquals(Double.valueOf(100.0), transactionDetails.getTxAmount());
        assertEquals("DEBIT", transactionDetails.getTxType());
        assertEquals(txDate, transactionDetails.getTxDateTime());
    }
    
    @Test
    void testConvertToTransactionEntity_ValidTransactionDetails_ShouldReturnTransaction() {
        Date txDate = new Date();
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(67890L)
                .txAmount(250.0)
                .txType("CREDIT")
                .txDateTime(txDate)
                .build();
        
        Transaction transaction = bankingServiceHelper.convertToTransactionEntity(transactionDetails);
        
        assertNotNull(transaction);
        assertEquals(Long.valueOf(67890L), transaction.getAccountNumber());
        assertEquals(Double.valueOf(250.0), transaction.getTxAmount());
        assertEquals("CREDIT", transaction.getTxType());
        assertEquals(txDate, transaction.getTxDateTime());
    }
    
    @Test
    void testCreateTransaction_ValidTransferDetails_ShouldReturnTransaction() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(12345L);
        transferDetails.setToAccountNumber(67890L);
        transferDetails.setTransferAmount(500.0);
        
        Transaction transaction = bankingServiceHelper.createTransaction(transferDetails, 12345L, "DEBIT");
        
        assertNotNull(transaction);
        assertEquals(Long.valueOf(12345L), transaction.getAccountNumber());
        assertEquals(Double.valueOf(500.0), transaction.getTxAmount());
        assertEquals("DEBIT", transaction.getTxType());
        assertNotNull(transaction.getTxDateTime());
    }
    
    @Test
    void testConvertToAddressEntity_ValidAddressDetails_ShouldReturnAddress() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("555 Test Ave")
                .address2("Unit 10")
                .city("Seattle")
                .state("WA")
                .zip("98101")
                .country("USA")
                .build();
        
        Address address = bankingServiceHelper.convertToAddressEntity(addressDetails);
        
        assertNotNull(address);
        assertEquals("555 Test Ave", address.getAddress1());
        assertEquals("Unit 10", address.getAddress2());
        assertEquals("Seattle", address.getCity());
        assertEquals("WA", address.getState());
        assertEquals("98101", address.getZip());
        assertEquals("USA", address.getCountry());
    }
    
    @Test
    void testConvertToAddressDomain_ValidAddress_ShouldReturnAddressDetails() {
        Address address = Address.builder()
                .address1("777 Demo St")
                .address2("Floor 5")
                .city("Boston")
                .state("MA")
                .zip("02101")
                .country("USA")
                .build();
        
        AddressDetails addressDetails = bankingServiceHelper.convertToAddressDomain(address);
        
        assertNotNull(addressDetails);
        assertEquals("777 Demo St", addressDetails.getAddress1());
        assertEquals("Floor 5", addressDetails.getAddress2());
        assertEquals("Boston", addressDetails.getCity());
        assertEquals("MA", addressDetails.getState());
        assertEquals("02101", addressDetails.getZip());
        assertEquals("USA", addressDetails.getCountry());
    }
    
    @Test
    void testConvertToContactEntity_ValidContactDetails_ShouldReturnContact() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("555-0001")
                .workPhone("555-0002")
                .build();
        
        Contact contact = bankingServiceHelper.convertToContactEntity(contactDetails);
        
        assertNotNull(contact);
        assertEquals("test@example.com", contact.getEmailId());
        assertEquals("555-0001", contact.getHomePhone());
        assertEquals("555-0002", contact.getWorkPhone());
    }
    
    @Test
    void testConvertToContactDomain_ValidContact_ShouldReturnContactDetails() {
        Contact contact = Contact.builder()
                .emailId("demo@example.com")
                .homePhone("555-0003")
                .workPhone("555-0004")
                .build();
        
        ContactDetails contactDetails = bankingServiceHelper.convertToContactDomain(contact);
        
        assertNotNull(contactDetails);
        assertEquals("demo@example.com", contactDetails.getEmailId());
        assertEquals("555-0003", contactDetails.getHomePhone());
        assertEquals("555-0004", contactDetails.getWorkPhone());
    }
    
    @Test
    void testConvertToBankInfoEntity_ValidBankInformation_ShouldReturnBankInfo() {
        AddressDetails branchAddress = AddressDetails.builder()
                .address1("999 Bank Plaza")
                .city("Denver")
                .state("CO")
                .zip("80201")
                .country("USA")
                .build();
        
        BankInformation bankInformation = BankInformation.builder()
                .branchCode(1003)
                .branchName("Denver Central Branch")
                .routingNumber(111222333)
                .branchAddress(branchAddress)
                .build();
        
        BankInfo bankInfo = bankingServiceHelper.convertToBankInfoEntity(bankInformation);
        
        assertNotNull(bankInfo);
        assertEquals(Integer.valueOf(1003), bankInfo.getBranchCode());
        assertEquals("Denver Central Branch", bankInfo.getBranchName());
        assertEquals(Integer.valueOf(111222333), bankInfo.getRoutingNumber());
        assertNotNull(bankInfo.getBranchAddress());
    }
    
    @Test
    void testConvertToBankInfoDomain_ValidBankInfo_ShouldReturnBankInformation() {
        Address branchAddress = Address.builder()
                .address1("888 Finance Center")
                .city("Phoenix")
                .state("AZ")
                .zip("85001")
                .country("USA")
                .build();
        
        BankInfo bankInfo = BankInfo.builder()
                .branchCode(1004)
                .branchName("Phoenix Main Branch")
                .routingNumber(444555666)
                .branchAddress(branchAddress)
                .build();
        
        BankInformation bankInformation = bankingServiceHelper.convertToBankInfoDomain(bankInfo);
        
        assertNotNull(bankInformation);
        assertEquals(Integer.valueOf(1004), bankInformation.getBranchCode());
        assertEquals("Phoenix Main Branch", bankInformation.getBranchName());
        assertEquals(Integer.valueOf(444555666), bankInformation.getRoutingNumber());
        assertNotNull(bankInformation.getBranchAddress());
    }
}
