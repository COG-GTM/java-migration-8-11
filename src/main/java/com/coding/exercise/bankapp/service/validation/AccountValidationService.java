package com.coding.exercise.bankapp.service.validation;

import org.springframework.http.ResponseEntity;
import com.coding.exercise.bankapp.model.Account;

public interface AccountValidationService {
    
    ResponseEntity<Object> validateAccountExists(Long accountNumber, String accountType);
    
    ResponseEntity<Object> validateSufficientBalance(Account account, Double transferAmount);
    
    ResponseEntity<Object> validateTransferAccounts(Long fromAccountNumber, Long toAccountNumber, Double transferAmount);
}
