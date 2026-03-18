package com.coding.exercise.bankapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== GET /accounts/{accountNumber} ==========

    @Test
    void getByAccountNumber_shouldReturnFoundWhenAccountExists() throws Exception {
        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(100L).accountType("SAVINGS").accountBalance(5000.0).accountStatus("ACTIVE").build();

        when(bankingService.findByAccountNumber(100L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accInfo));

        mockMvc.perform(get("/accounts/100"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.accountNumber").value(100))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"));
    }

    @Test
    void getByAccountNumber_shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        when(bankingService.findByAccountNumber(999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 999 not found."));

        mockMvc.perform(get("/accounts/999"))
                .andExpect(status().isNotFound());
    }

    // ========== POST /accounts/add/{customerNumber} ==========

    @Test
    void addNewAccount_shouldReturnCreated() throws Exception {
        AccountInformation accInfo = AccountInformation.builder()
                .accountNumber(100L).accountType("CHECKING").accountBalance(1000.0).accountStatus("ACTIVE").build();

        when(bankingService.addNewAccount(any(AccountInformation.class), eq(1L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accInfo)))
                .andExpect(status().isCreated());
    }

    // ========== PUT /accounts/transfer/{customerNumber} ==========

    @Test
    void transferDetails_shouldReturnOkOnSuccess() throws Exception {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(250.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 1"));

        mockMvc.perform(put("/accounts/transfer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isOk());
    }

    @Test
    void transferDetails_shouldReturnNotFoundWhenCustomerNotFound() throws Exception {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(250.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 999 not found."));

        mockMvc.perform(put("/accounts/transfer/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isNotFound());
    }

    @Test
    void transferDetails_shouldReturnBadRequestWhenInsufficientFunds() throws Exception {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100L);
        transfer.setToAccountNumber(200L);
        transfer.setTransferAmount(99999.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1L)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isBadRequest());
    }

    // ========== GET /accounts/transactions/{accountNumber} ==========

    @Test
    void getTransactionByAccountNumber_shouldReturnTransactions() throws Exception {
        TransactionDetails td1 = TransactionDetails.builder()
                .accountNumber(100L).txAmount(100.0).txType("DEBIT").build();
        TransactionDetails td2 = TransactionDetails.builder()
                .accountNumber(100L).txAmount(200.0).txType("CREDIT").build();

        when(bankingService.findTransactionsByAccountNumber(100L))
                .thenReturn(Arrays.asList(td1, td2));

        mockMvc.perform(get("/accounts/transactions/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].txType").value("DEBIT"))
                .andExpect(jsonPath("$[1].txType").value("CREDIT"));
    }

    @Test
    void getTransactionByAccountNumber_shouldReturnEmptyList() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(100L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/transactions/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
