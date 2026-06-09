package com.coding.exercise.bankapp.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Account {

	@Id
	private String id;
	
	private Long accountNumber;
	
	private BankInfo bankInformation;
	
	private String accountStatus;
	
	private String accountType;
	
	private Double accountBalance;
    
	private Date createDateTime;
	
	private Date updateDateTime;
}
