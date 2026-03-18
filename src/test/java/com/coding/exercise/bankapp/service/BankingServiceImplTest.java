package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    private BankingServiceImpl bankingService;

    @BeforeEach
    void setUp() throws Exception {
        bankingService = new BankingServiceImpl(customerRepository);
        setField(bankingService, "accountRepository", accountRepository);
        setField(bankingService, "transactionRepository", transactionRepository);
        setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
        setField(bankingService, "bankingServiceHelper", bankingServiceHelper);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ========== findAll() ==========

    @Test
    void findAll_shouldReturnListOfCustomers() {
        Customer customer1 = Customer.builder().firstName("John").customerNumber(1L).build();
        Customer customer2 = Customer.builder().firstName("Jane").customerNumber(2L).build();
        List<Customer> customers = Arrays.asList(customer1, customer2);

        CustomerDetails details1 = CustomerDetails.builder().firstName("John").customerNumber(1L).build();
        CustomerDetails details2 = CustomerDetails.builder().firstName("Jane").customerNumber(2L).build();

        when(customerRepository.findAll()).thenReturn(customers);
        when(bankingServiceHelper.convertToCustomerDomain(customer1)).thenReturn(details1);
        when(bankingServiceHelper.convertToCustomerDomain(customer2)).thenReturn(details2);

        List<CustomerDetails> result = bankingService.findAll();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        verify(customerRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoCustomers() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDetails> result = bankingService.findAll();

        assertTrue(result.isEmpty());
        verify(customerRepository).findAll();
    }

    // ========== addCustomer() ==========

    @Test
    void addCustomer_shouldReturnCreatedStatus() {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("John").lastName("Doe").build();
        Customer customerEntity = Customer.builder()
                .firstName("John").lastName("Doe").build();

        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customerEntity);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerEntity);

        ResponseEntity<Object> response = bankingService.addCustomer(customerDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(bankingServiceHelper).convertToCustomerEntity(customerDetails);
        verify(customerRepository).save(any(Customer.class));
    }

    // ========== findByCustomerNumber() ==========

    @Test
    void findByCustomerNumber_shouldReturnCustomerWhenFound() {
        Customer customer = Customer.builder().firstName("John").customerNumber(1L).build();
        CustomerDetails details = CustomerDetails.builder().firstName("John").customerNumber(1L).build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(details);

        CustomerDetails result = bankingService.findByCustomerNumber(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(1L, result.getCustomerNumber());
    }

    @Test
    void findByCustomerNumber_shouldReturnNullWhenNotFound() {
        when(customerRepository.findByCustomerNumber(999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(999L);

        assertNull(result);
    }

    // ========== updateCustomer() ==========

    @Test
    void updateCustomer_shouldReturnOkWhenCustomerFoundWithExistingContactAndAddress() {
        Contact existingContact = Contact.builder()
                .emailId("old@test.com").homePhone("111").workPhone("222").build();
        Address existingAddress = Address.builder()
                .address1("Old St").address2("").city("OldCity").state("OS").zip("00000").country("US").build();
        Customer managedCustomer = Customer.builder()
                .firstName("Old").customerNumber(1L)
                .contactDetails(existingContact)
                .customerAddress(existingAddress)
                .build();

        Contact newContact = Contact.builder()
                .emailId("new@test.com").homePhone("333").workPhone("444").build();
        Address newAddress = Address.builder()
                .address1("New St").address2("Apt 1").city("NewCity").state("NS").zip("11111").country("CA").build();
        Customer unmanagedCustomer = Customer.builder()
                .firstName("New").middleName("M").lastName("Name").status("ACTIVE")
                .contactDetails(newContact)
                .customerAddress(newAddress)
                .build();

        CustomerDetails updateDetails = CustomerDetails.builder()
                .firstName("New").middleName("M").lastName("Name").status("ACTIVE").build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).save(managedCustomer);
    }

    @Test
    void updateCustomer_shouldSetContactWhenExistingContactIsNull() {
        Customer managedCustomer = Customer.builder()
                .firstName("Old").customerNumber(1L)
                .contactDetails(null)
                .customerAddress(Address.builder().address1("Addr").address2("").city("C").state("S").zip("Z").country("US").build())
                .build();

        Contact newContact = Contact.builder()
                .emailId("new@test.com").homePhone("333").workPhone("444").build();
        Customer unmanagedCustomer = Customer.builder()
                .firstName("New").status("ACTIVE")
                .contactDetails(newContact)
                .customerAddress(Address.builder().address1("Addr").address2("").city("C").state("S").zip("Z").country("US").build())
                .build();

        CustomerDetails updateDetails = CustomerDetails.builder().firstName("New").status("ACTIVE").build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newContact, managedCustomer.getContactDetails());
    }

    @Test
    void updateCustomer_shouldSetAddressWhenExistingAddressIsNull() {
        Customer managedCustomer = Customer.builder()
                .firstName("Old").customerNumber(1L)
                .contactDetails(Contact.builder().emailId("e").homePhone("h").workPhone("w").build())
                .customerAddress(null)
                .build();

        Address newAddress = Address.builder()
                .address1("New St").address2("").city("NewCity").state("NS").zip("11111").country("CA").build();
        Customer unmanagedCustomer = Customer.builder()
                .firstName("New").status("ACTIVE")
                .contactDetails(Contact.builder().emailId("e").homePhone("h").workPhone("w").build())
                .customerAddress(newAddress)
                .build();

        CustomerDetails updateDetails = CustomerDetails.builder().firstName("New").status("ACTIVE").build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newAddress, managedCustomer.getCustomerAddress());
    }

    @Test
    void updateCustomer_shouldReturnNotFoundWhenCustomerDoesNotExist() {
        CustomerDetails updateDetails = CustomerDetails.builder().firstName("New").build();
        Customer unmanagedCustomer = Customer.builder().firstName("New").build();

        when(customerRepository.findByCustomerNumber(999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedCustomer);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerRepository, never()).save(any());
    }

    // ========== deleteCustomer() ==========

    @Test
    void deleteCustomer_shouldReturnOkWhenCustomerFound() {
        Customer customer = Customer.builder().firstName("John").customerNumber(1L).build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(customer));

        ResponseEntity<Object> response = bankingService.deleteCustomer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_shouldReturnBadRequestWhenCustomerNotFound() {
        when(customerRepository.findByCustomerNumber(999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.deleteCustomer(999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(customerRepository, never()).delete(any());
    }

    // ========== findByAccountNumber() ==========

    @Test
    void findByAccountNumber_shouldReturnFoundWhenAccountExists() {
        Account account = Account.builder().accountNumber(100L).accountBalance(5000.0).build();
        AccountInformation accInfo = AccountInformation.builder().accountNumber(100L).accountBalance(5000.0).build();

        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(account));
        when(bankingServiceHelper.convertToAccountDomain(account)).thenReturn(accInfo);

        ResponseEntity<Object> response = bankingService.findByAccountNumber(100L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(accInfo, response.getBody());
    }

    @Test
    void findByAccountNumber_shouldReturnNotFoundWhenAccountDoesNotExist() {
        when(accountRepository.findByAccountNumber(999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.findByAccountNumber(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ========== addNewAccount() ==========

    @Test
    void addNewAccount_shouldSaveAccountAndXRefWhenCustomerFound() {
        Customer customer = Customer.builder().customerNumber(1L).build();
        AccountInformation accInfo = AccountInformation.builder().accountNumber(100L).build();
        Account accountEntity = Account.builder().accountNumber(100L).build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToAccountEntity(accInfo)).thenReturn(accountEntity);
        when(accountRepository.save(accountEntity)).thenReturn(accountEntity);
        when(custAccXRefRepository.save(any(CustomerAccountXRef.class)))
                .thenReturn(CustomerAccountXRef.builder().build());

        ResponseEntity<Object> response = bankingService.addNewAccount(accInfo, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository).save(accountEntity);
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    void addNewAccount_shouldReturnCreatedEvenWhenCustomerNotFound() {
        AccountInformation accInfo = AccountInformation.builder().accountNumber(100L).build();

        when(customerRepository.findByCustomerNumber(999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.addNewAccount(accInfo, 999L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, never()).save(any());
    }

    // ========== transferDetails() ==========

    @Test
    void transferDetails_shouldReturnNotFoundWhenCustomerNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(50.0);

        when(customerRepository.findByCustomerNumber(999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void transferDetails_shouldReturnNotFoundWhenFromAccountNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(50.0);

        Customer customer = Customer.builder().customerNumber(1L).build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void transferDetails_shouldReturnNotFoundWhenToAccountNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(50.0);

        Customer customer = Customer.builder().customerNumber(1L).build();
        Account fromAccount = Account.builder().accountNumber(100L).accountBalance(1000.0).build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(200L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void transferDetails_shouldReturnBadRequestWhenInsufficientFunds() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(5000.0);

        Customer customer = Customer.builder().customerNumber(1L).build();
        Account fromAccount = Account.builder().accountNumber(100L).accountBalance(100.0).build();
        Account toAccount = Account.builder().accountNumber(200L).accountBalance(500.0).build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(200L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void transferDetails_shouldSuccessfullyTransferFunds() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(300.0);

        Customer customer = Customer.builder().customerNumber(1L).build();
        Account fromAccount = Account.builder().accountNumber(100L).accountBalance(1000.0).build();
        Account toAccount = Account.builder().accountNumber(200L).accountBalance(500.0).build();
        Transaction debitTx = Transaction.builder().accountNumber(100L).txType("DEBIT").txAmount(300.0).build();
        Transaction creditTx = Transaction.builder().accountNumber(200L).txType("CREDIT").txAmount(300.0).build();

        when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(200L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(eq(transfer), eq(100L), eq("DEBIT"))).thenReturn(debitTx);
        when(bankingServiceHelper.createTransaction(eq(transfer), eq(200L), eq("CREDIT"))).thenReturn(creditTx);

        ResponseEntity<Object> response = bankingService.transferDetails(transfer, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(700.0, fromAccount.getAccountBalance());
        assertEquals(800.0, toAccount.getAccountBalance());
        verify(accountRepository).saveAll(any());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    // ========== findTransactionsByAccountNumber() ==========

    @Test
    void findTransactionsByAccountNumber_shouldReturnTransactionsWhenAccountFound() {
        Account account = Account.builder().accountNumber(100L).build();
        Transaction tx1 = Transaction.builder().accountNumber(100L).txAmount(100.0).txType("DEBIT").build();
        Transaction tx2 = Transaction.builder().accountNumber(100L).txAmount(200.0).txType("CREDIT").build();
        List<Transaction> transactions = Arrays.asList(tx1, tx2);

        TransactionDetails td1 = TransactionDetails.builder().accountNumber(100L).txAmount(100.0).txType("DEBIT").build();
        TransactionDetails td2 = TransactionDetails.builder().accountNumber(100L).txAmount(200.0).txType("CREDIT").build();

        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(100L)).thenReturn(Optional.of(transactions));
        when(bankingServiceHelper.convertToTransactionDomain(tx1)).thenReturn(td1);
        when(bankingServiceHelper.convertToTransactionDomain(tx2)).thenReturn(td2);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(100L);

        assertEquals(2, result.size());
        assertEquals("DEBIT", result.get(0).getTxType());
        assertEquals("CREDIT", result.get(1).getTxType());
    }

    @Test
    void findTransactionsByAccountNumber_shouldReturnEmptyListWhenNoTransactions() {
        Account account = Account.builder().accountNumber(100L).build();

        when(accountRepository.findByAccountNumber(100L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(100L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(100L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findTransactionsByAccountNumber_shouldReturnEmptyListWhenAccountNotFound() {
        when(accountRepository.findByAccountNumber(999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(999L);

        assertTrue(result.isEmpty());
    }
}
