package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private Customer customer;
    private CustomerDetails customerDetails;
    private Account account;
    private AccountInformation accountInformation;

    @BeforeEach
    void setUp() {
        bankingService = new BankingServiceImpl(customerRepository);
        ReflectionTestUtils.setField(bankingService, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(bankingService, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
        ReflectionTestUtils.setField(bankingService, "bankingServiceHelper", bankingServiceHelper);

        customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(Contact.builder()
                        .emailId("john@example.com")
                        .homePhone("111-222-3333")
                        .workPhone("444-555-6666")
                        .build())
                .customerAddress(Address.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .createDateTime(new Date())
                .build();

        customerDetails = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com")
                        .homePhone("111-222-3333")
                        .workPhone("444-555-6666")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .build();

        account = Account.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(BankInfo.builder()
                        .branchName("Main Branch")
                        .branchCode(101)
                        .routingNumber(12345)
                        .branchAddress(Address.builder()
                                .address1("100 Bank St")
                                .city("Chicago")
                                .state("IL")
                                .zip("60601")
                                .country("US")
                                .build())
                        .build())
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
                                .address1("100 Bank St")
                                .city("Chicago")
                                .state("IL")
                                .zip("60601")
                                .country("US")
                                .build())
                        .build())
                .build();
    }

    // ========== Customer Operations ==========

    @Test
    void findAll_returnsListOfCustomers() {
        Customer customer2 = Customer.builder().firstName("Jane").lastName("Smith").customerNumber(1002L).build();
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer, customer2));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);
        CustomerDetails cd2 = CustomerDetails.builder().firstName("Jane").lastName("Smith").customerNumber(1002L).build();
        when(bankingServiceHelper.convertToCustomerDomain(customer2)).thenReturn(cd2);

        List<CustomerDetails> result = bankingService.findAll();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        verify(customerRepository).findAll();
    }

    @Test
    void findAll_returnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDetails> result = bankingService.findAll();

        assertTrue(result.isEmpty());
        verify(customerRepository).findAll();
    }

    @Test
    void findByCustomerNumber_found() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(1001L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(1001L, result.getCustomerNumber());
    }

    @Test
    void findByCustomerNumber_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(9999L);

        assertNull(result);
    }

    @Test
    void addCustomer_happyPath() {
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(customer);

        ResponseEntity<Object> response = bankingService.addCustomer(customerDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Customer created successfully.", response.getBody());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_existingCustomer() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        Customer unmanagedEntity = Customer.builder()
                .firstName("Johnny").lastName("Doe").middleName("M").status("Active")
                .contactDetails(Contact.builder().emailId("johnny@example.com").homePhone("999").workPhone("888").build())
                .customerAddress(Address.builder().address1("456 Oak").city("Chicago").state("IL").zip("60601").country("US").build())
                .build();
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer updated.", response.getBody());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_nonExistingCustomer() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(customer);

        ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer Number 9999 not found.", response.getBody());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_existing() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));

        ResponseEntity<Object> response = bankingService.deleteCustomer(1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer deleted.", response.getBody());
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_nonExisting() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.deleteCustomer(9999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Customer does not exist.", response.getBody());
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    // ========== Account Operations ==========

    @Test
    void findByAccountNumber_found() {
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(bankingServiceHelper.convertToAccountDomain(account)).thenReturn(accountInformation);

        ResponseEntity<Object> response = bankingService.findByAccountNumber(5001L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void findByAccountNumber_notFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.findByAccountNumber(9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account Number 9999 not found.", response.getBody());
    }

    @Test
    void addNewAccount_happyPath() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToAccountEntity(any(AccountInformation.class))).thenReturn(account);

        ResponseEntity<Object> response = bankingService.addNewAccount(accountInformation, 1001L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Account created successfully.", response.getBody());
        verify(accountRepository).save(account);
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    void addNewAccount_customerNotFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.addNewAccount(accountInformation, 9999L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
        verify(custAccXRefRepository, never()).save(any(CustomerAccountXRef.class));
    }

    // ========== Transaction / Transfer Operations ==========

    @Test
    void transferDetails_successfulTransfer() {
        Account fromAccount = Account.builder().accountNumber(5001L).accountBalance(1000.0).build();
        Account toAccount = Account.builder().accountNumber(5002L).accountBalance(500.0).build();
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 200.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(any(TransferDetails.class), anyLong(), any(String.class)))
                .thenReturn(Transaction.builder().accountNumber(5001L).txType("DEBIT").txAmount(200.0).txDateTime(new Date()).build())
                .thenReturn(Transaction.builder().accountNumber(5002L).txType("CREDIT").txAmount(200.0).txDateTime(new Date()).build());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(800.0, fromAccount.getAccountBalance());
        assertEquals(700.0, toAccount.getAccountBalance());
        verify(accountRepository).saveAll(any());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferDetails_customerNotFound() {
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 200.0);
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer Number 9999 not found.", response.getBody());
    }

    @Test
    void transferDetails_fromAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 200.0);
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("From Account Number 5001 not found.", response.getBody());
    }

    @Test
    void transferDetails_toAccountNotFound() {
        Account fromAccount = Account.builder().accountNumber(5001L).accountBalance(1000.0).build();
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 200.0);
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("To Account Number 5002 not found.", response.getBody());
    }

    @Test
    void transferDetails_insufficientFunds() {
        Account fromAccount = Account.builder().accountNumber(5001L).accountBalance(100.0).build();
        Account toAccount = Account.builder().accountNumber(5002L).accountBalance(500.0).build();
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 999.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient Funds.", response.getBody());
        verify(accountRepository, never()).saveAll(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void findTransactionsByAccountNumber_returnsTransactions() {
        Transaction tx1 = Transaction.builder().accountNumber(5001L).txType("CREDIT").txAmount(500.0).txDateTime(new Date()).build();
        Transaction tx2 = Transaction.builder().accountNumber(5001L).txType("DEBIT").txAmount(200.0).txDateTime(new Date()).build();

        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(Arrays.asList(tx1, tx2)));

        TransactionDetails td1 = TransactionDetails.builder().accountNumber(5001L).txType("CREDIT").txAmount(500.0).build();
        TransactionDetails td2 = TransactionDetails.builder().accountNumber(5001L).txType("DEBIT").txAmount(200.0).build();
        when(bankingServiceHelper.convertToTransactionDomain(tx1)).thenReturn(td1);
        when(bankingServiceHelper.convertToTransactionDomain(tx2)).thenReturn(td2);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertEquals(2, result.size());
        assertEquals("CREDIT", result.get(0).getTxType());
        assertEquals("DEBIT", result.get(1).getTxType());
    }

    @Test
    void findTransactionsByAccountNumber_accountNotFound_returnsEmptyList() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(9999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findTransactionsByAccountNumber_noTransactions_returnsEmptyList() {
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertTrue(result.isEmpty());
    }
}
