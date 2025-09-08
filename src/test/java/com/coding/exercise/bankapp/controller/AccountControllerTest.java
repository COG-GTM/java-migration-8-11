package com.coding.exercise.bankapp.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

class AccountControllerTest {

    @Mock
    private BankingServiceImpl bankingService;
    
    @InjectMocks
    private AccountController accountController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    void getByAccountNumber_ShouldDelegateToService() {
        Long accountNumber = 12345L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Account found");
        
        when(bankingService.findByAccountNumber(accountNumber)).thenReturn(expectedResponse);
        
        ResponseEntity<Object> result = accountController.getByAccountNumber(accountNumber);
        
        assertEquals(expectedResponse, result);
        verify(bankingService).findByAccountNumber(accountNumber);
    }
    
    @Test
    void addNewAccount_ShouldDelegateToService() {
        Long customerNumber = 123L;
        AccountInformation accountInfo = new AccountInformation();
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("Account created");
        
        when(bankingService.addNewAccount(accountInfo, customerNumber)).thenReturn(expectedResponse);
        
        ResponseEntity<Object> result = accountController.addNewAccount(accountInfo, customerNumber);
        
        assertEquals(expectedResponse, result);
        verify(bankingService).addNewAccount(accountInfo, customerNumber);
    }
}
