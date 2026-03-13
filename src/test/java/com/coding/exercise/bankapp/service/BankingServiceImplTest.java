package com.coding.exercise.bankapp.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.coding.exercise.bankapp.model.CustomerAccountXRef;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

@RunWith(MockitoJUnitRunner.class)
public class BankingServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerAccountXRefRepository custAccXRefRepository;

    @Mock
    private BankingServiceHelper bankingServiceHelper;

    private BankingServiceImpl bankingService;

    private Customer sampleCustomer;
    private CustomerDetails sampleCustomerDetails;
    private Account sampleAccount;
    private AccountInformation sampleAccountInfo;
    private Address sampleAddress;
    private AddressDetails sampleAddressDetails;
    private Contact sampleContact;
    private ContactDetails sampleContactDetails;

    @Before
    public void setUp() {
        bankingService = new BankingServiceImpl(customerRepository);
        ReflectionTestUtils.setField(bankingService, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(bankingService, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
        ReflectionTestUtils.setField(bankingService, "bankingServiceHelper", bankingServiceHelper);

        sampleAddress = Address.builder()
                .id(UUID.randomUUID())
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        sampleContact = Contact.builder()
                .id(UUID.randomUUID())
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        sampleCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(sampleAddress)
                .contactDetails(sampleContact)
                .createDateTime(new Date())
                .build();

        sampleAddressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("US")
                .build();

        sampleContactDetails = ContactDetails.builder()
                .emailId("john@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        sampleCustomerDetails = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(sampleAddressDetails)
                .contactDetails(sampleContactDetails)
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .id(UUID.randomUUID())
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(sampleAddress)
                .build();

        sampleAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(100001L)
                .bankInformation(bankInfo)
                .accountStatus("Active")
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .createDateTime(new Date())
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(sampleAddressDetails)
                .build();

        sampleAccountInfo = AccountInformation.builder()
                .accountNumber(100001L)
                .bankInformation(bankInformation)
                .accountStatus("Active")
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .build();
    }

    // ========== findAll tests ==========

    @Test
    public void testFindAll_ReturnsCustomerList() {
        List<Customer> customers = Arrays.asList(sampleCustomer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(bankingServiceHelper.convertToCustomerDomain(sampleCustomer)).thenReturn(sampleCustomerDetails);

        List<CustomerDetails> result = bankingService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(customerRepository).findAll();
        verify(bankingServiceHelper).convertToCustomerDomain(sampleCustomer);
    }

    @Test
    public void testFindAll_ReturnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        List<CustomerDetails> result = bankingService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository).findAll();
    }

    // ========== addCustomer tests ==========

    @Test
    public void testAddCustomer_Success() {
        when(bankingServiceHelper.convertToCustomerEntity(sampleCustomerDetails)).thenReturn(sampleCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        ResponseEntity<Object> response = bankingService.addCustomer(sampleCustomerDetails);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Customer created successfully.", response.getBody());
        verify(bankingServiceHelper).convertToCustomerEntity(sampleCustomerDetails);
        verify(customerRepository).save(any(Customer.class));
    }

    // ========== findByCustomerNumber tests ==========

    @Test
    public void testFindByCustomerNumber_Found() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));
        when(bankingServiceHelper.convertToCustomerDomain(sampleCustomer)).thenReturn(sampleCustomerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(12345L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(Long.valueOf(12345L), result.getCustomerNumber());
        verify(customerRepository).findByCustomerNumber(12345L);
    }

    @Test
    public void testFindByCustomerNumber_NotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(99999L);

        assertNull(result);
        verify(customerRepository).findByCustomerNumber(99999L);
    }

    // ========== updateCustomer tests ==========

    @Test
    public void testUpdateCustomer_Success() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(sampleCustomerDetails)).thenReturn(sampleCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(sampleCustomerDetails, 12345L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer updated.", response.getBody());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    public void testUpdateCustomer_NotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(sampleCustomerDetails)).thenReturn(sampleCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(sampleCustomerDetails, 99999L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer Number 99999 not found.", response.getBody());
    }

    @Test
    public void testUpdateCustomer_WithNullContact() {
        Customer customerNoContact = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(sampleAddress)
                .contactDetails(null)
                .build();

        Customer managedCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(sampleAddress)
                .contactDetails(null)
                .build();

        CustomerDetails updateDetails = CustomerDetails.builder()
                .firstName("Jane")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(sampleAddressDetails)
                .contactDetails(sampleContactDetails)
                .build();

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(customerNoContact);

        // customerNoContact has null contact, so the contact update branch won't execute
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 12345L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateCustomer_WithNewContactOnNullManagedContact() {
        Customer unmanagedWithContact = Customer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .contactDetails(sampleContact)
                .customerAddress(sampleAddress)
                .build();

        Customer managedNoContact = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .contactDetails(null)
                .customerAddress(sampleAddress)
                .build();

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(managedNoContact));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedWithContact);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedNoContact);

        ResponseEntity<Object> response = bankingService.updateCustomer(sampleCustomerDetails, 12345L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    public void testUpdateCustomer_WithNewAddressOnNullManagedAddress() {
        Customer unmanagedWithAddress = Customer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .contactDetails(sampleContact)
                .customerAddress(sampleAddress)
                .build();

        Customer managedNoAddress = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .contactDetails(sampleContact)
                .customerAddress(null)
                .build();

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(managedNoAddress));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedWithAddress);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedNoAddress);

        ResponseEntity<Object> response = bankingService.updateCustomer(sampleCustomerDetails, 12345L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ========== deleteCustomer tests ==========

    @Test
    public void testDeleteCustomer_Success() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));

        ResponseEntity<Object> response = bankingService.deleteCustomer(12345L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer deleted.", response.getBody());
        verify(customerRepository).delete(sampleCustomer);
    }

    @Test
    public void testDeleteCustomer_NotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.deleteCustomer(99999L);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Customer does not exist.", response.getBody());
    }

    // ========== findByAccountNumber tests ==========

    @Test
    public void testFindByAccountNumber_Found() {
        when(accountRepository.findByAccountNumber(100001L)).thenReturn(Optional.of(sampleAccount));
        when(bankingServiceHelper.convertToAccountDomain(sampleAccount)).thenReturn(sampleAccountInfo);

        ResponseEntity<Object> response = bankingService.findByAccountNumber(100001L);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        verify(accountRepository).findByAccountNumber(100001L);
    }

    @Test
    public void testFindByAccountNumber_NotFound() {
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.findByAccountNumber(99999L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account Number 99999 not found.", response.getBody());
    }

    // ========== addNewAccount tests ==========

    @Test
    public void testAddNewAccount_CustomerExists() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));
        when(bankingServiceHelper.convertToAccountEntity(sampleAccountInfo)).thenReturn(sampleAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);
        when(custAccXRefRepository.save(any(CustomerAccountXRef.class))).thenReturn(CustomerAccountXRef.builder().build());

        ResponseEntity<Object> response = bankingService.addNewAccount(sampleAccountInfo, 12345L);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Account created successfully.", response.getBody());
        verify(accountRepository).save(any(Account.class));
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    public void testAddNewAccount_CustomerNotExists() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.addNewAccount(sampleAccountInfo, 99999L);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ========== transferDetails tests ==========

    @Test
    public void testTransferDetails_Success() {
        Account fromAccount = Account.builder()
                .accountNumber(100001L)
                .accountBalance(5000.0)
                .build();
        Account toAccount = Account.builder()
                .accountNumber(100002L)
                .accountBalance(1000.0)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(100002L);
        transferDetails.setTransferAmount(2000.0);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(100001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(100002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(eq(transferDetails), eq(100001L), eq("DEBIT")))
                .thenReturn(Transaction.builder().accountNumber(100001L).txType("DEBIT").txAmount(2000.0).build());
        when(bankingServiceHelper.createTransaction(eq(transferDetails), eq(100002L), eq("CREDIT")))
                .thenReturn(Transaction.builder().accountNumber(100002L).txType("CREDIT").txAmount(2000.0).build());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3000.0, fromAccount.getAccountBalance(), 0.01);
        assertEquals(3000.0, toAccount.getAccountBalance(), 0.01);
        verify(accountRepository).saveAll(anyList());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    public void testTransferDetails_InsufficientFunds() {
        Account fromAccount = Account.builder()
                .accountNumber(100001L)
                .accountBalance(500.0)
                .build();
        Account toAccount = Account.builder()
                .accountNumber(100002L)
                .accountBalance(1000.0)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(100002L);
        transferDetails.setTransferAmount(2000.0);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(100001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(100002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient Funds.", response.getBody());
        verify(accountRepository, never()).saveAll(anyList());
    }

    @Test
    public void testTransferDetails_CustomerNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(100002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 99999L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer Number 99999 not found.", response.getBody());
    }

    @Test
    public void testTransferDetails_FromAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(99999L);
        transferDetails.setToAccountNumber(100002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("From Account Number 99999 not found.", response.getBody());
    }

    @Test
    public void testTransferDetails_ToAccountNotFound() {
        Account fromAccount = Account.builder()
                .accountNumber(100001L)
                .accountBalance(5000.0)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(99999L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(100001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("To Account Number 99999 not found.", response.getBody());
    }

    // ========== findTransactionsByAccountNumber tests ==========

    @Test
    public void testFindTransactionsByAccountNumber_WithTransactions() {
        Transaction transaction = Transaction.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(1000.0)
                .txDateTime(new Date())
                .build();

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(1000.0)
                .txDateTime(new Date())
                .build();

        when(accountRepository.findByAccountNumber(100001L)).thenReturn(Optional.of(sampleAccount));
        when(transactionRepository.findByAccountNumber(100001L)).thenReturn(Optional.of(Arrays.asList(transaction)));
        when(bankingServiceHelper.convertToTransactionDomain(transaction)).thenReturn(transactionDetails);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(100001L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CREDIT", result.get(0).getTxType());
        verify(transactionRepository).findByAccountNumber(100001L);
    }

    @Test
    public void testFindTransactionsByAccountNumber_AccountNotFound() {
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(99999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindTransactionsByAccountNumber_NoTransactions() {
        when(accountRepository.findByAccountNumber(100001L)).thenReturn(Optional.of(sampleAccount));
        when(transactionRepository.findByAccountNumber(100001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(100001L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
