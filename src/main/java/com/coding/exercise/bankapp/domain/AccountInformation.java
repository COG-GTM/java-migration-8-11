package com.coding.exercise.bankapp.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

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
public class AccountInformation {

	private Long accountNumber;
	
	private BankInformation bankInformation;
	
	private String accountStatus;
	
	@NotNull(message = "Account type is required")
	private String accountType;
	
	@PositiveOrZero(message = "Account balance must be zero or positive")
	private Double accountBalance;
	
	private Date accountCreated;
}
