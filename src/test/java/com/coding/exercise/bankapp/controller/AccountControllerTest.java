package com.coding.exercise.bankapp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private BankingServiceImpl bankingService;

    @InjectMocks
    private AccountController controller;

    @Test
    @DisplayName("getByAccountNumber delegates to service and returns response")
    void getByAccountNumber() {
        Long accountNumber = 12345L;
        AccountInformation mockAccount = AccountInformation.builder()
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .accountBalance(1000.0)
                .build();
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(mockAccount, HttpStatus.OK);
        
        when(bankingService.findByAccountNumber(accountNumber)).thenReturn(mockResponse);

        ResponseEntity<Object> result = controller.getByAccountNumber(accountNumber);

        assertThat(result).isEqualTo(mockResponse);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockAccount);
        verify(bankingService, times(1)).findByAccountNumber(accountNumber);
        verifyNoMoreInteractions(bankingService);
    }

    @Test
    @DisplayName("addNewAccount delegates to service with account info and customer number")
    void addNewAccount() {
        Long customerNumber = 67890L;
        AccountInformation accountInfo = AccountInformation.builder()
                .accountType("CHECKING")
                .accountBalance(500.0)
                .build();
        AccountInformation createdAccount = AccountInformation.builder()
                .accountNumber(99999L)
                .accountType("CHECKING")
                .accountBalance(500.0)
                .build();
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        
        when(bankingService.addNewAccount(accountInfo, customerNumber)).thenReturn(mockResponse);

        ResponseEntity<Object> result = controller.addNewAccount(accountInfo, customerNumber);

        assertThat(result).isEqualTo(mockResponse);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(bankingService, times(1)).addNewAccount(accountInfo, customerNumber);
        verifyNoMoreInteractions(bankingService);
    }

    @Test
    @DisplayName("transferDetails delegates to service for fund transfer")
    void transferDetails() {
        Long customerNumber = 11111L;
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(12345L);
        transferDetails.setToAccountNumber(67890L);
        transferDetails.setTransferAmount(250.0);
        
        String mockMessage = "Transfer successful";
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(mockMessage, HttpStatus.OK);
        
        when(bankingService.transferDetails(transferDetails, customerNumber)).thenReturn(mockResponse);

        ResponseEntity<Object> result = controller.transferDetails(transferDetails, customerNumber);

        assertThat(result).isEqualTo(mockResponse);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mockMessage);
        verify(bankingService, times(1)).transferDetails(transferDetails, customerNumber);
        verifyNoMoreInteractions(bankingService);
    }

    @Test
    @DisplayName("getTransactionByAccountNumber delegates to service and returns transaction list")
    void getTransactionByAccountNumber() {
        Long accountNumber = 12345L;
        TransactionDetails txn1 = TransactionDetails.builder()
                .txType("DEBIT")
                .txAmount(100.0)
                .build();
        TransactionDetails txn2 = TransactionDetails.builder()
                .txType("CREDIT")
                .txAmount(200.0)
                .build();
        List<TransactionDetails> transactions = Arrays.asList(txn1, txn2);
        
        when(bankingService.findTransactionsByAccountNumber(accountNumber)).thenReturn(transactions);

        List<TransactionDetails> result = controller.getTransactionByAccountNumber(accountNumber);

        assertThat(result).isEqualTo(transactions);
        assertThat(result).hasSize(2);
        verify(bankingService, times(1)).findTransactionsByAccountNumber(accountNumber);
        verifyNoMoreInteractions(bankingService);
    }
}
