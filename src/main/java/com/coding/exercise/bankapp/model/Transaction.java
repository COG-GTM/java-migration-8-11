package com.coding.exercise.bankapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="TX_ID")
	private UUID id;
	
	private Long accountNumber;
	
	@Temporal(TemporalType.TIME)
	private Date txDateTime;
	
	private String txType;
	
	private Double txAmount;
}
