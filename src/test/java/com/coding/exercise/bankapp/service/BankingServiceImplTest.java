package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    private Customer customer;
    private CustomerDetails customerDetails;
    private Account fromAccount;
    private Account toAccount;
    private AccountInformation accountInformation;

    @BeforeEach
    void setUp() {
        bankingService = new BankingServiceImpl(customerRepository);
        ReflectionTestUtils.setField(bankingService, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(bankingService, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
        ReflectionTestUtils.setField(bankingService, "bankingServiceHelper", bankingServiceHelper);

        Address address = Address.builder()
                .address1("123 Main St").address2("Apt 1")
                .city("Springfield").state("IL").zip("62701").country("US")
                .build();
        Contact contact = Contact.builder()
                .emailId("john@example.com").homePhone("555-1234").workPhone("555-5678")
                .build();
        customer = Customer.builder()
                .firstName("John").middleName("M").lastName("Doe")
                .customerNumber(1001L).status("Active")
                .contactDetails(contact).customerAddress(address)
                .createDateTime(new Date())
                .build();

        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St").address2("Apt 1")
                .city("Springfield").state("IL").zip("62701").country("US")
                .build();
        ContactDetails contactDet = ContactDetails.builder()
                .emailId("john@example.com").homePhone("555-1234").workPhone("555-5678")
                .build();
        customerDetails = CustomerDetails.builder()
                .firstName("John").middleName("M").lastName("Doe")
                .customerNumber(1001L).status("Active")
                .contactDetails(contactDet).customerAddress(addressDetails)
                .build();

        BankInfo bankInfo = BankInfo.builder()
                .branchName("Main").branchCode(100).routingNumber(999)
                .branchAddress(address)
                .build();
        fromAccount = Account.builder()
                .accountNumber(5001L).accountType("SAVINGS")
                .accountBalance(10000.0).accountStatus("Active")
                .bankInformation(bankInfo).createDateTime(new Date())
                .build();
        toAccount = Account.builder()
                .accountNumber(5002L).accountType("CHECKING")
                .accountBalance(2000.0).accountStatus("Active")
                .bankInformation(bankInfo).createDateTime(new Date())
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("Main").branchCode(100).routingNumber(999)
                .branchAddress(addressDetails)
                .build();
        accountInformation = AccountInformation.builder()
                .accountNumber(5001L).accountType("SAVINGS")
                .accountBalance(10000.0).accountStatus("Active")
                .bankInformation(bankInformation)
                .build();
    }

    // ---- findAll ----

    @Test
    void findAll_shouldReturnAllCustomers() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);

        List<CustomerDetails> result = bankingService.findAll();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(customerRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoCustomers() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        List<CustomerDetails> result = bankingService.findAll();

        assertTrue(result.isEmpty());
    }

    // ---- addCustomer ----

    @Test
    void addCustomer_shouldCreateCustomerSuccessfully() {
        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        ResponseEntity<Object> result = bankingService.addCustomer(customerDetails);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(customerRepository).save(any(Customer.class));
    }

    // ---- findByCustomerNumber ----

    @Test
    void findByCustomerNumber_shouldReturnCustomerWhenFound() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(1001L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void findByCustomerNumber_shouldReturnNullWhenNotFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(9999L);

        assertNull(result);
    }

    // ---- updateCustomer ----

    @Test
    void updateCustomer_shouldUpdateWhenCustomerExists() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        ResponseEntity<Object> result = bankingService.updateCustomer(customerDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Customer updated"));
    }

    @Test
    void updateCustomer_shouldReturnNotFoundWhenCustomerDoesNotExist() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customer);

        ResponseEntity<Object> result = bankingService.updateCustomer(customerDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void updateCustomer_shouldUpdateContactWhenExistingContactIsNull() {
        Customer existingCustomer = Customer.builder()
                .firstName("Old").lastName("Name").customerNumber(1001L)
                .status("Active").contactDetails(null).customerAddress(null)
                .build();
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(customerDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateCustomer_shouldUpdateWhenUnmanagedEntityHasNullContactAndAddress() {
        Customer unmanagedCustomer = Customer.builder()
                .firstName("John").lastName("Doe").customerNumber(1001L)
                .status("Active").contactDetails(null).customerAddress(null)
                .build();
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        ResponseEntity<Object> result = bankingService.updateCustomer(customerDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    // ---- deleteCustomer ----

    @Test
    void deleteCustomer_shouldDeleteWhenCustomerExists() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));

        ResponseEntity<Object> result = bankingService.deleteCustomer(1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_shouldReturnBadRequestWhenCustomerDoesNotExist() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.deleteCustomer(9999L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    // ---- findByAccountNumber ----

    @Test
    void findByAccountNumber_shouldReturnAccountWhenFound() {
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(bankingServiceHelper.convertToAccountDomain(fromAccount)).thenReturn(accountInformation);

        ResponseEntity<Object> result = bankingService.findByAccountNumber(5001L);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    void findByAccountNumber_shouldReturnNotFoundWhenNotExists() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.findByAccountNumber(9999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    // ---- addNewAccount ----

    @Test
    void addNewAccount_shouldCreateAccountWhenCustomerExists() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToAccountEntity(accountInformation)).thenReturn(fromAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(fromAccount);
        when(custAccXRefRepository.save(any(CustomerAccountXRef.class))).thenReturn(CustomerAccountXRef.builder().build());

        ResponseEntity<Object> result = bankingService.addNewAccount(accountInformation, 1001L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void addNewAccount_shouldStillReturnCreatedWhenCustomerDoesNotExist() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.addNewAccount(accountInformation, 9999L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ---- transferDetails ----

    @Test
    void transferDetails_shouldTransferSuccessfully() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(any(), anyLong(), eq("DEBIT")))
                .thenReturn(Transaction.builder().accountNumber(5001L).txAmount(500.0).txType("DEBIT").txDateTime(new Date()).build());
        when(bankingServiceHelper.createTransaction(any(), anyLong(), eq("CREDIT")))
                .thenReturn(Transaction.builder().accountNumber(5002L).txAmount(500.0).txType("CREDIT").txDateTime(new Date()).build());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(9500.0, fromAccount.getAccountBalance());
        assertEquals(2500.0, toAccount.getAccountBalance());
    }

    @Test
    void transferDetails_shouldReturnNotFoundWhenCustomerDoesNotExist() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void transferDetails_shouldReturnNotFoundWhenFromAccountDoesNotExist() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(9999L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("From Account"));
    }

    @Test
    void transferDetails_shouldReturnNotFoundWhenToAccountDoesNotExist() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(9999L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("To Account"));
    }

    @Test
    void transferDetails_shouldReturnBadRequestWhenInsufficientFunds() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(999999.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Insufficient"));
    }

    // ---- findTransactionsByAccountNumber ----

    @Test
    void findTransactionsByAccountNumber_shouldReturnTransactionsWhenAccountExists() {
        Transaction tx = Transaction.builder()
                .accountNumber(5001L).txAmount(500.0).txType("DEBIT").txDateTime(new Date())
                .build();
        TransactionDetails txDetails = TransactionDetails.builder()
                .accountNumber(5001L).txAmount(500.0).txType("DEBIT").txDateTime(new Date())
                .build();

        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(Arrays.asList(tx)));
        when(bankingServiceHelper.convertToTransactionDomain(tx)).thenReturn(txDetails);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertEquals(1, result.size());
        assertEquals(500.0, result.get(0).getTxAmount());
    }

    @Test
    void findTransactionsByAccountNumber_shouldReturnEmptyWhenAccountDoesNotExist() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(9999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findTransactionsByAccountNumber_shouldReturnEmptyWhenNoTransactions() {
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertTrue(result.isEmpty());
    }
}
