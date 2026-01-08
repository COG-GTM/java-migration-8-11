package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
@WithMockUser(username = "testuser", password = "testpass")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountInformation testAccountInfo;
    private TransactionDetails testTransactionDetails;

    @BeforeEach
    void setUp() {
        AddressDetails branchAddressDetails = AddressDetails.builder()
                .address1("789 Bank St")
                .city("Chicago")
                .state("IL")
                .zip("60601")
                .country("USA")
                .build();

        BankInformation bankInformation = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(123456789)
                .branchAddress(branchAddressDetails)
                .build();

        testAccountInfo = AccountInformation.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInformation)
                .build();

        testTransactionDetails = TransactionDetails.builder()
                .accountNumber(1000001L)
                .txDateTime(new Date())
                .txType("DEBIT")
                .txAmount(100.00)
                .build();
    }

    @Test
    void testGetByAccountNumber_Found() throws Exception {
        when(bankingService.findByAccountNumber(1000001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(testAccountInfo));

        mockMvc.perform(get("/accounts/1000001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void testGetByAccountNumber_NotFound() throws Exception {
        when(bankingService.findByAccountNumber(99999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 99999 not found."));

        mockMvc.perform(get("/accounts/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddNewAccount_Success() throws Exception {
        when(bankingService.addNewAccount(any(AccountInformation.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAccountInfo)))
                .andExpect(status().isCreated())
                .andExpect(content().string("New Account created successfully."));
    }

    @Test
    void testTransferDetails_Success() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(100.00);

        when(bankingService.transferDetails(any(TransferDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 12345"));

        mockMvc.perform(put("/accounts/transfer/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Amount transferred for Customer Number 12345"));
    }

    @Test
    void testTransferDetails_InsufficientFunds() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(10000.00);

        when(bankingService.transferDetails(any(TransferDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient Funds."));
    }

    @Test
    void testGetTransactionByAccountNumber() throws Exception {
        List<TransactionDetails> transactions = Arrays.asList(testTransactionDetails);
        when(bankingService.findTransactionsByAccountNumber(1000001L)).thenReturn(transactions);

        mockMvc.perform(get("/accounts/transactions/1000001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(1000001L))
                .andExpect(jsonPath("$[0].txType").value("DEBIT"))
                .andExpect(jsonPath("$[0].txAmount").value(100.00));
    }

    @Test
    void testGetTransactionByAccountNumber_Empty() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(99999L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/accounts/transactions/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
