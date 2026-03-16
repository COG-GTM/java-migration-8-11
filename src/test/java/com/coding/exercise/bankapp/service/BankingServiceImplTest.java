package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
        java.lang.reflect.Field accountRepoField = BankingServiceImpl.class.getDeclaredField("accountRepository");
        accountRepoField.setAccessible(true);
        accountRepoField.set(bankingService, accountRepository);
        java.lang.reflect.Field txRepoField = BankingServiceImpl.class.getDeclaredField("transactionRepository");
        txRepoField.setAccessible(true);
        txRepoField.set(bankingService, transactionRepository);
        java.lang.reflect.Field xrefRepoField = BankingServiceImpl.class.getDeclaredField("custAccXRefRepository");
        xrefRepoField.setAccessible(true);
        xrefRepoField.set(bankingService, custAccXRefRepository);
        java.lang.reflect.Field helperField = BankingServiceImpl.class.getDeclaredField("bankingServiceHelper");
        helperField.setAccessible(true);
        helperField.set(bankingService, bankingServiceHelper);
    }

    // ---- Helper builders ----

    private Customer buildCustomerEntity() {
        return Customer.builder()
                .firstName("John").middleName("M").lastName("Doe")
                .customerNumber(1001L).status("ACTIVE")
                .contactDetails(Contact.builder().emailId("j@d.com").homePhone("111").workPhone("222").build())
                .customerAddress(Address.builder().address1("123 St").city("NYC").state("NY").zip("10001").country("US").build())
                .build();
    }

    private CustomerDetails buildCustomerDetails() {
        return CustomerDetails.builder()
                .firstName("John").middleName("M").lastName("Doe")
                .customerNumber(1001L).status("ACTIVE")
                .contactDetails(ContactDetails.builder().emailId("j@d.com").homePhone("111").workPhone("222").build())
                .customerAddress(AddressDetails.builder().address1("123 St").city("NYC").state("NY").zip("10001").country("US").build())
                .build();
    }

    private Account buildAccountEntity(Long accountNumber, Double balance) {
        return Account.builder()
                .accountNumber(accountNumber).accountStatus("ACTIVE")
                .accountType("SAVINGS").accountBalance(balance)
                .bankInformation(BankInfo.builder().branchName("Main").branchCode(1).routingNumber(100)
                        .branchAddress(Address.builder().address1("x").city("y").state("z").zip("0").country("US").build()).build())
                .build();
    }

    private AccountInformation buildAccountInformation(Long accountNumber) {
        return AccountInformation.builder()
                .accountNumber(accountNumber).accountStatus("ACTIVE")
                .accountType("SAVINGS").accountBalance(1000.0)
                .bankInformation(BankInformation.builder().branchName("Main").branchCode(1).routingNumber(100)
                        .branchAddress(AddressDetails.builder().address1("x").city("y").state("z").zip("0").country("US").build()).build())
                .build();
    }

    // ==== findAll ====

    @Test
    void findAll_emptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDetails> result = bankingService.findAll();

        assertTrue(result.isEmpty());
        verify(customerRepository).findAll();
    }

    @Test
    void findAll_multipleCustomers() {
        Customer c1 = buildCustomerEntity();
        Customer c2 = Customer.builder().firstName("Jane").lastName("Smith").customerNumber(1002L).status("ACTIVE").build();
        when(customerRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        CustomerDetails cd1 = buildCustomerDetails();
        CustomerDetails cd2 = CustomerDetails.builder().firstName("Jane").lastName("Smith").customerNumber(1002L).status("ACTIVE").build();
        when(bankingServiceHelper.convertToCustomerDomain(c1)).thenReturn(cd1);
        when(bankingServiceHelper.convertToCustomerDomain(c2)).thenReturn(cd2);

        List<CustomerDetails> result = bankingService.findAll();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    // ==== addCustomer ====

    @Test
    void addCustomer_success() {
        CustomerDetails customerDetails = buildCustomerDetails();
        Customer customerEntity = buildCustomerEntity();
        when(bankingServiceHelper.convertToCustomerEntity(customerDetails)).thenReturn(customerEntity);

        ResponseEntity<Object> response = bankingService.addCustomer(customerDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(customerRepository).save(customerEntity);
    }

    // ==== findByCustomerNumber ====

    @Test
    void findByCustomerNumber_exists() {
        Customer customer = buildCustomerEntity();
        CustomerDetails customerDetails = buildCustomerDetails();
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails);

        CustomerDetails result = bankingService.findByCustomerNumber(1001L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void findByCustomerNumber_notExists() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        CustomerDetails result = bankingService.findByCustomerNumber(9999L);

        assertNull(result);
    }

    // ==== updateCustomer ====

    @Test
    void updateCustomer_success() {
        Customer managedCustomer = buildCustomerEntity();
        managedCustomer.setContactDetails(Contact.builder().emailId("old@old.com").homePhone("000").workPhone("000").build());
        managedCustomer.setCustomerAddress(Address.builder().address1("Old St").city("Old").state("OL").zip("00000").country("US").build());

        CustomerDetails updateDetails = buildCustomerDetails();
        Customer unmanagedEntity = buildCustomerEntity();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).save(managedCustomer);
    }

    @Test
    void updateCustomer_notFound() {
        CustomerDetails updateDetails = buildCustomerDetails();
        Customer unmanagedEntity = buildCustomerEntity();

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateCustomer_nullContact_setsNewContact() {
        Customer managedCustomer = buildCustomerEntity();
        managedCustomer.setContactDetails(null);

        CustomerDetails updateDetails = buildCustomerDetails();
        Customer unmanagedEntity = buildCustomerEntity();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(managedCustomer.getContactDetails());
    }

    @Test
    void updateCustomer_nullAddress_setsNewAddress() {
        Customer managedCustomer = buildCustomerEntity();
        managedCustomer.setCustomerAddress(null);

        CustomerDetails updateDetails = buildCustomerDetails();
        Customer unmanagedEntity = buildCustomerEntity();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(managedCustomer.getCustomerAddress());
    }

    @Test
    void updateCustomer_existingContactAndAddress_updatesFields() {
        Contact existingContact = Contact.builder().emailId("old@e.com").homePhone("old").workPhone("old").build();
        Address existingAddress = Address.builder().address1("old").address2("old").city("old").state("OL").zip("00000").country("OL").build();
        Customer managedCustomer = buildCustomerEntity();
        managedCustomer.setContactDetails(existingContact);
        managedCustomer.setCustomerAddress(existingAddress);

        CustomerDetails updateDetails = buildCustomerDetails();
        Customer unmanagedEntity = buildCustomerEntity();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managedCustomer));
        when(bankingServiceHelper.convertToCustomerEntity(updateDetails)).thenReturn(unmanagedEntity);

        ResponseEntity<Object> response = bankingService.updateCustomer(updateDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("j@d.com", existingContact.getEmailId());
        assertEquals("123 St", existingAddress.getAddress1());
    }

    // ==== deleteCustomer ====

    @Test
    void deleteCustomer_success() {
        Customer customer = buildCustomerEntity();
        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));

        ResponseEntity<Object> response = bankingService.deleteCustomer(1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_notFound() {
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.deleteCustomer(9999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ==== findByAccountNumber ====

    @Test
    void findByAccountNumber_found() {
        Account account = buildAccountEntity(5001L, 1000.0);
        AccountInformation accInfo = buildAccountInformation(5001L);
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(bankingServiceHelper.convertToAccountDomain(account)).thenReturn(accInfo);

        ResponseEntity<Object> response = bankingService.findByAccountNumber(5001L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    @Test
    void findByAccountNumber_notFound() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.findByAccountNumber(9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ==== addNewAccount ====

    @Test
    void addNewAccount_customerExists() {
        Customer customer = buildCustomerEntity();
        AccountInformation accInfo = buildAccountInformation(5001L);
        Account accountEntity = buildAccountEntity(5001L, 1000.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(bankingServiceHelper.convertToAccountEntity(accInfo)).thenReturn(accountEntity);

        ResponseEntity<Object> response = bankingService.addNewAccount(accInfo, 1001L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository).save(accountEntity);
        verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
    }

    @Test
    void addNewAccount_customerNotExists_stillReturns201() {
        // NOTE: This is a bug in the implementation - it returns 201 even when customer doesn't exist
        AccountInformation accInfo = buildAccountInformation(5001L);
        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.addNewAccount(accInfo, 9999L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountRepository, never()).save(any());
        verify(custAccXRefRepository, never()).save(any());
    }

    // ==== transferDetails ====

    @Test
    void transferDetails_success() {
        Customer customer = buildCustomerEntity();
        Account fromAccount = buildAccountEntity(5001L, 1000.0);
        Account toAccount = buildAccountEntity(5002L, 500.0);

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(200.0);

        Transaction fromTx = Transaction.builder().accountNumber(5001L).txAmount(200.0).txType("DEBIT").txDateTime(new Date()).build();
        Transaction toTx = Transaction.builder().accountNumber(5002L).txAmount(200.0).txType("CREDIT").txDateTime(new Date()).build();

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));
        when(bankingServiceHelper.createTransaction(eq(transferDetails), eq(5001L), eq("DEBIT"))).thenReturn(fromTx);
        when(bankingServiceHelper.createTransaction(eq(transferDetails), eq(5002L), eq("CREDIT"))).thenReturn(toTx);

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(800.0, fromAccount.getAccountBalance());
        assertEquals(700.0, toAccount.getAccountBalance());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferDetails_customerNotFound() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 9999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void transferDetails_fromAccountNotFound() {
        Customer customer = buildCustomerEntity();
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void transferDetails_toAccountNotFound() {
        Customer customer = buildCustomerEntity();
        Account fromAccount = buildAccountEntity(5001L, 1000.0);
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void transferDetails_insufficientFunds() {
        Customer customer = buildCustomerEntity();
        Account fromAccount = buildAccountEntity(5001L, 100.0);
        Account toAccount = buildAccountEntity(5002L, 500.0);

        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(200.0);

        when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(toAccount));

        ResponseEntity<Object> response = bankingService.transferDetails(transferDetails, 1001L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ==== findTransactionsByAccountNumber ====

    @Test
    void findTransactionsByAccountNumber_withTransactions() {
        Account account = buildAccountEntity(5001L, 1000.0);
        Transaction t1 = Transaction.builder().accountNumber(5001L).txAmount(100.0).txType("CREDIT").txDateTime(new Date()).build();
        Transaction t2 = Transaction.builder().accountNumber(5001L).txAmount(50.0).txType("DEBIT").txDateTime(new Date()).build();

        TransactionDetails td1 = TransactionDetails.builder().accountNumber(5001L).txAmount(100.0).txType("CREDIT").build();
        TransactionDetails td2 = TransactionDetails.builder().accountNumber(5001L).txAmount(50.0).txType("DEBIT").build();

        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(Arrays.asList(t1, t2)));
        when(bankingServiceHelper.convertToTransactionDomain(t1)).thenReturn(td1);
        when(bankingServiceHelper.convertToTransactionDomain(t2)).thenReturn(td2);

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertEquals(2, result.size());
    }

    @Test
    void findTransactionsByAccountNumber_noAccount() {
        when(accountRepository.findByAccountNumber(9999L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(9999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findTransactionsByAccountNumber_accountExistsButNoTransactions() {
        Account account = buildAccountEntity(5001L, 1000.0);
        when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

        List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

        assertTrue(result.isEmpty());
    }
}
