package com.coding.exercise.bankapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Address;
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
    
    private BankingServiceImpl service;
    
    private Customer mockCustomer;
    private CustomerDetails mockCustomerDetails;
    private Account mockAccount;
    private AccountInformation mockAccountInfo;
    private Transaction mockTransaction;
    
    @BeforeEach
    void setUp() {
        service = new BankingServiceImpl(customerRepository);
        ReflectionTestUtils.setField(service, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(service, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(service, "custAccXRefRepository", custAccXRefRepository);
        ReflectionTestUtils.setField(service, "bankingServiceHelper", bankingServiceHelper);
        
        mockCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .build();
                
        mockCustomerDetails = CustomerDetails.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .build();
                
        mockAccount = Account.builder()
                .accountNumber(98765L)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountStatus("ACTIVE")
                .build();
                
        mockAccountInfo = AccountInformation.builder()
                .accountNumber(98765L)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountStatus("ACTIVE")
                .build();
                
        mockTransaction = Transaction.builder()
                .accountNumber(98765L)
                .txAmount(100.0)
                .txType("CREDIT")
                .txDateTime(new Date())
                .build();
    }
    
    @Test
    @DisplayName("findAll returns list of all customers converted to DTOs")
    void findAllReturnsAllCustomers() {
        List<Customer> customerList = Arrays.asList(mockCustomer);
        when(customerRepository.findAll()).thenReturn(customerList);
        when(bankingServiceHelper.convertToCustomerDomain(mockCustomer)).thenReturn(mockCustomerDetails);
        
        List<CustomerDetails> result = service.findAll();
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockCustomerDetails);
        verify(customerRepository).findAll();
        verify(bankingServiceHelper).convertToCustomerDomain(mockCustomer);
    }
    
    @Test
    @DisplayName("addCustomer saves customer and returns created status")
    void addCustomerSavesCustomerAndReturnsCreated() {
        when(bankingServiceHelper.convertToCustomerEntity(mockCustomerDetails)).thenReturn(mockCustomer);
        
        ResponseEntity<Object> response = service.addCustomer(mockCustomerDetails);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("New Customer created successfully.");
        verify(bankingServiceHelper).convertToCustomerEntity(mockCustomerDetails);
        verify(customerRepository).save(mockCustomer);
    }
    
    @Test
    @DisplayName("findByCustomerNumber returns customer when found")
    void findByCustomerNumberReturnsCustomerWhenFound() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(mockCustomer));
        when(bankingServiceHelper.convertToCustomerDomain(mockCustomer)).thenReturn(mockCustomerDetails);
        
        CustomerDetails result = service.findByCustomerNumber(12345L);
        
        assertThat(result).isEqualTo(mockCustomerDetails);
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(bankingServiceHelper).convertToCustomerDomain(mockCustomer);
    }
    
    @Test
    @DisplayName("findByCustomerNumber returns null when customer not found")
    void findByCustomerNumberReturnsNullWhenNotFound() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.empty());
        
        CustomerDetails result = service.findByCustomerNumber(12345L);
        
        assertThat(result).isNull();
        verify(customerRepository).findByCustomerNumber(12345L);
    }
    
    @Test
    @DisplayName("updateCustomer returns not found when customer doesn't exist")
    void updateCustomerReturnsNotFoundWhenCustomerDoesntExist() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> response = service.updateCustomer(mockCustomerDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().toString()).contains("not found");
        verify(customerRepository).findByCustomerNumber(12345L);
    }
    
    @Test
    @DisplayName("updateCustomer updates existing customer successfully")
    void updateCustomerUpdatesExistingCustomerSuccessfully() {
        Customer existingCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("Old First")
                .lastName("Old Last")
                .build();
                
        Customer updatedCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("New First")
                .lastName("New Last")
                .build();
                
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(mockCustomerDetails)).thenReturn(updatedCustomer);
        
        ResponseEntity<Object> response = service.updateCustomer(mockCustomerDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Success: Customer updated.");
        
        assertThat(existingCustomer.getFirstName()).isEqualTo("New First");
        assertThat(existingCustomer.getLastName()).isEqualTo("New Last");
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(customerRepository).save(existingCustomer);
    }
    
    @Test
    @DisplayName("updateCustomer updates customer contact details when present")
    void updateCustomerUpdatesContactDetailsWhenPresent() {
        Contact existingContact = new Contact();
        existingContact.setEmailId("old@example.com");
        existingContact.setHomePhone("123-456-7890");
        existingContact.setWorkPhone("098-765-4321");
        
        Customer existingCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .contactDetails(existingContact)
                .build();
        
        Contact newContact = new Contact();
        newContact.setEmailId("new@example.com");
        newContact.setHomePhone("555-123-4567");
        newContact.setWorkPhone("555-987-6543");
        
        Customer updatedCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .contactDetails(newContact)
                .build();
        
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(mockCustomerDetails)).thenReturn(updatedCustomer);
        
        ResponseEntity<Object> response = service.updateCustomer(mockCustomerDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existingCustomer.getContactDetails().getEmailId()).isEqualTo("new@example.com");
        assertThat(existingCustomer.getContactDetails().getHomePhone()).isEqualTo("555-123-4567");
        assertThat(existingCustomer.getContactDetails().getWorkPhone()).isEqualTo("555-987-6543");
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(customerRepository).save(existingCustomer);
    }
    
    @Test
    @DisplayName("updateCustomer sets contact details when previously null")
    void updateCustomerSetsContactDetailsWhenPreviouslyNull() {
        Customer existingCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .contactDetails(null)
                .build();
        
        Contact newContact = new Contact();
        newContact.setEmailId("new@example.com");
        newContact.setHomePhone("555-123-4567");
        newContact.setWorkPhone("555-987-6543");
        
        Customer updatedCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .contactDetails(newContact)
                .build();
        
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(mockCustomerDetails)).thenReturn(updatedCustomer);
        
        ResponseEntity<Object> response = service.updateCustomer(mockCustomerDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existingCustomer.getContactDetails()).isEqualTo(newContact);
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(customerRepository).save(existingCustomer);
    }
    
    @Test
    @DisplayName("updateCustomer updates customer address when present")
    void updateCustomerUpdatesAddressWhenPresent() {
        Address existingAddress = new Address();
        existingAddress.setAddress1("123 Old St");
        existingAddress.setAddress2("Apt 1");
        existingAddress.setCity("Old City");
        existingAddress.setState("Old State");
        existingAddress.setZip("12345");
        existingAddress.setCountry("Old Country");
        
        Customer existingCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .customerAddress(existingAddress)
                .build();
        
        Address newAddress = new Address();
        newAddress.setAddress1("456 New St");
        newAddress.setAddress2("Suite 2");
        newAddress.setCity("New City");
        newAddress.setState("New State");
        newAddress.setZip("67890");
        newAddress.setCountry("New Country");
        
        Customer updatedCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .customerAddress(newAddress)
                .build();
        
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(mockCustomerDetails)).thenReturn(updatedCustomer);
        
        ResponseEntity<Object> response = service.updateCustomer(mockCustomerDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existingCustomer.getCustomerAddress().getAddress1()).isEqualTo("456 New St");
        assertThat(existingCustomer.getCustomerAddress().getAddress2()).isEqualTo("Suite 2");
        assertThat(existingCustomer.getCustomerAddress().getCity()).isEqualTo("New City");
        assertThat(existingCustomer.getCustomerAddress().getState()).isEqualTo("New State");
        assertThat(existingCustomer.getCustomerAddress().getZip()).isEqualTo("67890");
        assertThat(existingCustomer.getCustomerAddress().getCountry()).isEqualTo("New Country");
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(customerRepository).save(existingCustomer);
    }
    
    @Test
    @DisplayName("updateCustomer sets address when previously null")
    void updateCustomerSetsAddressWhenPreviouslyNull() {
        Customer existingCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .customerAddress(null)
                .build();
        
        Address newAddress = new Address();
        newAddress.setAddress1("456 New St");
        newAddress.setAddress2("Suite 2");
        newAddress.setCity("New City");
        newAddress.setState("New State");
        newAddress.setZip("67890");
        newAddress.setCountry("New Country");
        
        Customer updatedCustomer = Customer.builder()
                .customerNumber(12345L)
                .firstName("John")
                .lastName("Doe")
                .customerAddress(newAddress)
                .build();
        
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(mockCustomerDetails)).thenReturn(updatedCustomer);
        
        ResponseEntity<Object> response = service.updateCustomer(mockCustomerDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existingCustomer.getCustomerAddress()).isEqualTo(newAddress);
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(customerRepository).save(existingCustomer);
    }
    
    @Test
    @DisplayName("deleteCustomer removes customer when found")
    void deleteCustomerRemovesCustomerWhenFound() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(mockCustomer));
        
        ResponseEntity<Object> response = service.deleteCustomer(12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Success: Customer deleted.");
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(customerRepository).delete(mockCustomer);
    }
    
    @Test
    @DisplayName("deleteCustomer returns error when customer not found")
    void deleteCustomerReturnsErrorWhenNotFound() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> response = service.deleteCustomer(12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Customer does not exist.");
        verify(customerRepository).findByCustomerNumber(12345L);
    }
    
    @Test
    @DisplayName("findByAccountNumber returns account when found")
    void findByAccountNumberReturnsAccountWhenFound() {
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.of(mockAccount));
        when(bankingServiceHelper.convertToAccountDomain(mockAccount)).thenReturn(mockAccountInfo);
        
        ResponseEntity<Object> response = service.findByAccountNumber(98765L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getBody()).isEqualTo(mockAccountInfo);
        verify(accountRepository).findByAccountNumber(98765L);
        verify(bankingServiceHelper).convertToAccountDomain(mockAccount);
    }
    
    @Test
    @DisplayName("findByAccountNumber returns not found when account doesn't exist")
    void findByAccountNumberReturnsNotFoundWhenNotExist() {
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> response = service.findByAccountNumber(98765L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().toString()).contains("not found");
        verify(accountRepository).findByAccountNumber(98765L);
    }
    
    @Test
    @DisplayName("addNewAccount creates account and xref when customer exists")
    void addNewAccountCreatesAccountAndXref() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(mockCustomer));
        when(bankingServiceHelper.convertToAccountEntity(mockAccountInfo)).thenReturn(mockAccount);
        
        ResponseEntity<Object> response = service.addNewAccount(mockAccountInfo, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("New Account created successfully.");
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(accountRepository).save(mockAccount);
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }
    
    @Test
    @DisplayName("addNewAccount returns created status even when customer doesn't exist")
    void addNewAccountWhenCustomerDoesntExist() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> response = service.addNewAccount(mockAccountInfo, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("New Account created successfully.");
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verifyNoMoreInteractions(accountRepository);
        verifyNoMoreInteractions(custAccXRefRepository);
    }
    
    @Test
    @DisplayName("transferDetails fails when customer not found")
    void transferDetailsFailsWhenCustomerNotFound() {
        TransferDetails transferDetails = new TransferDetails(98765L, 54321L, 100.0);
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> response = service.transferDetails(transferDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().toString()).contains("not found");
        verify(customerRepository).findByCustomerNumber(12345L);
    }
    
    @Test
    @DisplayName("transferDetails fails when from account not found")
    void transferDetailsFailsWhenFromAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails(98765L, 54321L, 100.0);
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(mockCustomer));
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> response = service.transferDetails(transferDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().toString()).contains("From Account Number");
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(accountRepository).findByAccountNumber(98765L);
    }
    
    @Test
    @DisplayName("transferDetails fails when to account not found")
    void transferDetailsFailsWhenToAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails(98765L, 54321L, 100.0);
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(mockCustomer));
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.of(mockAccount));
        when(accountRepository.findByAccountNumber(54321L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> response = service.transferDetails(transferDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().toString()).contains("To Account Number");
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(accountRepository).findByAccountNumber(98765L);
        verify(accountRepository).findByAccountNumber(54321L);
    }
    
    @Test
    @DisplayName("transferDetails fails when insufficient funds")
    void transferDetailsFailsWhenInsufficientFunds() {
        TransferDetails transferDetails = new TransferDetails(98765L, 54321L, 2000.0); // More than balance
        Account fromAccount = Account.builder()
                .accountNumber(98765L)
                .accountType("SAVINGS")
                .accountBalance(1000.0) // Only 1000
                .accountStatus("ACTIVE")
                .build();
                
        Account toAccount = Account.builder()
                .accountNumber(54321L)
                .accountType("SAVINGS")
                .accountBalance(500.0)
                .accountStatus("ACTIVE")
                .build();
                
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(mockCustomer));
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(54321L)).thenReturn(Optional.of(toAccount));
        
        ResponseEntity<Object> response = service.transferDetails(transferDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Insufficient Funds.");
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(accountRepository).findByAccountNumber(98765L);
        verify(accountRepository).findByAccountNumber(54321L);
    }
    
    @Test
    @DisplayName("transferDetails successfully transfers funds between accounts")
    void transferDetailsSuccessfullyTransfersFunds() {
        TransferDetails transferDetails = new TransferDetails(98765L, 54321L, 300.0);
        Account fromAccount = Account.builder()
                .accountNumber(98765L)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .accountStatus("ACTIVE")
                .build();
                
        Account toAccount = Account.builder()
                .accountNumber(54321L)
                .accountType("SAVINGS")
                .accountBalance(500.0)
                .accountStatus("ACTIVE")
                .build();
                
        Transaction debitTransaction = Transaction.builder()
                .accountNumber(98765L)
                .txAmount(300.0)
                .txType("DEBIT")
                .txDateTime(new Date())
                .build();
                
        Transaction creditTransaction = Transaction.builder()
                .accountNumber(54321L)
                .txAmount(300.0)
                .txType("CREDIT")
                .txDateTime(new Date())
                .build();
                
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(mockCustomer));
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(54321L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(transferDetails, 98765L, "DEBIT")).thenReturn(debitTransaction);
        when(bankingServiceHelper.createTransaction(transferDetails, 54321L, "CREDIT")).thenReturn(creditTransaction);
        
        ResponseEntity<Object> response = service.transferDetails(transferDetails, 12345L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Success: Amount transferred for Customer Number " + 12345L);
        
        assertThat(fromAccount.getAccountBalance()).isEqualTo(700.0);
        assertThat(toAccount.getAccountBalance()).isEqualTo(800.0);
        
        verify(customerRepository).findByCustomerNumber(12345L);
        verify(accountRepository).findByAccountNumber(98765L);
        verify(accountRepository).findByAccountNumber(54321L);
        verify(accountRepository).saveAll(any());
        verify(bankingServiceHelper).createTransaction(transferDetails, 98765L, "DEBIT");
        verify(bankingServiceHelper).createTransaction(transferDetails, 54321L, "CREDIT");
        verify(transactionRepository).save(debitTransaction);
        verify(transactionRepository).save(creditTransaction);
    }
    
    @Test
    @DisplayName("findTransactionsByAccountNumber returns empty list when account not found")
    void findTransactionsByAccountNumberReturnsEmptyListWhenAccountNotFound() {
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.empty());
        
        List<TransactionDetails> result = service.findTransactionsByAccountNumber(98765L);
        
        assertThat(result).isEmpty();
        verify(accountRepository).findByAccountNumber(98765L);
    }
    
    @Test
    @DisplayName("findTransactionsByAccountNumber returns empty list when no transactions found")
    void findTransactionsByAccountNumberReturnsEmptyListWhenNoTransactions() {
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.findByAccountNumber(98765L)).thenReturn(Optional.empty());
        
        List<TransactionDetails> result = service.findTransactionsByAccountNumber(98765L);
        
        assertThat(result).isEmpty();
        verify(accountRepository).findByAccountNumber(98765L);
        verify(transactionRepository).findByAccountNumber(98765L);
    }
    
    @Test
    @DisplayName("findTransactionsByAccountNumber returns transactions when found")
    void findTransactionsByAccountNumberReturnsTransactions() {
        List<Transaction> transactions = Arrays.asList(mockTransaction);
        TransactionDetails transactionDetails = new TransactionDetails();
        
        when(accountRepository.findByAccountNumber(98765L)).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.findByAccountNumber(98765L)).thenReturn(Optional.of(transactions));
        when(bankingServiceHelper.convertToTransactionDomain(mockTransaction)).thenReturn(transactionDetails);
        
        List<TransactionDetails> result = service.findTransactionsByAccountNumber(98765L);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(transactionDetails);
        verify(accountRepository).findByAccountNumber(98765L);
        verify(transactionRepository).findByAccountNumber(98765L);
        verify(bankingServiceHelper).convertToTransactionDomain(mockTransaction);
    }
}
