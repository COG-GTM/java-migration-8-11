package com.coding.exercise.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

	@BeforeEach
	public void setUp() {
		bankingService = new BankingServiceImpl(customerRepository);
		ReflectionTestUtils.setField(bankingService, "accountRepository", accountRepository);
		ReflectionTestUtils.setField(bankingService, "transactionRepository", transactionRepository);
		ReflectionTestUtils.setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
		ReflectionTestUtils.setField(bankingService, "bankingServiceHelper", bankingServiceHelper);
	}

	private Customer customerEntity() {
		return Customer.builder()
				.customerNumber(1001L)
				.firstName("Jane")
				.middleName("Q")
				.lastName("Doe")
				.status("ACTIVE")
				.contactDetails(Contact.builder().emailId("jane@example.com").homePhone("111").workPhone("222").build())
				.customerAddress(Address.builder().address1("1 Main St").city("Austin").state("TX").zip("78701").country("US").build())
				.build();
	}

	private CustomerDetails customerDetails() {
		return CustomerDetails.builder().customerNumber(1001L).firstName("Jane").lastName("Doe").build();
	}

	@Test
	public void testFindAll() {
		Customer customer = customerEntity();
		when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));
		when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails());

		List<CustomerDetails> result = bankingService.findAll();

		assertEquals(1, result.size());
		assertEquals(Long.valueOf(1001L), result.get(0).getCustomerNumber());
	}

	@Test
	public void testAddCustomer() {
		Customer customer = customerEntity();
		when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(customer);

		ResponseEntity<Object> response = bankingService.addCustomer(customerDetails());

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(customerRepository).save(customer);
	}

	@Test
	public void testFindByCustomerNumberFound() {
		Customer customer = customerEntity();
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));
		when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(customerDetails());

		CustomerDetails result = bankingService.findByCustomerNumber(1001L);

		assertEquals(Long.valueOf(1001L), result.getCustomerNumber());
	}

	@Test
	public void testFindByCustomerNumberNotFound() {
		when(customerRepository.findByCustomerNumber(anyLong())).thenReturn(Optional.empty());

		assertNull(bankingService.findByCustomerNumber(9999L));
	}

	@Test
	public void testUpdateCustomerFoundWithExistingContactAndAddress() {
		Customer managed = customerEntity();
		Customer unmanaged = customerEntity();
		unmanaged.getContactDetails().setEmailId("new@example.com");
		unmanaged.getCustomerAddress().setCity("Dallas");
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managed));
		when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanaged);

		ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails(), 1001L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("new@example.com", managed.getContactDetails().getEmailId());
		assertEquals("Dallas", managed.getCustomerAddress().getCity());
		verify(customerRepository).save(managed);
	}

	@Test
	public void testUpdateCustomerFoundWithNullManagedContactAndAddress() {
		Customer managed = customerEntity();
		managed.setContactDetails(null);
		managed.setCustomerAddress(null);
		Customer unmanaged = customerEntity();
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(managed));
		when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(unmanaged);

		ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails(), 1001L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(unmanaged.getContactDetails(), managed.getContactDetails());
		assertEquals(unmanaged.getCustomerAddress(), managed.getCustomerAddress());
	}

	@Test
	public void testUpdateCustomerNotFound() {
		when(customerRepository.findByCustomerNumber(anyLong())).thenReturn(Optional.empty());
		when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(customerEntity());

		ResponseEntity<Object> response = bankingService.updateCustomer(customerDetails(), 9999L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	public void testDeleteCustomerFound() {
		Customer customer = customerEntity();
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customer));

		ResponseEntity<Object> response = bankingService.deleteCustomer(1001L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(customerRepository).delete(customer);
	}

	@Test
	public void testDeleteCustomerNotFound() {
		when(customerRepository.findByCustomerNumber(anyLong())).thenReturn(Optional.empty());

		ResponseEntity<Object> response = bankingService.deleteCustomer(9999L);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(customerRepository, never()).delete(any(Customer.class));
	}

	@Test
	public void testFindByAccountNumberFound() {
		Account account = Account.builder().accountNumber(5001L).accountBalance(100.0).build();
		when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
		when(bankingServiceHelper.convertToAccountDomain(account))
				.thenReturn(AccountInformation.builder().accountNumber(5001L).build());

		ResponseEntity<Object> response = bankingService.findByAccountNumber(5001L);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
	}

	@Test
	public void testFindByAccountNumberNotFound() {
		when(accountRepository.findByAccountNumber(anyLong())).thenReturn(Optional.empty());

		ResponseEntity<Object> response = bankingService.findByAccountNumber(9999L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testAddNewAccountCustomerFound() {
		Account account = Account.builder().accountNumber(5001L).build();
		AccountInformation accountInformation = AccountInformation.builder().accountNumber(5001L).build();
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customerEntity()));
		when(bankingServiceHelper.convertToAccountEntity(accountInformation)).thenReturn(account);

		ResponseEntity<Object> response = bankingService.addNewAccount(accountInformation, 1001L);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(accountRepository).save(account);
		verify(custAccXRefRepository).save(any(CustomerAccountXRef.class));
	}

	@Test
	public void testAddNewAccountCustomerNotFound() {
		when(customerRepository.findByCustomerNumber(anyLong())).thenReturn(Optional.empty());

		ResponseEntity<Object> response = bankingService.addNewAccount(AccountInformation.builder().build(), 9999L);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(accountRepository, never()).save(any(Account.class));
	}

	private TransferDetails transferDetails(double amount) {
		return new TransferDetails(5001L, 5002L, amount);
	}

	@Test
	public void testTransferDetailsSuccess() {
		Account from = Account.builder().accountNumber(5001L).accountBalance(500.0).build();
		Account to = Account.builder().accountNumber(5002L).accountBalance(50.0).build();
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customerEntity()));
		when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(from));
		when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(to));
		when(bankingServiceHelper.createTransaction(any(TransferDetails.class), eq(5001L), eq("DEBIT")))
				.thenReturn(Transaction.builder().accountNumber(5001L).txType("DEBIT").build());
		when(bankingServiceHelper.createTransaction(any(TransferDetails.class), eq(5002L), eq("CREDIT")))
				.thenReturn(Transaction.builder().accountNumber(5002L).txType("CREDIT").build());

		ResponseEntity<Object> response = bankingService.transferDetails(transferDetails(100.0), 1001L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(Double.valueOf(400.0), from.getAccountBalance());
		assertEquals(Double.valueOf(150.0), to.getAccountBalance());
		verify(accountRepository).saveAll(any());
		verify(transactionRepository, times(2)).save(any(Transaction.class));
	}

	@Test
	public void testTransferDetailsInsufficientFunds() {
		Account from = Account.builder().accountNumber(5001L).accountBalance(10.0).build();
		Account to = Account.builder().accountNumber(5002L).accountBalance(50.0).build();
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customerEntity()));
		when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(from));
		when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.of(to));

		ResponseEntity<Object> response = bankingService.transferDetails(transferDetails(100.0), 1001L);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	public void testTransferDetailsFromAccountNotFound() {
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customerEntity()));
		when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.empty());

		ResponseEntity<Object> response = bankingService.transferDetails(transferDetails(100.0), 1001L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testTransferDetailsToAccountNotFound() {
		Account from = Account.builder().accountNumber(5001L).accountBalance(500.0).build();
		when(customerRepository.findByCustomerNumber(1001L)).thenReturn(Optional.of(customerEntity()));
		when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(from));
		when(accountRepository.findByAccountNumber(5002L)).thenReturn(Optional.empty());

		ResponseEntity<Object> response = bankingService.transferDetails(transferDetails(100.0), 1001L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testTransferDetailsCustomerNotFound() {
		when(customerRepository.findByCustomerNumber(anyLong())).thenReturn(Optional.empty());

		ResponseEntity<Object> response = bankingService.transferDetails(transferDetails(100.0), 9999L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testFindTransactionsByAccountNumberFound() {
		Account account = Account.builder().accountNumber(5001L).build();
		Transaction transaction = Transaction.builder().accountNumber(5001L).txAmount(25.0).txType("DEBIT")
				.txDateTime(new Date()).build();
		when(accountRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(account));
		when(transactionRepository.findByAccountNumber(5001L)).thenReturn(Optional.of(Arrays.asList(transaction)));
		when(bankingServiceHelper.convertToTransactionDomain(transaction))
				.thenReturn(TransactionDetails.builder().accountNumber(5001L).txAmount(25.0).txType("DEBIT").build());

		List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(5001L);

		assertEquals(1, result.size());
		assertEquals("DEBIT", result.get(0).getTxType());
	}

	@Test
	public void testFindTransactionsByAccountNumberAccountNotFound() {
		when(accountRepository.findByAccountNumber(anyLong())).thenReturn(Optional.empty());

		List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(9999L);

		assertEquals(0, result.size());
	}
}
