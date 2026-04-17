package com.coding.exercise.bankapp.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;

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

	@NotNull(message = "Account number is required")
	private Long accountNumber;

	private BankInformation bankInformation;

	private String accountStatus;

	private String accountType;

	private Double accountBalance;

	private Date accountCreated;
}
