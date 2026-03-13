package com.coding.exercise.bankapp.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.BankInformation;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountInformation createSampleAccountInfo() {
        return AccountInformation.builder()
                .accountNumber(100001L)
                .bankInformation(BankInformation.builder()
                        .branchName("Main Branch")
                        .branchCode(1001)
                        .routingNumber(123456789)
                        .branchAddress(AddressDetails.builder()
                                .address1("456 Bank St")
                                .city("Springfield")
                                .state("IL")
                                .zip("62701")
                                .country("US")
                                .build())
                        .build())
                .accountStatus("Active")
                .accountType("SAVINGS")
                .accountBalance(5000.0)
                .build();
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetByAccountNumber_Found() throws Exception {
        AccountInformation accountInfo = createSampleAccountInfo();
        when(bankingService.findByAccountNumber(100001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accountInfo));

        mockMvc.perform(get("/accounts/100001"))
                .andExpect(status().isFound());

        verify(bankingService).findByAccountNumber(100001L);
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetByAccountNumber_NotFound() throws Exception {
        when(bankingService.findByAccountNumber(99999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 99999 not found."));

        mockMvc.perform(get("/accounts/99999"))
                .andExpect(status().isNotFound());

        verify(bankingService).findByAccountNumber(99999L);
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testAddNewAccount_Success() throws Exception {
        AccountInformation accountInfo = createSampleAccountInfo();
        when(bankingService.addNewAccount(any(AccountInformation.class), eq(12345L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountInfo)))
                .andExpect(status().isCreated());

        verify(bankingService).addNewAccount(any(AccountInformation.class), eq(12345L));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testTransferDetails_Success() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(100002L);
        transferDetails.setTransferAmount(500.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(12345L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 12345"));

        mockMvc.perform(put("/accounts/transfer/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isOk());

        verify(bankingService).transferDetails(any(TransferDetails.class), eq(12345L));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testTransferDetails_InsufficientFunds() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(100002L);
        transferDetails.setTransferAmount(50000.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(12345L)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isBadRequest());

        verify(bankingService).transferDetails(any(TransferDetails.class), eq(12345L));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testTransferDetails_CustomerNotFound() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(100001L);
        transferDetails.setToAccountNumber(100002L);
        transferDetails.setTransferAmount(500.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(99999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 99999 not found."));

        mockMvc.perform(put("/accounts/transfer/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetTransactionByAccountNumber_WithTransactions() throws Exception {
        TransactionDetails tx = TransactionDetails.builder()
                .accountNumber(100001L)
                .txType("CREDIT")
                .txAmount(1000.0)
                .txDateTime(new Date())
                .build();

        when(bankingService.findTransactionsByAccountNumber(100001L))
                .thenReturn(Arrays.asList(tx));

        mockMvc.perform(get("/accounts/transactions/100001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].txType", is("CREDIT")))
                .andExpect(jsonPath("$[0].txAmount", is(1000.0)));

        verify(bankingService).findTransactionsByAccountNumber(100001L);
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetTransactionByAccountNumber_Empty() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(100001L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/transactions/100001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
