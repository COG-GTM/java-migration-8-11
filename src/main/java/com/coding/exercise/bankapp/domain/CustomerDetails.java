package com.coding.exercise.bankapp.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
public class CustomerDetails {

    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private String middleName;
    
    private Long customerNumber;
    
    private String status;
    
    @Valid
    private AddressDetails customerAddress;
    
    @Valid
    private ContactDetails contactDetails;
    
}
