package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @InjectMocks
    private BankingServiceImpl service;

    private Customer testCustomer;
    private CustomerDetails testCustomerDetails;
    private Account testAccount;
    private AccountInformation testAccountInfo;

    @BeforeEach
    void setUp() {
        Contact contact = Contact.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();

        Address address = Address.builder()
            .address1("123 Main St")
            .address2("Apt 4B")
            .city("Anytown")
            .state("NY")
            .zip("12345")
            .country("USA")
            .build();

        testCustomer = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName("Q")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contact)
            .customerAddress(address)
            .createDateTime(new Date())
            .updateDateTime(new Date())
            .build();

        ContactDetails contactDetails = ContactDetails.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();

        AddressDetails addressDetails = AddressDetails.builder()
            .address1("123 Main St")
            .address2("Apt 4B")
            .city("Anytown")
            .state("NY")
            .zip("12345")
            .country("USA")
            .build();

        testCustomerDetails = CustomerDetails.builder()
            .customerNumber(123L)
            .firstName("John")
            .middleName("Q")
            .lastName("Doe")
            .status("ACTIVE")
            .contactDetails(contactDetails)
            .customerAddress(addressDetails)
            .build();

        BankInfo bankInfo = BankInfo.builder()
            .branchName("Test Bank")
            .routingNumber(123456789)
            .build();

        testAccount = Account.builder()
            .accountNumber(456L)
            .accountType("CHECKING")
            .accountBalance(1000.0)
            .bankInformation(bankInfo)
            .createDateTime(new Date())
            .updateDateTime(new Date())
            .build();

        BankInformation bankInformation = BankInformation.builder()
            .branchName("Test Bank")
            .routingNumber(123456789)
            .build();

        testAccountInfo = AccountInformation.builder()
            .accountNumber(456L)
            .accountType("CHECKING")
            .accountBalance(1000.0)
            .bankInformation(bankInformation)
            .build();
    }

    @Test
    void findAll_returnsConvertedCustomers() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(bankingServiceHelper.convertToCustomerDomain(testCustomer)).thenReturn(testCustomerDetails);

        List<CustomerDetails> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(testCustomerDetails, result.get(0));
        verify(customerRepository).findAll();
        verify(bankingServiceHelper).convertToCustomerDomain(testCustomer);
    }

    @Test
    void addCustomer_savesAndReturnsCreated() {
        when(bankingServiceHelper.convertToCustomerEntity(testCustomerDetails)).thenReturn(testCustomer);

        ResponseEntity<Object> result = service.addCustomer(testCustomerDetails);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("New Customer created successfully.", result.getBody());
        verify(customerRepository).save(any(Customer.class));
        verify(bankingServiceHelper).convertToCustomerEntity(testCustomerDetails);
    }

    @Test
    void findByCustomerNumber_foundReturnsDomain() {
        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerDomain(testCustomer)).thenReturn(testCustomerDetails);

        CustomerDetails result = service.findByCustomerNumber(123L);

        assertEquals(testCustomerDetails, result);
        verify(customerRepository).findByCustomerNumber(123L);
        verify(bankingServiceHelper).convertToCustomerDomain(testCustomer);
    }

    @Test
    void findByCustomerNumber_notFound_returnsNull() {
        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.empty());

        CustomerDetails result = service.findByCustomerNumber(123L);

        assertNull(result);
        verify(customerRepository).findByCustomerNumber(123L);
        verify(bankingServiceHelper, never()).convertToCustomerDomain(any());
    }

    @Test
    void updateCustomer_notFound_returns404() {
        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(testCustomerDetails)).thenReturn(testCustomer);

        ResponseEntity<Object> result = service.updateCustomer(testCustomerDetails, 123L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Customer Number 123 not found"));
        verify(customerRepository).findByCustomerNumber(123L);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_found_updatesAndSaves() {
        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(testCustomerDetails)).thenReturn(testCustomer);

        ResponseEntity<Object> result = service.updateCustomer(testCustomerDetails, 123L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success: Customer updated.", result.getBody());
        verify(customerRepository).findByCustomerNumber(123L);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void updateCustomer_nullContactDetails_setsNewContactDetails() {
        Customer existingCustomer = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .contactDetails(null)
            .customerAddress(Address.builder().city("OldCity").build())
            .build();

        CustomerDetails updateDetails = CustomerDetails.builder()
            .customerNumber(123L)
            .contactDetails(ContactDetails.builder()
                .emailId("new@example.com")
                .homePhone("555-1234")
                .build())
            .build();

        Contact newContact = Contact.builder()
            .emailId("new@example.com")
            .homePhone("555-1234")
            .build();

        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(
            Customer.builder().contactDetails(newContact).build()
        );

        ResponseEntity<Object> result = service.updateCustomer(updateDetails, 123L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerRepository).save(existingCustomer);
        assertEquals(newContact, existingCustomer.getContactDetails());
    }

    @Test
    void updateCustomer_nullAddress_setsNewAddress() {
        Customer existingCustomer = Customer.builder()
            .customerNumber(123L)
            .firstName("John")
            .lastName("Doe")
            .contactDetails(Contact.builder().emailId("old@example.com").build())
            .customerAddress(null)
            .build();

        CustomerDetails updateDetails = CustomerDetails.builder()
            .customerNumber(123L)
            .customerAddress(AddressDetails.builder()
                .address1("123 New St")
                .city("NewCity")
                .build())
            .build();

        Address newAddress = Address.builder()
            .address1("123 New St")
            .city("NewCity")
            .build();

        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(existingCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(
            Customer.builder().customerAddress(newAddress).build()
        );

        ResponseEntity<Object> result = service.updateCustomer(updateDetails, 123L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerRepository).save(existingCustomer);
        assertEquals(newAddress, existingCustomer.getCustomerAddress());
    }

    @Test
    void deleteCustomer_found_deletes_andReturnsOk() {
        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));

        ResponseEntity<Object> result = service.deleteCustomer(123L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success: Customer deleted.", result.getBody());
        verify(customerRepository).findByCustomerNumber(123L);
        verify(customerRepository).delete(testCustomer);
    }

    @Test
    void deleteCustomer_notFound_returnsBadRequest() {
        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = service.deleteCustomer(123L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Customer does not exist.", result.getBody());
        verify(customerRepository).findByCustomerNumber(123L);
        verify(customerRepository, never()).delete(any());
    }

    @Test
    void findByAccountNumber_found_returnsFOUND_withDomain() {
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.of(testAccount));
        when(bankingServiceHelper.convertToAccountDomain(testAccount)).thenReturn(testAccountInfo);

        ResponseEntity<Object> result = service.findByAccountNumber(456L);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertEquals(testAccountInfo, result.getBody());
        verify(accountRepository).findByAccountNumber(456L);
        verify(bankingServiceHelper).convertToAccountDomain(testAccount);
    }

    @Test
    void findByAccountNumber_notFound_returnsNotFound() {
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = service.findByAccountNumber(456L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Account Number 456 not found"));
        verify(accountRepository).findByAccountNumber(456L);
        verify(bankingServiceHelper, never()).convertToAccountDomain(any());
    }

    @Test
    void addNewAccount_existingCustomer_savesAccount_andXRef_returnsCreated() {
        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));
        when(bankingServiceHelper.convertToAccountEntity(testAccountInfo)).thenReturn(testAccount);

        ResponseEntity<Object> result = service.addNewAccount(testAccountInfo, 123L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("New Account created successfully.", result.getBody());
        verify(customerRepository).findByCustomerNumber(123L);
        verify(accountRepository).save(testAccount);
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    void addNewAccount_customerNotFound_stillReturnsCreated_butDoesNotSave() {
        when(customerRepository.findByCustomerNumber(999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = service.addNewAccount(testAccountInfo, 999L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("New Account created successfully.", result.getBody());
        verify(customerRepository).findByCustomerNumber(999L);
        verify(accountRepository, never()).save(any());
        verify(custAccXRefRepository, never()).save(any());
    }

    @Test
    void transferDetails_missingFromAccount_returns404() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(999L);
        transferDetails.setToAccountNumber(456L);
        transferDetails.setTransferAmount(100.0);

        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = service.transferDetails(transferDetails, 123L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("From Account Number 999 not found"));
        verify(accountRepository, never()).saveAll(any());
    }

    @Test
    void transferDetails_missingToAccount_returns404() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(456L);
        transferDetails.setToAccountNumber(999L);
        transferDetails.setTransferAmount(100.0);

        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber(999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> result = service.transferDetails(transferDetails, 123L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("To Account Number 999 not found"));
        verify(accountRepository, never()).saveAll(any());
    }

    @Test
    void transferDetails_insufficientFunds_returns400() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(456L);
        transferDetails.setToAccountNumber(789L);
        transferDetails.setTransferAmount(2000.0);

        Account toAccount = new Account();
        toAccount.setAccountNumber(789L);
        toAccount.setAccountBalance(500.0);

        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber(789L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> result = service.transferDetails(transferDetails, 123L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Insufficient Funds.", result.getBody());
        verify(accountRepository, never()).saveAll(any());
    }

    @Test
    void transferDetails_success_updatesBalances_savesAll_andCreatesTwoTransactions() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(456L);
        transferDetails.setToAccountNumber(789L);
        transferDetails.setTransferAmount(100.0);

        Account toAccount = new Account();
        toAccount.setAccountNumber(789L);
        toAccount.setAccountBalance(500.0);

        Transaction debitTransaction = new Transaction();
        Transaction creditTransaction = new Transaction();

        when(customerRepository.findByCustomerNumber(123L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber(789L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(transferDetails, 456L, "DEBIT")).thenReturn(debitTransaction);
        when(bankingServiceHelper.createTransaction(transferDetails, 789L, "CREDIT")).thenReturn(creditTransaction);

        ResponseEntity<Object> result = service.transferDetails(transferDetails, 123L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Success: Amount transferred for Customer Number 123"));
        assertEquals(Double.valueOf(900.0), testAccount.getAccountBalance());
        assertEquals(Double.valueOf(600.0), toAccount.getAccountBalance());
        verify(accountRepository).saveAll(any());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void findTransactionsByAccountNumber_found_returnsConvertedTransactions() {
        Transaction transaction = new Transaction();
        TransactionDetails transactionDetails = new TransactionDetails();
        List<Transaction> transactions = Arrays.asList(transaction);

        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(456L)).thenReturn(Optional.of(transactions));
        when(bankingServiceHelper.convertToTransactionDomain(transaction)).thenReturn(transactionDetails);

        List<TransactionDetails> result = service.findTransactionsByAccountNumber(456L);

        assertEquals(1, result.size());
        assertEquals(transactionDetails, result.get(0));
        verify(accountRepository).findByAccountNumber(456L);
        verify(transactionRepository).findByAccountNumber(456L);
        verify(bankingServiceHelper).convertToTransactionDomain(transaction);
    }

    @Test
    void findTransactionsByAccountNumber_accountNotFound_returnsEmptyList() {
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = service.findTransactionsByAccountNumber(456L);

        assertTrue(result.isEmpty());
        verify(accountRepository).findByAccountNumber(456L);
        verify(transactionRepository, never()).findByAccountNumber(any());
    }
}
