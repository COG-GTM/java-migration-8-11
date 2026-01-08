package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class BankingServiceImplTest {

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

    private Customer testCustomer;
    private CustomerDetails testCustomerDetails;
    private Account testAccount;
    private AccountInformation testAccountInfo;
    private Transaction testTransaction;
    private TransactionDetails testTransactionDetails;

    @BeforeEach
    void setUp() {
        bankingService = new BankingServiceImpl(customerRepository);
        ReflectionTestUtils.setField(bankingService, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(bankingService, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
        ReflectionTestUtils.setField(bankingService, "bankingServiceHelper", bankingServiceHelper);

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

        testCustomer = Customer.builder()
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

        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        testCustomerDetails = CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(addressDetails)
                .contactDetails(contactDetails)
                .build();

        Address branchAddress = Address.builder()
                .address1("789 Bank St")
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

        testAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
                .updateDateTime(new Date())
                .build();

        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddressDetails)
                .build();

        testAccountInfo = AccountInformation.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInformation)
                .build();

        testTransaction = Transaction.builder()
                .id(UUID.randomUUID())
                .accountNumber(1000001L)
                .txDateTime(new Date())
                .txType("DEBIT")
                .txAmount(100.00)
                .build();

        testTransactionDetails = TransactionDetails.builder()
                .accountNumber(1000001L)
                .txDateTime(new Date())
                .txType("DEBIT")
                .txAmount(100.00)
                .build();
    }

    @Test
    void testFindAll() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(bankingServiceHelper.convertToCustomerDomain(any(Customer.class))).thenReturn(testCustomerDetails);

        List<CustomerDetails> result = bankingService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        List<CustomerDetails> result = bankingService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddCustomer() {
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Object> result = bankingService.addCustomer(testCustomerDetails);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("New Customer created successfully.", result.getBody());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testFindByCustomerNumber_Found() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerDomain(any(Customer.class))).thenReturn(testCustomerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(12345L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(12345L, result.getCustomerNumber());
    }

    @Test
    void testFindByCustomerNumber_NotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(99999L);

        assertNull(result);
    }

    @Test
    void testUpdateCustomer_Success() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(testCustomerDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success: Customer updated.", result.getBody());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(testCustomerDetails, 99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("not found"));
    }

    @Test
    void testUpdateCustomer_WithNullContactAndAddress() {
        Customer customerWithNullContact = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(null)
                .contactDetails(null)
                .build();

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(customerWithNullContact));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerWithNullContact);

        ResponseEntity<Object> result = bankingService.updateCustomer(testCustomerDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testDeleteCustomer_Success() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        doNothing().when(customerRepository).delete(any(Customer.class));

        ResponseEntity<Object> result = bankingService.deleteCustomer(12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success: Customer deleted.", result.getBody());
        verify(customerRepository, times(1)).delete(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.deleteCustomer(99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Customer does not exist.", result.getBody());
    }

    @Test
    void testFindByAccountNumber_Found() {
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(testAccount));
        when(bankingServiceHelper.convertToAccountDomain(any(Account.class))).thenReturn(testAccountInfo);

        ResponseEntity<Object> result = bankingService.findByAccountNumber(1000001L);

        assertNotNull(result);
        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    void testFindByAccountNumber_NotFound() {
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.findByAccountNumber(99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("not found"));
    }

    @Test
    void testAddNewAccount_Success() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToAccountEntity(any(AccountInformation.class))).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(custAccXRefRepository.save(any(CustomerAccountXRef.class))).thenReturn(null);

        ResponseEntity<Object> result = bankingService.addNewAccount(testAccountInfo, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("New Account created successfully.", result.getBody());
    }

    @Test
    void testAddNewAccount_CustomerNotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.addNewAccount(testAccountInfo, 99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testTransferDetails_Success() {
        Account fromAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(1001L)
                .accountBalance(1000.00)
                .build();

        Account toAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(1002L)
                .accountBalance(500.00)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(100.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(1002L)).thenReturn(Optional.of(toAccount));
        when(accountRepository.saveAll(anyList())).thenReturn(Arrays.asList(fromAccount, toAccount));
        when(bankingServiceHelper.createTransaction(any(TransferDetails.class), anyLong(), anyString()))
                .thenReturn(testTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Success"));
    }

    @Test
    void testTransferDetails_CustomerNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(100.00);

        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Customer Number"));
    }

    @Test
    void testTransferDetails_FromAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(9999L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(100.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("From Account Number"));
    }

    @Test
    void testTransferDetails_ToAccountNotFound() {
        Account fromAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(1001L)
                .accountBalance(1000.00)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(9999L);
        transferDetails.setTransferAmount(100.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("To Account Number"));
    }

    @Test
    void testTransferDetails_InsufficientFunds() {
        Account fromAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(1001L)
                .accountBalance(50.00)
                .build();

        Account toAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(1002L)
                .accountBalance(500.00)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(100.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(1002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Insufficient Funds.", result.getBody());
    }

    @Test
    void testFindTransactionsByAccountNumber_Found() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(transactions));
        when(bankingServiceHelper.convertToTransactionDomain(any(Transaction.class))).thenReturn(testTransactionDetails);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1000001L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DEBIT", result.get(0).getTxType());
    }

    @Test
    void testFindTransactionsByAccountNumber_AccountNotFound() {
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(99999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindTransactionsByAccountNumber_NoTransactions() {
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(1000001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1000001L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
