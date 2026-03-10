package com.coding.exercise.bankapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
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

    private Customer customer;
    private CustomerDetails customerDetails;
    private Account account;
    private AccountInformation accountInformation;

    @Before
    public void setUp() {
        bankingService = new BankingServiceImpl(customerRepository);
        ReflectionTestUtils.setField(bankingService, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(bankingService, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
        ReflectionTestUtils.setField(bankingService, "bankingServiceHelper", bankingServiceHelper);

        Address address = Address.builder()
                .address1("123 Main St")
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

        customer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .build();

        customerDetails = CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com")
                        .homePhone("111-222-3333")
                        .workPhone("444-555-6666")
                        .build())
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main Branch")
                .branchCode(101)
                .routingNumber(12345)
                .branchAddress(address)
                .build();

        account = Account.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
                .build();

        accountInformation = AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("Main Branch")
                        .branchCode(101)
                        .routingNumber(12345)
                        .branchAddress(AddressDetails.builder()
                                .address1("123 Main St")
                                .city("Springfield")
                                .state("IL")
                                .zip("62701")
                                .country("US")
                                .build())
                        .build())
                .build();
    }

    // ===================== findAll =====================

    @Test
    public void findAll_returnsCustomerList() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);

        List<CustomerDetails> result = bankingService.findAll();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(customerRepository).findAll();
        verify(bankingServiceHelper).convertToCustomerDomain(customer);
    }

    @Test
    public void findAll_emptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDetails> result = bankingService.findAll();

        assertTrue(result.isEmpty());
    }

    // ===================== addCustomer =====================

    @Test
    public void addCustomer_success() {
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        ResponseEntity<Object> response = bankingService.addCustomer(customerDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Customer created successfully.", response.getBody());
        verify(customerRepository).save(any(Customer.class));
    }

    // ===================== findByCustomerNumber =====================

    @Test
    public void findByCustomerNumber_found() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(1001L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(Long.valueOf(1001L), result.getCustomerNumber());
    }

    @Test
    public void findByCustomerNumber_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(9999L);

        assertNull(result);
    }

    // ===================== updateCustomer =====================

    @Test
    public void updateCustomer_success() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer updated.", response.getBody());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    public void updateCustomer_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(customer);

        ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void updateCustomer_withNullContact() {
        Customer customerNoContact = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(null)
                .customerAddress(null)
                .createDateTime(new Date())
                .build();

        Customer unmanagedWithContact = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(Contact.builder().emailId("new@example.com").build())
                .customerAddress(Address.builder().address1("456 Other St").build())
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customerNoContact));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedWithContact);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerNoContact);

        ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ===================== deleteCustomer =====================

    @Test
    public void deleteCustomer_success() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));

        ResponseEntity<Object> response = bankingService.deleteCustomer(1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer deleted.", response.getBody());
        verify(customerRepository).delete(customer);
    }

    @Test
    public void deleteCustomer_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.deleteCustomer(9999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Customer does not exist.", response.getBody());
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    // ===================== findByAccountNumber =====================

    @Test
    public void findByAccountNumber_found() {
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(bankingServiceHelper.convertToAccountDomain(account)).thenReturn(accountInformation);

        ResponseEntity<Object> response = bankingService.findByAccountNumber(5001L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        verify(bankingServiceHelper).convertToAccountDomain(account);
    }

    @Test
    public void findByAccountNumber_notFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.findByAccountNumber(9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account Number 9999 not found.", response.getBody());
    }

    // ===================== addNewAccount =====================

    @Test
    public void addNewAccount_customerExists() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToAccountEntity(any(AccountInformation.class))).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(custAccXRefRepository.save(any(CustomerAccountXRef.class))).thenReturn(CustomerAccountXRef.builder().build());

        ResponseEntity<Object> response = bankingService.addNewAccount(accountInformation, 1001L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Account created successfully.", response.getBody());
        verify(accountRepository).save(any(Account.class));
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    public void addNewAccount_customerNotExists() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.addNewAccount(accountInformation, 9999L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ===================== transferDetails =====================

    @Test
    public void transferDetails_success() {
        Account fromAccount = Account.builder()
                .accountNumber(5001L)
                .accountBalance(1000.0)
                .build();
        Account toAccount = Account.builder()
                .accountNumber(5002L)
                .accountBalance(500.0)
                .build();

        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(200.0);

        Transaction debitTx = Transaction.builder()
                .accountNumber(5001L)
                .txAmount(200.0)
                .txType("DEBIT")
                .txDateTime(new Date())
                .build();
        Transaction creditTx = Transaction.builder()
                .accountNumber(5002L)
                .txAmount(200.0)
                .txType("CREDIT")
                .txDateTime(new Date())
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(any(TransferDetails.class), eq(5001L), eq("DEBIT"))).thenReturn(debitTx);
        when(bankingServiceHelper.createTransaction(any(TransferDetails.class), eq(5002L), eq("CREDIT"))).thenReturn(creditTx);

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(800.0, fromAccount.getAccountBalance(), 0.001);
        assertEquals(700.0, toAccount.getAccountBalance(), 0.001);
        verify(accountRepository).saveAll(any());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    public void transferDetails_customerNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Customer Number 9999 not found."));
    }

    @Test
    public void transferDetails_fromAccountNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("From Account Number 5001 not found."));
    }

    @Test
    public void transferDetails_toAccountNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(200.0);

        Account fromAccount = Account.builder()
                .accountNumber(5001L)
                .accountBalance(1000.0)
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("To Account Number 5002 not found."));
    }

    @Test
    public void transferDetails_insufficientFunds() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(5000.0);

        Account fromAccount = Account.builder()
                .accountNumber(5001L)
                .accountBalance(100.0)
                .build();
        Account toAccount = Account.builder()
                .accountNumber(5002L)
                .accountBalance(500.0)
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1001L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient Funds.", response.getBody());
        verify(accountRepository, never()).saveAll(any());
    }

    // ===================== findTransactionsByAccountNumber =====================

    @Test
    public void findTransactionsByAccountNumber_found() {
        Transaction tx1 = Transaction.builder()
                .accountNumber(5001L)
                .txType("DEBIT")
                .txAmount(100.0)
                .txDateTime(new Date())
                .build();
        Transaction tx2 = Transaction.builder()
                .accountNumber(5001L)
                .txType("CREDIT")
                .txAmount(200.0)
                .txDateTime(new Date())
                .build();

        TransactionDetails td1 = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("DEBIT")
                .txAmount(100.0)
                .build();
        TransactionDetails td2 = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("CREDIT")
                .txAmount(200.0)
                .build();

        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(Arrays.asList(tx1, tx2)));
        when(bankingServiceHelper.convertToTransactionDomain(tx1)).thenReturn(td1);
        when(bankingServiceHelper.convertToTransactionDomain(tx2)).thenReturn(td2);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertEquals(2, result.size());
        assertEquals("DEBIT", result.get(0).getTxType());
        assertEquals("CREDIT", result.get(1).getTxType());
    }

    @Test
    public void findTransactionsByAccountNumber_accountNotFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(9999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findTransactionsByAccountNumber_noTransactions() {
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertTrue(result.isEmpty());
    }
}
