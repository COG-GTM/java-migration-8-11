package com.coding.exercise.bankapp.domain;

import java.time.LocalDateTime;

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

	private Long accountNumber;
	
	private LocalDateTime txDateTime;
	
	private String txType;
	
	private Double txAmount;
}
