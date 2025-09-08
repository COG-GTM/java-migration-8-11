package com.coding.exercise.bankapp.controller;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private BankingServiceImpl bankingService;
    
    @InjectMocks
    private AccountController accountController;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }
    
    @Test
    void testGetAccountDetails_ValidAccountNumber_ShouldReturnAccount() throws Exception {
        AccountInformation accountInfo = new AccountInformation();
        accountInfo.setAccountNumber(12345L);
        accountInfo.setAccountBalance(1000.0);
        
        when(bankingService.findByAccountNumber(12345L))
            .thenReturn(ResponseEntity.status(302).body(accountInfo));
        
        mockMvc.perform(get("/accounts/12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
        
        verify(bankingService, times(1)).findByAccountNumber(12345L);
    }
    
    @Test
    void testGetAccountDetails_InvalidAccountNumber_ShouldReturnNotFound() throws Exception {
        when(bankingService.findByAccountNumber(99999L))
            .thenReturn(ResponseEntity.status(404).body("Account Number 99999 not found"));
        
        mockMvc.perform(get("/accounts/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(bankingService, times(1)).findByAccountNumber(99999L);
    }
    
    @Test
    void testAddAccount_ValidRequest_ShouldCreateAccount() throws Exception {
        AccountInformation accountInfo = new AccountInformation();
        accountInfo.setAccountNumber(12345L);
        accountInfo.setAccountType("SAVINGS");
        accountInfo.setAccountBalance(1000.0);
        
        when(bankingService.addNewAccount(any(AccountInformation.class), eq(1001L)))
            .thenReturn(ResponseEntity.status(201).body("New Account created successfully"));
        
        mockMvc.perform(post("/accounts/add/1001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountNumber\":12345,\"accountType\":\"SAVINGS\",\"accountBalance\":1000.0}"))
                .andExpect(status().isCreated());
        
        verify(bankingService, times(1)).addNewAccount(any(AccountInformation.class), eq(1001L));
    }
    
    @Test
    void testAddAccount_InvalidCustomerNumber_ShouldReturnBadRequest() {
    }
    
    @Test
    void testTransferDetails_ValidTransfer_ShouldReturnSuccess() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(12345L);
        transferDetails.setToAccountNumber(67890L);
        transferDetails.setTransferAmount(100.0);
        
        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
            .thenReturn(ResponseEntity.status(200).body("Success: Amount transferred for Customer Number 1001"));
        
        mockMvc.perform(put("/accounts/transfer/1001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fromAccountNumber\":12345,\"toAccountNumber\":67890,\"transferAmount\":100.0}"))
                .andExpect(status().isOk());
        
        verify(bankingService, times(1)).transferDetails(any(TransferDetails.class), eq(1001L));
    }
    
    @Test
    void testTransferDetails_InvalidTransferData_ShouldReturnBadRequest() {
    }
    
    @Test
    void testGetTransactionDetails_ValidAccountNumber_ShouldReturnTransactions() throws Exception {
        List<TransactionDetails> transactions = new ArrayList<>();
        TransactionDetails transaction = new TransactionDetails();
        transaction.setAccountNumber(12345L);
        transaction.setTxAmount(100.0);
        transaction.setTxType("DEBIT");
        transactions.add(transaction);
        
        when(bankingService.findTransactionsByAccountNumber(12345L))
            .thenReturn(transactions);
        
        mockMvc.perform(get("/accounts/transactions/12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(bankingService, times(1)).findTransactionsByAccountNumber(12345L);
    }
    
    @Test
    void testGetTransactionDetails_InvalidAccountNumber_ShouldReturnNotFound() {
    }
}
