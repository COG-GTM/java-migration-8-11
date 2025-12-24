package com.coding.exercise.bankapp.service.validation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.coding.exercise.bankapp.model.Account;
import com.coding.exercise.bankapp.repository.AccountRepository;

@Service
public class AccountValidationServiceImpl implements AccountValidationService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public ResponseEntity<Object> validateAccountExists(Long accountNumber, String accountType) {
        Optional<Account> accountEntityOpt = accountRepository.findByAccountNumber(accountNumber);
        if (!accountEntityOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(accountType + " Account Number " + accountNumber + " not found.");
        }
        return ResponseEntity.ok(accountEntityOpt.get());
    }
    
    @Override
    public ResponseEntity<Object> validateSufficientBalance(Account account, Double transferAmount) {
        if (account.getAccountBalance() < transferAmount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds.");
        }
        return ResponseEntity.ok().build();
    }
    
    @Override
    public ResponseEntity<Object> validateTransferAccounts(Long fromAccountNumber, Long toAccountNumber, Double transferAmount) {
        ResponseEntity<Object> fromValidation = validateAccountExists(fromAccountNumber, "From");
        if (fromValidation.getStatusCode() != HttpStatus.OK) {
            return fromValidation;
        }
        
        ResponseEntity<Object> toValidation = validateAccountExists(toAccountNumber, "To");
        if (toValidation.getStatusCode() != HttpStatus.OK) {
            return toValidation;
        }
        
        Account fromAccount = (Account) fromValidation.getBody();
        return validateSufficientBalance(fromAccount, transferAmount);
    }
}
