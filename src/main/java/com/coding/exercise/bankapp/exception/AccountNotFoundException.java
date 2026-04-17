package com.coding.exercise.bankapp.exception;

public class AccountNotFoundException extends RuntimeException {

	public AccountNotFoundException(Long accountNumber) {
		super("Account Number " + accountNumber + " not found.");
	}
}
