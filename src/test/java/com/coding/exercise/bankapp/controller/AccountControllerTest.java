package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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

    private AccountInformation buildAccountInformation() {
        return AccountInformation.builder()
                .accountNumber(5001L).accountStatus("ACTIVE")
                .accountType("SAVINGS").accountBalance(1000.0)
                .bankInformation(BankInformation.builder()
                        .branchName("Main").branchCode(1).routingNumber(100)
                        .branchAddress(AddressDetails.builder()
                                .address1("x").city("y").state("z").zip("0").country("US").build())
                        .build())
                .build();
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void getByAccountNumber_returnsAccountDetails() throws Exception {
        AccountInformation accInfo = buildAccountInformation();
        when(bankingService.findByAccountNumber(5001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accInfo));

        mockMvc.perform(get("/accounts/5001"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void getByAccountNumber_notFound() throws Exception {
        when(bankingService.findByAccountNumber(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 9999 not found."));

        mockMvc.perform(get("/accounts/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void addNewAccount_returns201() throws Exception {
        AccountInformation accInfo = buildAccountInformation();
        when(bankingService.addNewAccount(any(AccountInformation.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accInfo)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void transferDetails_returnsOk() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(200.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 1001"));

        mockMvc.perform(put("/accounts/transfer/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void transferDetails_insufficientFunds() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(5001L);
        transferDetails.setToAccountNumber(5002L);
        transferDetails.setTransferAmount(99999.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void getTransactionByAccountNumber_returnsList() throws Exception {
        TransactionDetails td1 = TransactionDetails.builder()
                .accountNumber(5001L).txAmount(100.0).txType("CREDIT").txDateTime(new Date())
                .build();
        TransactionDetails td2 = TransactionDetails.builder()
                .accountNumber(5001L).txAmount(50.0).txType("DEBIT").txDateTime(new Date())
                .build();

        when(bankingService.findTransactionsByAccountNumber(5001L))
                .thenReturn(Arrays.asList(td1, td2));

        mockMvc.perform(get("/accounts/transactions/5001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void getTransactionByAccountNumber_emptyList() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(9999L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/transactions/9999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
