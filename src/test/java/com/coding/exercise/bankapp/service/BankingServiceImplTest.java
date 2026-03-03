package com.coding.exercise.bankapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

/**
 * Unit tests for BankingServiceImpl covering Java 8 modernized code.
 * Tests verify streams, Optional patterns, method references, and java.time usage.
 */
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

    @InjectMocks
    private BankingServiceImpl bankingService;

    private Customer testCustomer;
    private CustomerDetails testCustomerDetails;
    private Account testAccount;
    private AccountInformation testAccountInfo;
    private Transaction testTransaction;
    private TransactionDetails testTransactionDetails;

    @Before
    public void setUp() {
        testCustomer = Customer.builder()
                .customerNumber(1001L)
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .status("Active")
                .contactDetails(Contact.builder()
                        .emailId("john@example.com")
                        .homePhone("555-1234")
                        .workPhone("555-5678")
                        .build())
                .customerAddress(Address.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .createDateTime(LocalDateTime.now())
                .build();

        testCustomerDetails = CustomerDetails.builder()
                .customerNumber(1001L)
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .status("Active")
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com")
                        .homePhone("555-1234")
                        .workPhone("555-5678")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .build();

        testAccount = Account.builder()
                .accountNumber(2001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(BankInfo.builder()
                        .branchName("Main Branch")
                        .branchCode(100)
                        .routingNumber(12345)
                        .branchAddress(Address.builder()
                                .address1("456 Bank Ave")
                                .city("Springfield")
                                .state("IL")
                                .zip("62701")
                                .country("US")
                                .build())
                        .build())
                .createDateTime(LocalDateTime.now())
                .build();

        testAccountInfo = AccountInformation.builder()
                .accountNumber(2001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("Main Branch")
                        .branchCode(100)
                        .routingNumber(12345)
                        .branchAddress(AddressDetails.builder()
                                .address1("456 Bank Ave")
                                .city("Springfield")
                                .state("IL")
                                .zip("62701")
                                .country("US")
                                .build())
                        .build())
                .build();

        testTransaction = Transaction.builder()
                .accountNumber(2001L)
                .txAmount(100.0)
                .txType("DEBIT")
                .txDateTime(LocalDateTime.now())
                .build();

        testTransactionDetails = TransactionDetails.builder()
                .accountNumber(2001L)
                .txAmount(100.0)
                .txType("DEBIT")
                .txDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    public void testFindAll_ReturnsCustomerList() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(bankingServiceHelper.convertToCustomerDomain(testCustomer)).thenReturn(testCustomerDetails);

        List<CustomerDetails> result = bankingService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(bankingServiceHelper, times(1)).convertToCustomerDomain(testCustomer);
    }

    @Test
    public void testFindAll_EmptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDetails> result = bankingService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAddCustomer_Success() {
        when(bankingServiceHelper.convertToCustomerEntity(testCustomerDetails)).thenReturn(testCustomer);

        ResponseEntity<Object> response = bankingService.addCustomer(testCustomerDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(testCustomer.getCreateDateTime());
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    public void testFindByCustomerNumber_Found() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerDomain(testCustomer)).thenReturn(testCustomerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(1001L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    public void testFindByCustomerNumber_NotFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(9999L);

        assertNull(result);
    }

    @Test
    public void testUpdateCustomer_Success() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(testCustomerDetails)).thenReturn(testCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(testCustomerDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testUpdateCustomer_NotFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(testCustomerDetails)).thenReturn(testCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(testCustomerDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteCustomer_Success() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(testCustomer));

        ResponseEntity<Object> response = bankingService.deleteCustomer(1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository, times(1)).delete(testCustomer);
    }

    @Test
    public void testDeleteCustomer_NotFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.deleteCustomer(9999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindByAccountNumber_Found() {
        when(accountRepository.findByAccountNumber(2001L)).thenReturn(Optional.of(testAccount));
        when(bankingServiceHelper.convertToAccountDomain(testAccount)).thenReturn(testAccountInfo);

        ResponseEntity<Object> response = bankingService.findByAccountNumber(2001L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    @Test
    public void testFindByAccountNumber_NotFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.findByAccountNumber(9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddNewAccount_CustomerExists() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToAccountEntity(testAccountInfo)).thenReturn(testAccount);

        ResponseEntity<Object> response = bankingService.addNewAccount(testAccountInfo, 1001L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, times(1)).save(testAccount);
        verify(custAccXRefRepository, times(1)).save(any(CustomerAccountXRef.class));
    }

    @Test
    public void testAddNewAccount_CustomerNotFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.addNewAccount(testAccountInfo, 9999L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testTransferDetails_Success() {
        Account fromAccount = Account.builder()
                .accountNumber(2001L).accountBalance(1000.0)
                .createDateTime(LocalDateTime.now()).build();
        Account toAccount = Account.builder()
                .accountNumber(2002L).accountBalance(500.0)
                .createDateTime(LocalDateTime.now()).build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(2001L);
        transferDetails.setToAccountNumber(2002L);
        transferDetails.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(2001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(2002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(any(TransferDetails.class), anyLong(), any(String.class)))
                .thenReturn(testTransaction);

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(800.0, fromAccount.getAccountBalance(), 0.01);
        assertEquals(700.0, toAccount.getAccountBalance(), 0.01);
        assertNotNull(fromAccount.getUpdateDateTime());
        assertNotNull(toAccount.getUpdateDateTime());
    }

    @Test
    public void testTransferDetails_CustomerNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(2001L);
        transferDetails.setToAccountNumber(2002L);
        transferDetails.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testTransferDetails_InsufficientFunds() {
        Account fromAccount = Account.builder()
                .accountNumber(2001L).accountBalance(100.0)
                .createDateTime(LocalDateTime.now()).build();
        Account toAccount = Account.builder()
                .accountNumber(2002L).accountBalance(500.0)
                .createDateTime(LocalDateTime.now()).build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(2001L);
        transferDetails.setToAccountNumber(2002L);
        transferDetails.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(2001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(2002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindTransactionsByAccountNumber_WithTransactions() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(accountRepository.findByAccountNumber(2001L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(2001L)).thenReturn(Optional.of(transactions));
        when(bankingServiceHelper.convertToTransactionDomain(testTransaction)).thenReturn(testTransactionDetails);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(2001L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DEBIT", result.get(0).getTxType());
    }

    @Test
    public void testFindTransactionsByAccountNumber_AccountNotFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(9999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindTransactionsByAccountNumber_NoTransactions() {
        when(accountRepository.findByAccountNumber(2001L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(2001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(2001L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
