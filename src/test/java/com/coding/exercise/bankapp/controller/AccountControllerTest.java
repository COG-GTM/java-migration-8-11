package com.coding.exercise.bankapp.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getByAccountNumber_delegatesToService_andReturnsServiceResponse() throws Exception {
        AccountInformation accountInfo = new AccountInformation();
        accountInfo.setAccountNumber(123L);
        accountInfo.setAccountBalance(1000.0);
        
        when(bankingService.findByAccountNumber(123L))
            .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accountInfo));

        mockMvc.perform(get("/accounts/123"))
            .andExpect(status().isFound());

        verify(bankingService).findByAccountNumber(123L);
    }

    @Test
    void getByAccountNumber_notFound_returns404() throws Exception {
        when(bankingService.findByAccountNumber(999L))
            .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found"));

        mockMvc.perform(get("/accounts/999"))
            .andExpect(status().isNotFound());

        verify(bankingService).findByAccountNumber(999L);
    }

    @Test
    void addNewAccount_delegatesToService_andReturnsCreated() throws Exception {
        AccountInformation accountInfo = new AccountInformation();
        accountInfo.setAccountNumber(456L);
        accountInfo.setAccountType("CHECKING");
        accountInfo.setAccountBalance(500.0);

        when(bankingService.addNewAccount(any(AccountInformation.class), eq(123L)))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("Account created"));

        mockMvc.perform(post("/accounts/add/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountInfo)))
            .andExpect(status().isCreated());

        verify(bankingService).addNewAccount(any(AccountInformation.class), eq(123L));
    }

    @Test
    void transferDetails_delegatesToService_andReturnsOk() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(123L);
        transferDetails.setToAccountNumber(456L);
        transferDetails.setTransferAmount(100.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(123L)))
            .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Transfer successful"));

        mockMvc.perform(put("/accounts/transfer/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
            .andExpect(status().isOk());

        verify(bankingService).transferDetails(any(TransferDetails.class), eq(123L));
    }

    @Test
    void transferDetails_insufficientFunds_returns400() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(123L);
        transferDetails.setToAccountNumber(456L);
        transferDetails.setTransferAmount(10000.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(123L)))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds"));

        mockMvc.perform(put("/accounts/transfer/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
            .andExpect(status().isBadRequest());

        verify(bankingService).transferDetails(any(TransferDetails.class), eq(123L));
    }

    @Test
    void getTransactionByAccountNumber_delegatesToService_andReturnsTransactions() throws Exception {
        TransactionDetails transaction1 = new TransactionDetails();
        transaction1.setAccountNumber(123L);
        transaction1.setTxAmount(100.0);
        transaction1.setTxType("DEBIT");

        TransactionDetails transaction2 = new TransactionDetails();
        transaction2.setAccountNumber(123L);
        transaction2.setTxAmount(50.0);
        transaction2.setTxType("CREDIT");

        List<TransactionDetails> transactions = Arrays.asList(transaction1, transaction2);

        when(bankingService.findTransactionsByAccountNumber(123L)).thenReturn(transactions);

        mockMvc.perform(get("/accounts/transactions/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].accountNumber").value(123))
            .andExpect(jsonPath("$[0].txAmount").value(100.0))
            .andExpect(jsonPath("$[0].txType").value("DEBIT"))
            .andExpect(jsonPath("$[1].accountNumber").value(123))
            .andExpect(jsonPath("$[1].txAmount").value(50.0))
            .andExpect(jsonPath("$[1].txType").value("CREDIT"));

        verify(bankingService).findTransactionsByAccountNumber(123L);
    }

    @Test
    void getTransactionByAccountNumber_emptyList_returnsEmptyArray() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(999L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/accounts/transactions/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        verify(bankingService).findTransactionsByAccountNumber(999L);
    }
}
