package com.coding.exercise.bankapp.exception;

public class SelfTransferException extends RuntimeException {

	public SelfTransferException() {
		super("Cannot transfer to the same account.");
	}
}
