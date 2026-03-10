package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private Customer sampleCustomer;
    private CustomerDetails sampleCustomerDetails;

    @BeforeEach
    public void setUp() throws Exception {
        bankingService = new BankingServiceImpl(customerRepository);
        // Inject remaining @Autowired fields via reflection
        setField(bankingService, "accountRepository", accountRepository);
        setField(bankingService, "transactionRepository", transactionRepository);
        setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
        setField(bankingService, "bankingServiceHelper", bankingServiceHelper);

        sampleCustomer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(Address.builder().address1("123 Main St").city("Springfield").state("IL").zip("62704").country("US").build())
                .contactDetails(Contact.builder().emailId("john@example.com").homePhone("555-1234").workPhone("555-5678").build())
                .build();

        sampleCustomerDetails = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(AddressDetails.builder().address1("123 Main St").city("Springfield").state("IL").zip("62704").country("US").build())
                .contactDetails(ContactDetails.builder().emailId("john@example.com").homePhone("555-1234").workPhone("555-5678").build())
                .build();
    }

    // ============================================================
    // findAll()
    // ============================================================

    @Test
    public void testFindAll_emptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDetails> result = bankingService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindAll_twoCustomers() {
        Customer customer2 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(1002L)
                .status("Active")
                .customerAddress(Address.builder().address1("456 Oak").city("Chicago").state("IL").zip("60601").country("US").build())
                .contactDetails(Contact.builder().emailId("jane@example.com").homePhone("555-0000").workPhone("555-1111").build())
                .build();

        CustomerDetails details2 = CustomerDetails.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(1002L)
                .status("Active")
                .build();

        when(customerRepository.findAll()).thenReturn(Arrays.asList(sampleCustomer, customer2));
        when(bankingServiceHelper.convertToCustomerDomain(sampleCustomer)).thenReturn(sampleCustomerDetails);
        when(bankingServiceHelper.convertToCustomerDomain(customer2)).thenReturn(details2);

        List<CustomerDetails> result = bankingService.findAll();

        assertEquals(2, result.size());
        verify(bankingServiceHelper, times(2)).convertToCustomerDomain(any(Customer.class));
    }

    // ============================================================
    // addCustomer()
    // ============================================================

    @Test
    public void testAddCustomer_happyPath() {
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(sampleCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        ResponseEntity<Object> result = bankingService.addCustomer(sampleCustomerDetails);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(bankingServiceHelper).convertToCustomerEntity(sampleCustomerDetails);
        verify(customerRepository).save(any(Customer.class));
    }

    // ============================================================
    // findByCustomerNumber()
    // ============================================================

    @Test
    public void testFindByCustomerNumber_found() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(sampleCustomer));
        when(bankingServiceHelper.convertToCustomerDomain(sampleCustomer)).thenReturn(sampleCustomerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(1001L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    public void testFindByCustomerNumber_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(9999L);

        assertNull(result);
    }

    // ============================================================
    // updateCustomer()
    // ============================================================

    @Test
    public void testUpdateCustomer_found_withExistingContactAndAddress() {
        Contact existingContact = Contact.builder().emailId("old@mail.com").homePhone("000-0000").workPhone("111-1111").build();
        Address existingAddress = Address.builder().address1("Old St").city("OldCity").state("OS").zip("00000").country("US").build();
        Customer managedCustomer = Customer.builder()
                .firstName("Old")
                .lastName("Name")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(existingContact)
                .customerAddress(existingAddress)
                .build();

        Customer unmanagedCustomer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .status("Active")
                .contactDetails(Contact.builder().emailId("john@example.com").homePhone("555-1234").workPhone("555-5678").build())
                .customerAddress(Address.builder().address1("123 Main St").city("Springfield").state("IL").zip("62704").country("US").build())
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(sampleCustomerDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("john@example.com", existingContact.getEmailId());
        assertEquals("555-1234", existingContact.getHomePhone());
        assertEquals("555-5678", existingContact.getWorkPhone());
        assertEquals("123 Main St", existingAddress.getAddress1());
        assertEquals("Springfield", existingAddress.getCity());
    }

    @Test
    public void testUpdateCustomer_found_nullManagedContact() {
        Customer managedCustomer = Customer.builder()
                .firstName("Old")
                .lastName("Name")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(null)
                .customerAddress(Address.builder().address1("Old St").city("OldCity").state("OS").zip("00000").country("US").build())
                .build();

        Contact newContact = Contact.builder().emailId("new@mail.com").homePhone("999-9999").workPhone("888-8888").build();
        Customer unmanagedCustomer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .status("Active")
                .contactDetails(newContact)
                .customerAddress(Address.builder().address1("123 Main St").city("Springfield").state("IL").zip("62704").country("US").build())
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(sampleCustomerDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(newContact, managedCustomer.getContactDetails());
    }

    @Test
    public void testUpdateCustomer_found_nullManagedAddress() {
        Customer managedCustomer = Customer.builder()
                .firstName("Old")
                .lastName("Name")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(Contact.builder().emailId("old@mail.com").homePhone("000-0000").workPhone("111-1111").build())
                .customerAddress(null)
                .build();

        Address newAddress = Address.builder().address1("New St").city("NewCity").state("NS").zip("11111").country("US").build();
        Customer unmanagedCustomer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .status("Active")
                .contactDetails(Contact.builder().emailId("john@example.com").homePhone("555-1234").workPhone("555-5678").build())
                .customerAddress(newAddress)
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(sampleCustomerDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(newAddress, managedCustomer.getCustomerAddress());
    }

    @Test
    public void testUpdateCustomer_found_nullContactInPayload() {
        Contact existingContact = Contact.builder().emailId("old@mail.com").homePhone("000-0000").workPhone("111-1111").build();
        Customer managedCustomer = Customer.builder()
                .firstName("Old")
                .lastName("Name")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(existingContact)
                .customerAddress(Address.builder().address1("Old St").city("OldCity").state("OS").zip("00000").country("US").build())
                .build();

        Customer unmanagedCustomer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .status("Active")
                .contactDetails(null)
                .customerAddress(Address.builder().address1("123 Main St").city("Springfield").state("IL").zip("62704").country("US").build())
                .build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanagedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(managedCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(sampleCustomerDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        // Contact should remain unchanged since payload has null contact
        assertEquals("old@mail.com", managedCustomer.getContactDetails().getEmailId());
    }

    @Test
    public void testUpdateCustomer_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(sampleCustomer);

        ResponseEntity<Object> result = bankingService.updateCustomer(sampleCustomerDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("9999"));
    }

    // ============================================================
    // deleteCustomer()
    // ============================================================

    @Test
    public void testDeleteCustomer_found() {
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(sampleCustomer));

        ResponseEntity<Object> result = bankingService.deleteCustomer(1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success: Customer deleted.", result.getBody());
        verify(customerRepository).delete(sampleCustomer);
    }

    @Test
    public void testDeleteCustomer_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.deleteCustomer(9999L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Customer does not exist.", result.getBody());
    }

    // ============================================================
    // findByAccountNumber()
    // ============================================================

    @Test
    public void testFindByAccountNumber_found() {
        Account account = Account.builder().accountNumber(5001L).accountBalance(1000.0).build();
        AccountInformation accInfo = AccountInformation.builder().accountNumber(5001L).accountBalance(1000.0).build();

        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(bankingServiceHelper.convertToAccountDomain(account)).thenReturn(accInfo);

        ResponseEntity<Object> result = bankingService.findByAccountNumber(5001L);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertEquals(accInfo, result.getBody());
    }

    @Test
    public void testFindByAccountNumber_notFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.findByAccountNumber(9999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("9999"));
    }

    // ============================================================
    // addNewAccount()
    // ============================================================

    @Test
    public void testAddNewAccount_customerFound() {
        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountBalance(500.0)
                .bankInformation(BankInformation.builder().branchCode(100).branchName("Main").routingNumber(123456).branchAddress(AddressDetails.builder().address1("1 St").city("C").state("S").zip("0").country("US").build()).build())
                .build();

        Account accountEntity = Account.builder().accountNumber(5001L).build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(sampleCustomer));
        when(bankingServiceHelper.convertToAccountEntity(accInfo)).thenReturn(accountEntity);
        when(accountRepository.save(any(Account.class))).thenReturn(accountEntity);
        when(custAccXRefRepository.save(any(CustomerAccountXRef.class))).thenReturn(null);

        ResponseEntity<Object> result = bankingService.addNewAccount(accInfo, 1001L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(accountRepository).save(accountEntity);
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    public void testAddNewAccount_customerNotFound() {
        // Documents existing bug: method returns 201 regardless of whether customer exists
        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountBalance(500.0)
                .build();

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.addNewAccount(accInfo, 9999L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ============================================================
    // transferDetails()
    // ============================================================

    @Test
    public void testTransferDetails_customerNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(100.0);

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("9999"));
    }

    @Test
    public void testTransferDetails_fromAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(100.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("5001"));
    }

    @Test
    public void testTransferDetails_toAccountNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(100.0);

        Account fromAccount = Account.builder().accountNumber(5001L).accountBalance(1000.0).build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("5002"));
    }

    @Test
    public void testTransferDetails_insufficientFunds() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(2000.0);

        Account fromAccount = Account.builder().accountNumber(5001L).accountBalance(500.0).build();
        Account toAccount = Account.builder().accountNumber(5002L).accountBalance(300.0).build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Insufficient Funds.", result.getBody());
    }

    @Test
    public void testTransferDetails_success() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(200.0);

        Account fromAccount = Account.builder().accountNumber(5001L).accountBalance(1000.0).build();
        Account toAccount = Account.builder().accountNumber(5002L).accountBalance(300.0).build();

        Transaction debitTx = Transaction.builder().accountNumber(5001L).txAmount(200.0).txType("DEBIT").build();
        Transaction creditTx = Transaction.builder().accountNumber(5002L).txAmount(200.0).txType("CREDIT").build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(sampleCustomer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(eq(transferDetails), eq(5001L), eq("DEBIT"))).thenReturn(debitTx);
        when(bankingServiceHelper.createTransaction(eq(transferDetails), eq(5002L), eq("CREDIT"))).thenReturn(creditTx);

        ResponseEntity<Object> result = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        // Verify balances updated
        assertEquals(Double.valueOf(800.0), fromAccount.getAccountBalance());
        assertEquals(Double.valueOf(500.0), toAccount.getAccountBalance());
        verify(accountRepository).saveAll(anyList());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    // ============================================================
    // findTransactionsByAccountNumber()
    // ============================================================

    @Test
    public void testFindTransactionsByAccountNumber_accountNotFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(9999L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindTransactionsByAccountNumber_noTransactions() {
        Account account = Account.builder().accountNumber(5001L).build();
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindTransactionsByAccountNumber_withTransactions() {
        Account account = Account.builder().accountNumber(5001L).build();
        Transaction tx1 = Transaction.builder().accountNumber(5001L).txAmount(100.0).txType("DEBIT").build();
        Transaction tx2 = Transaction.builder().accountNumber(5001L).txAmount(200.0).txType("CREDIT").build();

        TransactionDetails td1 = TransactionDetails.builder().accountNumber(5001L).txAmount(100.0).txType("DEBIT").build();
        TransactionDetails td2 = TransactionDetails.builder().accountNumber(5001L).txAmount(200.0).txType("CREDIT").build();

        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(Arrays.asList(tx1, tx2)));
        when(bankingServiceHelper.convertToTransactionDomain(tx1)).thenReturn(td1);
        when(bankingServiceHelper.convertToTransactionDomain(tx2)).thenReturn(td2);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertEquals(2, result.size());
        verify(bankingServiceHelper, times(2)).convertToTransactionDomain(any(Transaction.class));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
