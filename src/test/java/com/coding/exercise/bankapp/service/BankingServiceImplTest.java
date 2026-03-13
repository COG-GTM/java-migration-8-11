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

import java.lang.reflect.Field;
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

    private Customer buildSampleCustomerEntity() {
        return Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(100L)
                .status("ACTIVE")
                .contactDetails(Contact.builder()
                        .emailId("john@example.com")
                        .homePhone("555-1234")
                        .workPhone("555-5678")
                        .build())
                .customerAddress(Address.builder()
                        .address1("123 Main St")
                        .city("New York")
                        .state("NY")
                        .zip("10001")
                        .country("USA")
                        .build())
                .createDateTime(new Date())
                .build();
    }

    private CustomerDetails buildSampleCustomerDetails() {
        return CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(100L)
                .status("ACTIVE")
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com")
                        .homePhone("555-1234")
                        .workPhone("555-5678")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St")
                        .city("New York")
                        .state("NY")
                        .zip("10001")
                        .country("USA")
                        .build())
                .build();
    }

    private Account buildSampleAccountEntity() {
        return Account.builder()
                .accountNumber(1001L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(5000.0)
                .bankInformation(BankInfo.builder()
                        .branchName("Main Branch")
                        .branchCode(1001)
                        .routingNumber(12345)
                        .branchAddress(Address.builder()
                                .address1("456 Bank St")
                                .city("New York")
                                .state("NY")
                                .zip("10002")
                                .country("USA")
                                .build())
                        .build())
                .createDateTime(new Date())
                .build();
    }

    private AccountInformation buildSampleAccountInfo() {
        return AccountInformation.builder()
                .accountNumber(1001L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(5000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("Main Branch")
                        .branchCode(1001)
                        .routingNumber(12345)
                        .branchAddress(AddressDetails.builder()
                                .address1("456 Bank St")
                                .city("New York")
                                .state("NY")
                                .zip("10002")
                                .country("USA")
                                .build())
                        .build())
                .build();
    }

    // ========== findByAccountNumber ==========

    @Test
    void findByAccountNumber_found_returnsAccountInfo() {
        Account account = buildSampleAccountEntity();
        AccountInformation accountInfo = buildSampleAccountInfo();

        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(account));
        when(bankingServiceHelper.convertToAccountDomain(account)).thenReturn(accountInfo);

        ResponseEntity<Object> response = bankingService.findByAccountNumber(1001L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AccountInformation);
        AccountInformation body = (AccountInformation) response.getBody();
        assertEquals(1001L, body.getAccountNumber());
    }

    @Test
    void findByAccountNumber_notFound_returns404() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.findByAccountNumber(9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account Number 9999 not found.", response.getBody());
    }

    // ========== createAccount (addNewAccount) ==========

    @Test
    void addNewAccount_customerExists_createsAccountAndXRef() {
        Customer customer = buildSampleCustomerEntity();
        AccountInformation accountInfo = buildSampleAccountInfo();
        Account accountEntity = buildSampleAccountEntity();

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToAccountEntity(accountInfo)).thenReturn(accountEntity);

        ResponseEntity<Object> response = bankingService.addNewAccount(accountInfo, 100L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Account created successfully.", response.getBody());
        verify(accountRepository).save(accountEntity);
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    void addNewAccount_customerNotFound_stillReturns201ButNoSave() {
        AccountInformation accountInfo = buildSampleAccountInfo();

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.addNewAccount(accountInfo, 9999L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
        verify(custAccXRefRepository, never()).save(any(CustomerAccountXRef.class));
    }

    // ========== transferDetails ==========

    @Test
    void transferDetails_validTransfer_updatesBalancesAndCreatesTransactions() {
        Customer customer = buildSampleCustomerEntity();
        Account fromAccount = Account.builder()
                .accountNumber(1001L)
                .accountBalance(5000.0)
                .build();
        Account toAccount = Account.builder()
                .accountNumber(1002L)
                .accountBalance(1000.0)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(500.0);

        Transaction debitTx = Transaction.builder().accountNumber(1001L).txType("DEBIT").txAmount(500.0).build();
        Transaction creditTx = Transaction.builder().accountNumber(1002L).txType("CREDIT").txAmount(500.0).build();

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(1002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(transferDetails, 1001L, "DEBIT")).thenReturn(debitTx);
        when(bankingServiceHelper.createTransaction(transferDetails, 1002L, "CREDIT")).thenReturn(creditTx);

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4500.0, fromAccount.getAccountBalance());
        assertEquals(1500.0, toAccount.getAccountBalance());
        verify(accountRepository).saveAll(any());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferDetails_insufficientFunds_returns400() {
        Customer customer = buildSampleCustomerEntity();
        Account fromAccount = Account.builder()
                .accountNumber(1001L)
                .accountBalance(100.0)
                .build();
        Account toAccount = Account.builder()
                .accountNumber(1002L)
                .accountBalance(1000.0)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(1002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 100L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient Funds.", response.getBody());
    }

    @Test
    void transferDetails_customerNotFound_returns404() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer Number 9999 not found.", response.getBody());
    }

    @Test
    void transferDetails_fromAccountNotFound_returns404() {
        Customer customer = buildSampleCustomerEntity();
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(9999L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("From Account Number 9999 not found.", response.getBody());
    }

    @Test
    void transferDetails_toAccountNotFound_returns404() {
        Customer customer = buildSampleCustomerEntity();
        Account fromAccount = Account.builder()
                .accountNumber(1001L)
                .accountBalance(5000.0)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(9999L);
        transferDetails.setTransferAmount(500.0);

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("To Account Number 9999 not found.", response.getBody());
    }

    // ========== findTransactionsByAccountNumber ==========

    @Test
    void findTransactionsByAccountNumber_found_returnsTransactions() {
        Account account = buildSampleAccountEntity();
        Transaction tx1 = Transaction.builder().accountNumber(1001L).txType("CREDIT").txAmount(500.0).txDateTime(new Date()).build();
        Transaction tx2 = Transaction.builder().accountNumber(1001L).txType("DEBIT").txAmount(200.0).txDateTime(new Date()).build();

        TransactionDetails td1 = TransactionDetails.builder().accountNumber(1001L).txType("CREDIT").txAmount(500.0).build();
        TransactionDetails td2 = TransactionDetails.builder().accountNumber(1001L).txType("DEBIT").txAmount(200.0).build();

        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(Arrays.asList(tx1, tx2)));
        when(bankingServiceHelper.convertToTransactionDomain(tx1)).thenReturn(td1);
        when(bankingServiceHelper.convertToTransactionDomain(tx2)).thenReturn(td2);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1001L);

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
        Account account = buildSampleAccountEntity();
        when(accountRepository.findByAccountNumber(1001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(1001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1001L);

        assertTrue(result.isEmpty());
    }

    // ========== findAll (customers) ==========

    @Test
    void findAll_returnsList() {
        Customer customer1 = buildSampleCustomerEntity();
        Customer customer2 = Customer.builder().firstName("Jane").lastName("Smith").customerNumber(200L).build();
        CustomerDetails cd1 = buildSampleCustomerDetails();
        CustomerDetails cd2 = CustomerDetails.builder().firstName("Jane").lastName("Smith").customerNumber(200L).build();

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        when(bankingServiceHelper.convertToCustomerDomain(customer1)).thenReturn(cd1);
        when(bankingServiceHelper.convertToCustomerDomain(customer2)).thenReturn(cd2);

        List<CustomerDetails> result = bankingService.findAll();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void findAll_emptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDetails> result = bankingService.findAll();

        assertTrue(result.isEmpty());
    }

    // ========== findByCustomerNumber ==========

    @Test
    void findByCustomerNumber_found_returnsCustomerDetails() {
        Customer customer = buildSampleCustomerEntity();
        CustomerDetails customerDetails = buildSampleCustomerDetails();

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(100L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(100L, result.getCustomerNumber());
    }

    @Test
    void findByCustomerNumber_notFound_returnsNull() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(9999L);

        assertNull(result);
    }

    // ========== addCustomer ==========

    @Test
    void addCustomer_success_returns201() {
        CustomerDetails customerDetails = buildSampleCustomerDetails();
        Customer customerEntity = buildSampleCustomerEntity();

        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customerEntity);

        ResponseEntity<Object> response = bankingService.addCustomer(customerDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Customer created successfully.", response.getBody());
        verify(customerRepository).save(customerEntity);
    }

    // ========== updateCustomer ==========

    @Test
    void updateCustomer_existing_returns200() {
        CustomerDetails updatedDetails = CustomerDetails.builder()
                .firstName("Johnny")
                .middleName("X")
                .lastName("Doe")
                .status("ACTIVE")
                .contactDetails(ContactDetails.builder()
                        .emailId("johnny@example.com")
                        .homePhone("555-0000")
                        .workPhone("555-1111")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("789 Updated St")
                        .city("Boston")
                        .state("MA")
                        .zip("02101")
                        .country("USA")
                        .build())
                .build();

        Customer existingEntity = buildSampleCustomerEntity();
        Customer unmanagedEntity = Customer.builder()
                .firstName("Johnny")
                .middleName("X")
                .lastName("Doe")
                .status("ACTIVE")
                .contactDetails(Contact.builder()
                        .emailId("johnny@example.com")
                        .homePhone("555-0000")
                        .workPhone("555-1111")
                        .build())
                .customerAddress(Address.builder()
                        .address1("789 Updated St")
                        .city("Boston")
                        .state("MA")
                        .zip("02101")
                        .country("USA")
                        .build())
                .build();

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(existingEntity));
        when(bankingServiceHelper.convertToCustomerEntity(updatedDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updatedDetails, 100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer updated.", response.getBody());
        verify(customerRepository).save(existingEntity);
    }

    @Test
    void updateCustomer_notFound_returns404() {
        CustomerDetails customerDetails = buildSampleCustomerDetails();
        Customer unmanagedEntity = buildSampleCustomerEntity();

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer Number 9999 not found.", response.getBody());
    }

    @Test
    void updateCustomer_withNullContactAndAddress_updatesBasicFields() {
        CustomerDetails updatedDetails = CustomerDetails.builder()
                .firstName("Johnny")
                .lastName("Doe")
                .status("ACTIVE")
                .build();

        Customer existingEntity = buildSampleCustomerEntity();
        Customer unmanagedEntity = Customer.builder()
                .firstName("Johnny")
                .lastName("Doe")
                .status("ACTIVE")
                .build();

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(existingEntity));
        when(bankingServiceHelper.convertToCustomerEntity(updatedDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updatedDetails, 100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).save(existingEntity);
    }

    @Test
    void updateCustomer_existingHasNullContact_setsNewContact() {
        Customer existingEntity = buildSampleCustomerEntity();
        existingEntity.setContactDetails(null);

        CustomerDetails updatedDetails = buildSampleCustomerDetails();
        Customer unmanagedEntity = Customer.builder()
                .firstName("Johnny")
                .lastName("Doe")
                .status("ACTIVE")
                .contactDetails(Contact.builder()
                        .emailId("new@example.com")
                        .homePhone("555-9999")
                        .workPhone("555-8888")
                        .build())
                .build();

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(existingEntity));
        when(bankingServiceHelper.convertToCustomerEntity(updatedDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updatedDetails, 100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).save(existingEntity);
    }

    @Test
    void updateCustomer_existingHasNullAddress_setsNewAddress() {
        Customer existingEntity = buildSampleCustomerEntity();
        existingEntity.setCustomerAddress(null);

        CustomerDetails updatedDetails = buildSampleCustomerDetails();
        Customer unmanagedEntity = Customer.builder()
                .firstName("Johnny")
                .lastName("Doe")
                .status("ACTIVE")
                .customerAddress(Address.builder()
                        .address1("999 New St")
                        .city("Boston")
                        .state("MA")
                        .zip("02101")
                        .country("USA")
                        .build())
                .build();

        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(existingEntity));
        when(bankingServiceHelper.convertToCustomerEntity(updatedDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updatedDetails, 100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).save(existingEntity);
    }

    // ========== deleteCustomer ==========

    @Test
    void deleteCustomer_existing_returns200() {
        Customer customer = buildSampleCustomerEntity();
        when(customerRepository.findByCustomerNumber(100L)).thenReturn(Optional.of(customer));

        ResponseEntity<Object> response = bankingService.deleteCustomer(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success: Customer deleted.", response.getBody());
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_notFound_returns400() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.deleteCustomer(9999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Customer does not exist.", response.getBody());
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}
