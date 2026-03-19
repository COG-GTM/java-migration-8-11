package com.coding.exercise.bankapp.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransactionDetails {

	@NotNull(message = "Account number is required")
	private Long accountNumber;
	
	private Date txDateTime;
	
	@NotNull(message = "Transaction type is required")
	private String txType;
	
	@NotNull(message = "Transaction amount is required")
	@Positive(message = "Transaction amount must be positive")
	private Double txAmount;
}
