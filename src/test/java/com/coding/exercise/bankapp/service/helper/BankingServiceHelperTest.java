package com.coding.exercise.bankapp.service.helper;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.model.Transaction;

public class BankingServiceHelperTest {

	private final BankingServiceHelper helper = new BankingServiceHelper();

	@Test
	public void createTransaction_setsExpectedFields() {
		TransferDetails transferDetails = new TransferDetails(1L, 2L, 10.0);
		LocalDateTime before = LocalDateTime.now();

		Transaction tx = helper.createTransaction(transferDetails, 123L, "DEBIT");

		assertNotNull(tx);
		assertEquals(Long.valueOf(123L), tx.getAccountNumber());
		assertEquals(Double.valueOf(10.0), tx.getTxAmount());
		assertEquals("DEBIT", tx.getTxType());
		assertNotNull(tx.getTxDateTime());
		assertTrue(!tx.getTxDateTime().isBefore(before));
	}
}
