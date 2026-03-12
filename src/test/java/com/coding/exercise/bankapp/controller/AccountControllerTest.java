package com.coding.exercise.bankapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private AccountInformation buildAccountInformation() {
        return AccountInformation.builder()
                .accountNumber(5001L)
                .accountType("SAVINGS")
                .accountStatus("Active")
                .accountBalance(1000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("Main Branch")
                        .branchCode(101)
                        .routingNumber(12345)
                        .branchAddress(AddressDetails.builder()
                                .address1("100 Bank St")
                                .city("Chicago")
                                .state("IL")
                                .zip("60601")
                                .country("US")
                                .build())
                        .build())
                .build();
    }

    @Test
    @WithMockUser
    void getByAccountNumber_returnsAccount() throws Exception {
        AccountInformation account = buildAccountInformation();
        when(bankingService.findByAccountNumber(5001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(account));

        mockMvc.perform(get("/accounts/{accountNumber}", 5001L))
                .andExpect(status().isFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber", is(5001)))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")))
                .andExpect(jsonPath("$.accountBalance", is(1000.0)));
    }

    @Test
    @WithMockUser
    void getByAccountNumber_returnsNotFoundForUnknownId() throws Exception {
        when(bankingService.findByAccountNumber(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 9999 not found."));

        mockMvc.perform(get("/accounts/{accountNumber}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account Number 9999 not found."));
    }

    @Test
    @WithMockUser
    void addNewAccount_createsAccount() throws Exception {
        AccountInformation account = buildAccountInformation();
        when(bankingService.addNewAccount(any(AccountInformation.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/{customerNumber}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(content().string("New Account created successfully."));
    }

    @Test
    @WithMockUser
    void transferDetails_transfersFunds() throws Exception {
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 200.0);
        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body("Success: Amount transferred for Customer Number 1001"));

        mockMvc.perform(put("/accounts/transfer/{customerNumber}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Amount transferred for Customer Number 1001"));
    }

    @Test
    @WithMockUser
    void transferDetails_returnsNotFoundForUnknownCustomer() throws Exception {
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 200.0);
        when(bankingService.transferDetails(any(TransferDetails.class), eq(9999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Customer Number 9999 not found."));

        mockMvc.perform(put("/accounts/transfer/{customerNumber}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer Number 9999 not found."));
    }

    @Test
    @WithMockUser
    void transferDetails_returnsBadRequestForInsufficientFunds() throws Exception {
        TransferDetails transferDetails = new TransferDetails(5001L, 5002L, 99999.0);
        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/{customerNumber}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient Funds."));
    }

    @Test
    @WithMockUser
    void getTransactionByAccountNumber_returnsTransactions() throws Exception {
        TransactionDetails tx1 = TransactionDetails.builder()
                .accountNumber(5001L).txType("CREDIT").txAmount(500.0).txDateTime(new Date()).build();
        TransactionDetails tx2 = TransactionDetails.builder()
                .accountNumber(5001L).txType("DEBIT").txAmount(200.0).txDateTime(new Date()).build();

        when(bankingService.findTransactionsByAccountNumber(5001L))
                .thenReturn(Arrays.asList(tx1, tx2));

        mockMvc.perform(get("/accounts/transactions/{accountNumber}", 5001L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].txType", is("CREDIT")))
                .andExpect(jsonPath("$[1].txType", is("DEBIT")));
    }

    @Test
    @WithMockUser
    void getTransactionByAccountNumber_returnsEmptyListForUnknownAccount() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(9999L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/transactions/{accountNumber}", 9999L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
