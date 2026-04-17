package com.coding.exercise.bankapp.exception;

public class InsufficientFundsException extends RuntimeException {

	public InsufficientFundsException() {
		super("Insufficient Funds.");
	}
}
