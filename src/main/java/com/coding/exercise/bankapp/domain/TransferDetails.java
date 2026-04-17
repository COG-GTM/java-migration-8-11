package com.coding.exercise.bankapp.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransferDetails {

	@NotNull(message = "From account number is required")
	private Long fromAccountNumber;

	@NotNull(message = "To account number is required")
	private Long toAccountNumber;

	@NotNull(message = "Transfer amount is required")
	@Positive(message = "Transfer amount must be positive")
	private Double transferAmount;
}
