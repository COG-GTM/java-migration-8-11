package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.Transaction;
import com.coding.exercise.bankapp.repository.AccountRepository;
import com.coding.exercise.bankapp.repository.CustomerRepository;
import com.coding.exercise.bankapp.repository.TransactionRepository;
import com.coding.exercise.bankapp.repository.CustomerAccountXRefRepository;
import com.coding.exercise.bankapp.service.helper.BankingServiceHelper;

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
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        bankingService = new BankingServiceImpl(customerRepository);
        
        try {
            java.lang.reflect.Field accountRepoField = BankingServiceImpl.class.getDeclaredField("accountRepository");
            accountRepoField.setAccessible(true);
            accountRepoField.set(bankingService, accountRepository);
            
            java.lang.reflect.Field transactionRepoField = BankingServiceImpl.class.getDeclaredField("transactionRepository");
            transactionRepoField.setAccessible(true);
            transactionRepoField.set(bankingService, transactionRepository);
            
            java.lang.reflect.Field custAccXRefRepoField = BankingServiceImpl.class.getDeclaredField("custAccXRefRepository");
            custAccXRefRepoField.setAccessible(true);
            custAccXRefRepoField.set(bankingService, custAccXRefRepository);
            
            java.lang.reflect.Field helperField = BankingServiceImpl.class.getDeclaredField("bankingServiceHelper");
            helperField.setAccessible(true);
            helperField.set(bankingService, bankingServiceHelper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }
    }
    
    @Test
    void findAll_ShouldReturnEmptyList_WhenNoCustomersExist() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());
        
        List<CustomerDetails> result = bankingService.findAll();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository).findAll();
    }
    
    @Test
    void findByCustomerNumber_ShouldReturnNull_WhenCustomerNotFound() {
        Long customerNumber = 123L;
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.empty());
        
        CustomerDetails result = bankingService.findByCustomerNumber(customerNumber);
        
        assertNull(result);
        verify(customerRepository).findByCustomerNumber(customerNumber);
    }
    
    @Test
    void transferDetails_ShouldReturnSuccess_WhenValidTransfer() {
        Long customerNumber = 123L;
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100L);
        transferDetails.setToAccountNumber(200L);
        transferDetails.setTransferAmount(500.0);
        
        Customer customer = new Customer();
        Account fromAccount = new Account();
        fromAccount.setAccountNumber(100L);
        fromAccount.setAccountBalance(1000.0);
        Account toAccount = new Account();
        toAccount.setAccountNumber(200L);
        toAccount.setAccountBalance(500.0);
        
        Transaction fromTransaction = new Transaction();
        Transaction toTransaction = new Transaction();
        
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(200L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(transferDetails, 100L, "DEBIT")).thenReturn(fromTransaction);
        when(bankingServiceHelper.createTransaction(transferDetails, 200L, "CREDIT")).thenReturn(toTransaction);
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, customerNumber);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success: Amount transferred for Customer Number " + customerNumber, result.getBody());
        assertEquals(500.0, fromAccount.getAccountBalance(), 0.01);
        assertEquals(1000.0, toAccount.getAccountBalance(), 0.01);
        verify(accountRepository).saveAll(any());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }
    
    @Test
    void transferDetails_ShouldReturnBadRequest_WhenInsufficientFunds() {
        Long customerNumber = 123L;
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100L);
        transferDetails.setToAccountNumber(200L);
        transferDetails.setTransferAmount(1000.0);
        
        Customer customer = new Customer();
        Account fromAccount = new Account();
        fromAccount.setAccountBalance(500.0);
        Account toAccount = new Account();
        
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(200L)).thenReturn(Optional.of(toAccount));
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, customerNumber);
        
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Insufficient Funds.", result.getBody());
        verify(accountRepository, never()).saveAll(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void transferDetails_ShouldReturnNotFound_WhenCustomerNotFound() {
        Long customerNumber = 999L;
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100L);
        transferDetails.setToAccountNumber(200L);
        transferDetails.setTransferAmount(500.0);
        
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, customerNumber);
        
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Customer Number " + customerNumber + " not found.", result.getBody());
        verify(accountRepository, never()).findByAccountNumber(any());
    }
    
    @Test
    void transferDetails_ShouldReturnNotFound_WhenFromAccountNotFound() {
        Long customerNumber = 123L;
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(999L);
        transferDetails.setToAccountNumber(200L);
        transferDetails.setTransferAmount(500.0);
        
        Customer customer = new Customer();
        
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, customerNumber);
        
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("From Account Number " + transferDetails.getFromAccountNumber() + " not found.", result.getBody());
        verify(accountRepository, never()).saveAll(any());
    }
    
    @Test
    void transferDetails_ShouldReturnNotFound_WhenToAccountNotFound() {
        Long customerNumber = 123L;
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100L);
        transferDetails.setToAccountNumber(999L);
        transferDetails.setTransferAmount(500.0);
        
        Customer customer = new Customer();
        Account fromAccount = new Account();
        fromAccount.setAccountBalance(1000.0);
        
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(999L)).thenReturn(Optional.empty());
        
        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, customerNumber);
        
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("To Account Number " + transferDetails.getToAccountNumber() + " not found.", result.getBody());
        verify(accountRepository, never()).saveAll(any());
    }
}
