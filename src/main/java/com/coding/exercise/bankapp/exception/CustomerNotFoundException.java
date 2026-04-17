package com.coding.exercise.bankapp.exception;

public class CustomerNotFoundException extends RuntimeException {

	public CustomerNotFoundException(Long customerNumber) {
		super("Customer Number " + customerNumber + " not found.");
	}
}
