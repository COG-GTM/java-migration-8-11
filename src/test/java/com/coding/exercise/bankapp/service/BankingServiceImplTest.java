package com.coding.exercise.bankapp.service;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

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
    
    @InjectMocks
    private BankingServiceImpl bankingService;
    
    @BeforeEach
    void setUp() {
    }
    
    @Test
    void testTransferDetails_ValidTransfer_ShouldSucceed() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(12345L);
        transferDetails.setToAccountNumber(67890L);
        transferDetails.setTransferAmount(100.0);
        
        Account fromAccount = new Account();
        fromAccount.setAccountNumber(12345L);
        fromAccount.setAccountBalance(500.0);
        
        Account toAccount = new Account();
        toAccount.setAccountNumber(67890L);
        toAccount.setAccountBalance(200.0);
        
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        lenient().when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(fromAccount));
        lenient().when(accountRepository.findByAccountNumber(67890L)).thenReturn(Optional.of(toAccount));
        lenient().when(accountRepository.saveAll(any())).thenReturn(null);
        lenient().when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        lenient().when(bankingServiceHelper.createTransaction(any(TransferDetails.class), any(Long.class), any(String.class))).thenReturn(new Transaction());
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);
        
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Success"));
        assertEquals(400.0, fromAccount.getAccountBalance(), 0.01);
        assertEquals(300.0, toAccount.getAccountBalance(), 0.01);
        verify(accountRepository, times(1)).saveAll(any());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }
    
    @Test
    void testTransferDetails_InsufficientFunds_ShouldReturnBadRequest() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(12345L);
        transferDetails.setToAccountNumber(67890L);
        transferDetails.setTransferAmount(600.0);
        
        Account fromAccount = new Account();
        fromAccount.setAccountNumber(12345L);
        fromAccount.setAccountBalance(500.0);
        
        Account toAccount = new Account();
        toAccount.setAccountNumber(67890L);
        toAccount.setAccountBalance(200.0);
        
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        lenient().when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(fromAccount));
        lenient().when(accountRepository.findByAccountNumber(67890L)).thenReturn(Optional.of(toAccount));
        lenient().when(bankingServiceHelper.createTransaction(any(TransferDetails.class), any(Long.class), any(String.class))).thenReturn(new Transaction());
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);
        
        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Insufficient Funds"));
        assertEquals(500.0, fromAccount.getAccountBalance(), 0.01);
        assertEquals(200.0, toAccount.getAccountBalance(), 0.01);
        verify(accountRepository, never()).saveAll(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void testTransferDetails_InvalidAccountNumber_ShouldThrowException() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(99999L);
        transferDetails.setToAccountNumber(67890L);
        transferDetails.setTransferAmount(100.0);
        
        Account toAccount = new Account();
        toAccount.setAccountNumber(67890L);
        toAccount.setAccountBalance(200.0);
        
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        lenient().when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());
        lenient().when(accountRepository.findByAccountNumber(67890L)).thenReturn(Optional.of(toAccount));
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);
        
        assertEquals(404, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("From Account Number 99999 not found"));
        verify(accountRepository, never()).saveAll(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void testAddNewAccount_ValidCustomer_ShouldCreateAccount() {
        AccountInformation accountInfo = new AccountInformation();
        accountInfo.setAccountNumber(12345L);
        accountInfo.setAccountType("SAVINGS");
        accountInfo.setAccountBalance(1000.0);
        
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        
        Account account = new Account();
        account.setAccountNumber(12345L);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        lenient().when(bankingServiceHelper.convertToAccountEntity(accountInfo)).thenReturn(account);
        lenient().when(accountRepository.save(any(Account.class))).thenReturn(account);
        lenient().when(custAccXRefRepository.save(any())).thenReturn(null);
        
        ResponseEntity<Object> result = bankingService.addNewAccount(accountInfo, 1001L);
        
        assertEquals(201, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("New Account created successfully"));
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(custAccXRefRepository, times(1)).save(any());
    }
    
    @Test
    void testAddNewAccount_InvalidCustomer_ShouldThrowException() {
        AccountInformation accountInfo = new AccountInformation();
        accountInfo.setAccountNumber(12345L);
        accountInfo.setAccountType("SAVINGS");
        accountInfo.setAccountBalance(1000.0);
        
        lenient().when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.addNewAccount(accountInfo, 99999L);
        
        assertEquals(201, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("New Account created successfully"));
        verify(accountRepository, never()).save(any(Account.class));
        verify(custAccXRefRepository, never()).save(any());
    }
    
    @Test
    void testFindByAccountNumber_ExistingAccount_ShouldReturnAccount() {
        Account account = new Account();
        account.setAccountNumber(12345L);
        account.setAccountBalance(500.0);
        
        AccountInformation accountInfo = new AccountInformation();
        accountInfo.setAccountNumber(12345L);
        accountInfo.setAccountBalance(500.0);
        
        lenient().when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(account));
        lenient().when(bankingServiceHelper.convertToAccountDomain(account)).thenReturn(accountInfo);
        
        ResponseEntity<Object> result = bankingService.findByAccountNumber(12345L);
        
        assertEquals(302, result.getStatusCodeValue());
        verify(accountRepository, times(1)).findByAccountNumber(12345L);
        verify(bankingServiceHelper, times(1)).convertToAccountDomain(account);
    }
    
    @Test
    void testFindByAccountNumber_NonExistentAccount_ShouldThrowException() {
        lenient().when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.findByAccountNumber(99999L);
        
        assertEquals(404, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Account Number 99999 not found"));
        verify(accountRepository, times(1)).findByAccountNumber(99999L);
        verify(bankingServiceHelper, never()).convertToAccountDomain(any());
    }
    
    @Test
    void testUpdateCustomer_ValidUpdate_ShouldSucceed() {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Doe");
        customerDetails.setStatus("ACTIVE");
        
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerNumber(1001L);
        existingCustomer.setFirstName("Jane");
        existingCustomer.setLastName("Smith");
        
        Customer updatedCustomer = new Customer();
        updatedCustomer.setFirstName("John");
        updatedCustomer.setLastName("Doe");
        updatedCustomer.setStatus("ACTIVE");
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(existingCustomer));
        lenient().when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(updatedCustomer);
        lenient().when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        
        ResponseEntity<Object> result = bankingService.updateCustomer(customerDetails, 1001L);
        
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Success: Customer updated"));
        verify(customerRepository, times(1)).findByCustomerNumber(1001L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    void testUpdateCustomer_InvalidCustomer_ShouldThrowException() {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Doe");
        customerDetails.setStatus("ACTIVE");
        
        lenient().when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.updateCustomer(customerDetails, 99999L);
        
        assertEquals(404, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Customer Number 99999 not found"));
        verify(customerRepository, times(1)).findByCustomerNumber(99999L);
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void testDeleteCustomer_ExistingCustomer_ShouldDeleteSuccessfully() {
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        
        ResponseEntity<Object> result = bankingService.deleteCustomer(1001L);
        
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Success: Customer deleted"));
        verify(customerRepository, times(1)).findByCustomerNumber(1001L);
        verify(customerRepository, times(1)).delete(customer);
    }
    
    @Test
    void testDeleteCustomer_NonExistentCustomer_ShouldThrowException() {
        lenient().when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.deleteCustomer(99999L);
        
        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Customer does not exist"));
        verify(customerRepository, times(1)).findByCustomerNumber(99999L);
        verify(customerRepository, never()).delete(any());
    }
    
    @Test
    void testTransferDetails_InvalidCustomer_ShouldReturnNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(12345L);
        transferDetails.setToAccountNumber(67890L);
        transferDetails.setTransferAmount(100.0);
        
        lenient().when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 99999L);
        
        assertEquals(404, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("Customer Number 99999 not found"));
        verify(accountRepository, never()).findByAccountNumber(any());
        verify(accountRepository, never()).saveAll(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void testTransferDetails_InvalidToAccount_ShouldReturnNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(12345L);
        transferDetails.setToAccountNumber(99999L);
        transferDetails.setTransferAmount(100.0);
        
        Account fromAccount = new Account();
        fromAccount.setAccountNumber(12345L);
        fromAccount.setAccountBalance(500.0);
        
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        lenient().when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(fromAccount));
        lenient().when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);
        
        assertEquals(404, result.getStatusCodeValue());
        assertTrue(result.getBody().toString().contains("To Account Number 99999 not found"));
        verify(accountRepository, never()).saveAll(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void testFindAll_ShouldReturnAllCustomers() {
        Customer customer1 = new Customer();
        customer1.setCustomerNumber(1001L);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        
        Customer customer2 = new Customer();
        customer2.setCustomerNumber(1002L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        
        CustomerDetails details1 = new CustomerDetails();
        details1.setCustomerNumber(1001L);
        details1.setFirstName("John");
        details1.setLastName("Doe");
        
        CustomerDetails details2 = new CustomerDetails();
        details2.setCustomerNumber(1002L);
        details2.setFirstName("Jane");
        details2.setLastName("Smith");
        
        lenient().when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        lenient().when(bankingServiceHelper.convertToCustomerDomain(customer1)).thenReturn(details1);
        lenient().when(bankingServiceHelper.convertToCustomerDomain(customer2)).thenReturn(details2);
        
        List<CustomerDetails> result = bankingService.findAll();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(1001L), result.get(0).getCustomerNumber());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals(Long.valueOf(1002L), result.get(1).getCustomerNumber());
        assertEquals("Jane", result.get(1).getFirstName());
    }
    
    @Test
    void testAddCustomer_ShouldCreateNewCustomer() {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerNumber(1001L);
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Doe");
        
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        lenient().when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customer);
        lenient().when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        ResponseEntity<Object> response = bankingService.addCustomer(customerDetails);
        
        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("New Customer created successfully"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    void testFindByCustomerNumber_CustomerExists_ShouldReturnCustomerDetails() {
        Customer customer = new Customer();
        customer.setCustomerNumber(1001L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        CustomerDetails expectedDetails = new CustomerDetails();
        expectedDetails.setCustomerNumber(1001L);
        expectedDetails.setFirstName("John");
        expectedDetails.setLastName("Doe");
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        lenient().when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(expectedDetails);
        
        CustomerDetails result = bankingService.findByCustomerNumber(1001L);
        
        assertNotNull(result);
        assertEquals(Long.valueOf(1001L), result.getCustomerNumber());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }
    
    @Test
    void testFindByCustomerNumber_CustomerNotExists_ShouldReturnNull() {
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.empty());
        
        CustomerDetails result = bankingService.findByCustomerNumber(1001L);
        
        assertNull(result);
    }
    
    @Test
    void testFindTransactionsByAccountNumber_AccountExists_ShouldReturnTransactions() {
        Account account = new Account();
        account.setAccountNumber(12345L);
        account.setAccountBalance(1000.0);
        
        Transaction transaction1 = new Transaction();
        transaction1.setAccountNumber(12345L);
        transaction1.setTxAmount(100.0);
        transaction1.setTxType("DEBIT");
        
        Transaction transaction2 = new Transaction();
        transaction2.setAccountNumber(12345L);
        transaction2.setTxAmount(200.0);
        transaction2.setTxType("CREDIT");
        
        TransactionDetails details1 = new TransactionDetails();
        details1.setAccountNumber(12345L);
        details1.setTxAmount(100.0);
        details1.setTxType("DEBIT");
        
        TransactionDetails details2 = new TransactionDetails();
        details2.setAccountNumber(12345L);
        details2.setTxAmount(200.0);
        details2.setTxType("CREDIT");
        
        lenient().when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(account));
        lenient().when(transactionRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(Arrays.asList(transaction1, transaction2)));
        lenient().when(bankingServiceHelper.convertToTransactionDomain(transaction1)).thenReturn(details1);
        lenient().when(bankingServiceHelper.convertToTransactionDomain(transaction2)).thenReturn(details2);
        
        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(12345L);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(12345L), result.get(0).getAccountNumber());
        assertEquals(Double.valueOf(100.0), result.get(0).getTxAmount());
        assertEquals("DEBIT", result.get(0).getTxType());
        assertEquals(Long.valueOf(12345L), result.get(1).getAccountNumber());
        assertEquals(Double.valueOf(200.0), result.get(1).getTxAmount());
        assertEquals("CREDIT", result.get(1).getTxType());
    }
    
    @Test
    void testFindTransactionsByAccountNumber_AccountNotExists_ShouldReturnEmptyList() {
        lenient().when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());
        
        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(12345L);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testFindTransactionsByAccountNumber_NoTransactions_ShouldReturnEmptyList() {
        Account account = new Account();
        account.setAccountNumber(12345L);
        account.setAccountBalance(1000.0);
        
        lenient().when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(account));
        lenient().when(transactionRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());
        
        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(12345L);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testUpdateCustomer_WithContactDetails_ShouldUpdateContact() {
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerNumber(1001L);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");
        
        Contact existingContact = new Contact();
        existingContact.setEmailId("old@example.com");
        existingContact.setHomePhone("111-111-1111");
        existingContact.setWorkPhone("222-222-2222");
        existingCustomer.setContactDetails(existingContact);
        
        CustomerDetails updateDetails = new CustomerDetails();
        updateDetails.setCustomerNumber(1001L);
        updateDetails.setFirstName("John");
        updateDetails.setLastName("Doe");
        updateDetails.setStatus("ACTIVE");
        
        ContactDetails newContactDetails = new ContactDetails();
        newContactDetails.setEmailId("new@example.com");
        newContactDetails.setHomePhone("333-333-3333");
        newContactDetails.setWorkPhone("444-444-4444");
        updateDetails.setContactDetails(newContactDetails);
        
        Customer unmanagedCustomer = new Customer();
        unmanagedCustomer.setCustomerNumber(1001L);
        unmanagedCustomer.setFirstName("John");
        unmanagedCustomer.setLastName("Doe");
        unmanagedCustomer.setStatus("ACTIVE");
        
        Contact unmanagedContact = new Contact();
        unmanagedContact.setEmailId("new@example.com");
        unmanagedContact.setHomePhone("333-333-3333");
        unmanagedContact.setWorkPhone("444-444-4444");
        unmanagedCustomer.setContactDetails(unmanagedContact);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(existingCustomer));
        lenient().when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);
        lenient().when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        
        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Success: Customer updated"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    void testUpdateCustomer_WithNullContactDetails_ShouldSetNewContact() {
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerNumber(1001L);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");
        existingCustomer.setContactDetails(null);
        
        CustomerDetails updateDetails = new CustomerDetails();
        updateDetails.setCustomerNumber(1001L);
        updateDetails.setFirstName("John");
        updateDetails.setLastName("Doe");
        updateDetails.setStatus("ACTIVE");
        
        ContactDetails newContactDetails = new ContactDetails();
        newContactDetails.setEmailId("new@example.com");
        newContactDetails.setHomePhone("333-333-3333");
        newContactDetails.setWorkPhone("444-444-4444");
        updateDetails.setContactDetails(newContactDetails);
        
        Customer unmanagedCustomer = new Customer();
        unmanagedCustomer.setCustomerNumber(1001L);
        unmanagedCustomer.setFirstName("John");
        unmanagedCustomer.setLastName("Doe");
        unmanagedCustomer.setStatus("ACTIVE");
        
        Contact unmanagedContact = new Contact();
        unmanagedContact.setEmailId("new@example.com");
        unmanagedContact.setHomePhone("333-333-3333");
        unmanagedContact.setWorkPhone("444-444-4444");
        unmanagedCustomer.setContactDetails(unmanagedContact);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(existingCustomer));
        lenient().when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);
        lenient().when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        
        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Success: Customer updated"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    void testUpdateCustomer_WithAddressDetails_ShouldUpdateAddress() {
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerNumber(1001L);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");
        
        Address existingAddress = new Address();
        existingAddress.setAddress1("123 Old St");
        existingAddress.setCity("Old City");
        existingAddress.setState("OS");
        existingAddress.setZip("12345");
        existingAddress.setCountry("USA");
        existingCustomer.setCustomerAddress(existingAddress);
        
        CustomerDetails updateDetails = new CustomerDetails();
        updateDetails.setCustomerNumber(1001L);
        updateDetails.setFirstName("John");
        updateDetails.setLastName("Doe");
        updateDetails.setStatus("ACTIVE");
        
        AddressDetails newAddressDetails = new AddressDetails();
        newAddressDetails.setAddress1("456 New Ave");
        newAddressDetails.setCity("New City");
        newAddressDetails.setState("NS");
        newAddressDetails.setZip("67890");
        newAddressDetails.setCountry("USA");
        updateDetails.setCustomerAddress(newAddressDetails);
        
        Customer unmanagedCustomer = new Customer();
        unmanagedCustomer.setCustomerNumber(1001L);
        unmanagedCustomer.setFirstName("John");
        unmanagedCustomer.setLastName("Doe");
        unmanagedCustomer.setStatus("ACTIVE");
        
        Address unmanagedAddress = new Address();
        unmanagedAddress.setAddress1("456 New Ave");
        unmanagedAddress.setCity("New City");
        unmanagedAddress.setState("NS");
        unmanagedAddress.setZip("67890");
        unmanagedAddress.setCountry("USA");
        unmanagedCustomer.setCustomerAddress(unmanagedAddress);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(existingCustomer));
        lenient().when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);
        lenient().when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        
        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Success: Customer updated"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    void testUpdateCustomer_WithNullAddressDetails_ShouldSetNewAddress() {
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerNumber(1001L);
        existingCustomer.setFirstName("John");
        existingCustomer.setLastName("Doe");
        existingCustomer.setCustomerAddress(null);
        
        CustomerDetails updateDetails = new CustomerDetails();
        updateDetails.setCustomerNumber(1001L);
        updateDetails.setFirstName("John");
        updateDetails.setLastName("Doe");
        updateDetails.setStatus("ACTIVE");
        
        AddressDetails newAddressDetails = new AddressDetails();
        newAddressDetails.setAddress1("456 New Ave");
        newAddressDetails.setCity("New City");
        newAddressDetails.setState("NS");
        newAddressDetails.setZip("67890");
        newAddressDetails.setCountry("USA");
        updateDetails.setCustomerAddress(newAddressDetails);
        
        Customer unmanagedCustomer = new Customer();
        unmanagedCustomer.setCustomerNumber(1001L);
        unmanagedCustomer.setFirstName("John");
        unmanagedCustomer.setLastName("Doe");
        unmanagedCustomer.setStatus("ACTIVE");
        
        Address unmanagedAddress = new Address();
        unmanagedAddress.setAddress1("456 New Ave");
        unmanagedAddress.setCity("New City");
        unmanagedAddress.setState("NS");
        unmanagedAddress.setZip("67890");
        unmanagedAddress.setCountry("USA");
        unmanagedCustomer.setCustomerAddress(unmanagedAddress);
        
        lenient().when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(existingCustomer));
        lenient().when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);
        lenient().when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        
        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Success: Customer updated"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}
