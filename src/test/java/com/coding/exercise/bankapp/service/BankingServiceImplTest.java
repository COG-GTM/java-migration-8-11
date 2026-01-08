package com.coding.exercise.bankapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private Customer testCustomer;
    private CustomerDetails testCustomerDetails;
    private Account testAccount;
    private AccountInformation testAccountInformation;
    private Transaction testTransaction;
    private TransactionDetails testTransactionDetails;

    @Before
    public void setUp() throws Exception {
        bankingService = new BankingServiceImpl(customerRepository);
        
        Field accountRepoField = BankingServiceImpl.class.getDeclaredField("accountRepository");
        accountRepoField.setAccessible(true);
        accountRepoField.set(bankingService, accountRepository);
        
        Field transactionRepoField = BankingServiceImpl.class.getDeclaredField("transactionRepository");
        transactionRepoField.setAccessible(true);
        transactionRepoField.set(bankingService, transactionRepository);
        
        Field custAccXRefRepoField = BankingServiceImpl.class.getDeclaredField("custAccXRefRepository");
        custAccXRefRepoField.setAccessible(true);
        custAccXRefRepoField.set(bankingService, custAccXRefRepository);
        
        Field helperField = BankingServiceImpl.class.getDeclaredField("bankingServiceHelper");
        helperField.setAccessible(true);
        helperField.set(bankingService, bankingServiceHelper);

        Address address = Address.builder()
                .address1("123 Main St")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        testCustomer = Customer.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(address)
                .contactDetails(contact)
                .createDateTime(new Date())
                .build();

        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
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
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddress)
                .build();

        testAccount = Account.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInfo)
                .createDateTime(new Date())
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

        testAccountInformation = AccountInformation.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInformation)
                .build();

        testTransaction = Transaction.builder()
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
    public void testFindAll() {
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
    public void testFindAllEmpty() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        List<CustomerDetails> result = bankingService.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testAddCustomer() {
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Object> result = bankingService.addCustomer(testCustomerDetails);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testFindByCustomerNumber() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerDomain(any(Customer.class))).thenReturn(testCustomerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(12345L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(Long.valueOf(12345L), result.getCustomerNumber());
    }

    @Test
    public void testFindByCustomerNumberNotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(99999L);

        assertNull(result);
    }

    @Test
    public void testUpdateCustomerSuccess() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(testCustomerDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testUpdateCustomerNotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(testCustomerDetails, 99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testUpdateCustomerWithNullContact() {
        Customer customerWithNullContact = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .contactDetails(null)
                .customerAddress(null)
                .build();

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(customerWithNullContact));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerWithNullContact);

        ResponseEntity<Object> result = bankingService.updateCustomer(testCustomerDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testUpdateCustomerWithExistingContactAndAddress() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(testCustomerDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testDeleteCustomerSuccess() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));

        ResponseEntity<Object> result = bankingService.deleteCustomer(12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerRepository, times(1)).delete(any(Customer.class));
    }

    @Test
    public void testDeleteCustomerNotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.deleteCustomer(99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testFindByAccountNumberSuccess() {
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(testAccount));
        when(bankingServiceHelper.convertToAccountDomain(any(Account.class))).thenReturn(testAccountInformation);

        ResponseEntity<Object> result = bankingService.findByAccountNumber(1000001L);

        assertNotNull(result);
        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void testFindByAccountNumberNotFound() {
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.findByAccountNumber(99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testAddNewAccountSuccess() {
        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToAccountEntity(any(AccountInformation.class))).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        ResponseEntity<Object> result = bankingService.addNewAccount(testAccountInformation, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    public void testAddNewAccountCustomerNotFound() {
        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.addNewAccount(testAccountInformation, 99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    public void testTransferDetailsSuccess() {
        Account fromAccount = Account.builder()
                .accountNumber(1000001L)
                .accountBalance(5000.00)
                .build();

        Account toAccount = Account.builder()
                .accountNumber(2000002L)
                .accountBalance(1000.00)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000001L);
        transferDetails.setToAccountNumber(2000002L);
        transferDetails.setTransferAmount(500.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(2000002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(any(TransferDetails.class), anyLong(), any(String.class)))
                .thenReturn(testTransaction);

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testTransferDetailsCustomerNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000001L);
        transferDetails.setToAccountNumber(2000002L);
        transferDetails.setTransferAmount(500.00);

        when(customerRepository.findByCustomerNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 99999L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testTransferDetailsFromAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(99999L);
        transferDetails.setToAccountNumber(2000002L);
        transferDetails.setTransferAmount(500.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testTransferDetailsToAccountNotFound() {
        Account fromAccount = Account.builder()
                .accountNumber(1000001L)
                .accountBalance(5000.00)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000001L);
        transferDetails.setToAccountNumber(99999L);
        transferDetails.setTransferAmount(500.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testTransferDetailsInsufficientFunds() {
        Account fromAccount = Account.builder()
                .accountNumber(1000001L)
                .accountBalance(100.00)
                .build();

        Account toAccount = Account.builder()
                .accountNumber(2000002L)
                .accountBalance(1000.00)
                .build();

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000001L);
        transferDetails.setToAccountNumber(2000002L);
        transferDetails.setTransferAmount(500.00);

        when(customerRepository.findByCustomerNumber(12345L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(2000002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 12345L);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testFindTransactionsByAccountNumberSuccess() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(transactions));
        when(bankingServiceHelper.convertToTransactionDomain(any(Transaction.class))).thenReturn(testTransactionDetails);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1000001L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindTransactionsByAccountNumberAccountNotFound() {
        when(accountRepository.findByAccountNumber(99999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(99999L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testFindTransactionsByAccountNumberNoTransactions() {
        when(accountRepository.findByAccountNumber(1000001L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(1000001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1000001L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
