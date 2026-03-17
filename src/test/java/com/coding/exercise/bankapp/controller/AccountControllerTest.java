package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.BankInformation;
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

    private AccountInformation createAccountInformation() {
        return AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountBalance(10000.0)
                .accountStatus("Active")
                .bankInformation(BankInformation.builder()
                        .branchName("Main").branchCode(100).routingNumber(999)
                        .branchAddress(AddressDetails.builder()
                                .address1("456 Bank Ave").city("Chicago").state("IL").zip("60601").country("US")
                                .build())
                        .build())
                .build();
    }

    @Test
    @WithMockUser
    void getByAccountNumber_shouldReturnAccount() throws Exception {
        AccountInformation accountInfo = createAccountInformation();
        when(bankingService.findByAccountNumber(5001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accountInfo));

        mockMvc.perform(get("/accounts/5001"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser
    void getByAccountNumber_shouldReturnNotFound() throws Exception {
        when(bankingService.findByAccountNumber(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 9999 not found."));

        mockMvc.perform(get("/accounts/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void addNewAccount_shouldReturnCreated() throws Exception {
        AccountInformation accountInfo = createAccountInformation();
        when(bankingService.addNewAccount(any(AccountInformation.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountInfo)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void transferDetails_shouldReturnOk() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(500.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.ok("Success: Amount transferred for Customer Number 1001"));

        mockMvc.perform(put("/accounts/transfer/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void transferDetails_shouldReturnBadRequestForInsufficientFunds() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(999999.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getTransactionByAccountNumber_shouldReturnTransactions() throws Exception {
        TransactionDetails txDetails = TransactionDetails.builder()
                .accountNumber(5001L).txAmount(500.0).txType("DEBIT").txDateTime(new Date())
                .build();
        when(bankingService.findTransactionsByAccountNumber(5001L))
                .thenReturn(Arrays.asList(txDetails));

        mockMvc.perform(get("/accounts/transactions/5001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].txAmount").value(500.0));
    }

    @Test
    @WithMockUser
    void getTransactionByAccountNumber_shouldReturnEmptyList() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(9999L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/transactions/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

}
