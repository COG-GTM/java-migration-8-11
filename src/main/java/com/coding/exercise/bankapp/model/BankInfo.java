package com.coding.exercise.bankapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BankInfo {

	private String branchName;
	
	private Integer branchCode;
	
	private Address branchAddress;
	
	private Integer routingNumber;
	
}
