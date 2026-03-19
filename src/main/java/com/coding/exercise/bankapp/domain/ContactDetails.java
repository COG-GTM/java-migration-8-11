package com.coding.exercise.bankapp.domain;

import javax.validation.constraints.Email;

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
public class ContactDetails {

	@Email(message = "Email must be a valid email address")
	private String emailId;
	
	private String homePhone;
	
	private String workPhone;
}
