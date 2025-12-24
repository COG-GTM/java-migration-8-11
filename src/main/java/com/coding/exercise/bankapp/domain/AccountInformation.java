package com.coding.exercise.bankapp.domain;

import java.util.Date;

import com.coding.exercise.bankapp.model.AccountStatus;

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
	
	private AccountStatus accountStatus;
	
	private String accountType;
	
	private Double accountBalance;
	
	private Date accountCreated;
}
