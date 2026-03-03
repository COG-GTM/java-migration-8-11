package com.coding.exercise.bankapp.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.model.Account;
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

	@Before
	public void setUp() {
		bankingService = new BankingServiceImpl(customerRepository);
		ReflectionTestUtils.setField(bankingService, "accountRepository", accountRepository);
		ReflectionTestUtils.setField(bankingService, "transactionRepository", transactionRepository);
		ReflectionTestUtils.setField(bankingService, "custAccXRefRepository", custAccXRefRepository);
		ReflectionTestUtils.setField(bankingService, "bankingServiceHelper", bankingServiceHelper);
	}

	@Test
	public void findAll_convertsEntitiesToDomain() {
		Customer customer = Customer.builder().firstName("A").build();
		when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

		CustomerDetails details = CustomerDetails.builder().firstName("A").build();
		when(bankingServiceHelper.convertToCustomerDomain(customer)).thenReturn(details);

		List<CustomerDetails> result = bankingService.findAll();

		assertEquals(1, result.size());
		assertEquals("A", result.get(0).getFirstName());
		verify(bankingServiceHelper).convertToCustomerDomain(customer);
	}

	@Test
	public void findByCustomerNumber_returnsNullWhenMissing() {
		when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.empty());
		assertNull(bankingService.findByCustomerNumber(1L));
	}

	@Test
	public void deleteCustomer_returnsBadRequestWhenMissing() {
		when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.empty());

		ResponseEntity<Object> response = bankingService.deleteCustomer(1L);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(customerRepository, never()).delete(any(Customer.class));
	}

	@Test
	public void updateCustomer_updatesNamesAndTimestamp() {
		Customer managed = Customer.builder().firstName("Old").build();
		when(customerRepository.findByCustomerNumber(1L)).thenReturn(Optional.of(managed));

		Customer incoming = Customer.builder().firstName("New").lastName("Last").middleName("Mid").status("ACTIVE").build();
		when(bankingServiceHelper.convertToCustomerEntity(any(CustomerDetails.class))).thenReturn(incoming);

		ResponseEntity<Object> response = bankingService.updateCustomer(CustomerDetails.builder().firstName("New").build(), 1L);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
		verify(customerRepository).save(captor.capture());
		Customer saved = captor.getValue();

		assertEquals("New", saved.getFirstName());
		assertEquals("Last", saved.getLastName());
		assertEquals("Mid", saved.getMiddleName());
		assertEquals("ACTIVE", saved.getStatus());
		assertNotNull(saved.getUpdateDateTime());
		assertTrue(!saved.getUpdateDateTime().isAfter(LocalDateTime.now()));
	}

	@Test
	public void findTransactionsByAccountNumber_returnsEmptyWhenNoAccount() {
		when(accountRepository.findByAccountNumber(1L)).thenReturn(Optional.empty());
		List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1L);
		assertTrue(result.isEmpty());
	}

	@Test
	public void findTransactionsByAccountNumber_convertsTransactions_happyPath() {
		when(accountRepository.findByAccountNumber(1L)).thenReturn(Optional.of(mock(Account.class)));

		Transaction t1 = Transaction.builder().txType("DEBIT").build();
		Transaction t2 = Transaction.builder().txType("CREDIT").build();
		when(transactionRepository.findByAccountNumber(1L)).thenReturn(Optional.of(Arrays.asList(t1, t2)));

		TransactionDetails d1 = TransactionDetails.builder().txType("DEBIT").build();
		TransactionDetails d2 = TransactionDetails.builder().txType("CREDIT").build();
		when(bankingServiceHelper.convertToTransactionDomain(t1)).thenReturn(d1);
		when(bankingServiceHelper.convertToTransactionDomain(t2)).thenReturn(d2);

		List<TransactionDetails> result = bankingService.findTransactionsByAccountNumber(1L);

		assertEquals(2, result.size());
		assertEquals("DEBIT", result.get(0).getTxType());
		assertEquals("CREDIT", result.get(1).getTxType());
	}
}
