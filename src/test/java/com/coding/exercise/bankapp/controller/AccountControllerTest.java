package com.coding.exercise.bankapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

	@Mock
	private BankingServiceImpl bankingService;

	private AccountController controller;

	@BeforeEach
	public void setUp() {
		controller = new AccountController();
		ReflectionTestUtils.setField(controller, "bankingService", bankingService);
	}

	@Test
	public void testGetByAccountNumber() {
		when(bankingService.findByAccountNumber(5001L)).thenReturn(
				ResponseEntity.status(HttpStatus.FOUND).body(AccountInformation.builder().accountNumber(5001L).build()));

		ResponseEntity<Object> response = controller.getByAccountNumber(5001L);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
	}

	@Test
	public void testAddNewAccount() {
		AccountInformation accountInformation = AccountInformation.builder().accountNumber(5001L).build();
		when(bankingService.addNewAccount(accountInformation, 1001L))
				.thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

		ResponseEntity<Object> response = controller.addNewAccount(accountInformation, 1001L);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	public void testTransferDetails() {
		TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 100.0);
		when(bankingService.transferDetails(transferDetails, 1001L)).thenReturn(
				ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 1001"));

		ResponseEntity<Object> response = controller.transferDetails(transferDetails, 1001L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testGetTransactionByAccountNumber() {
		TransactionDetails transactionDetails = TransactionDetails.builder().accountNumber(5001L).txType("DEBIT").build();
		when(bankingService.findTransactionsByAccountNumber(5001L)).thenReturn(Arrays.asList(transactionDetails));

		List<TransactionDetails> result = controller.getTransactionByAccountNumber(5001L);

		assertEquals(1, result.size());
		assertEquals("DEBIT", result.get(0).getTxType());
	}
}
