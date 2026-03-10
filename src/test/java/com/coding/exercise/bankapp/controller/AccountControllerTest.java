package com.coding.exercise.bankapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
@WithMockUser
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountInformation buildAccountInfo() {
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
                                .city("Springfield")
                                .state("IL")
                                .zip("62701")
                                .country("US")
                                .build())
                        .build())
                .build();
    }

    @Test
    public void getByAccountNumber_found() throws Exception {
        AccountInformation accountInfo = buildAccountInfo();
        when(bankingService.findByAccountNumber(5001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accountInfo));

        mockMvc.perform(get("/accounts/{accountNumber}", 5001L))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.accountNumber", is(5001)))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")));

        verify(bankingService).findByAccountNumber(5001L);
    }

    @Test
    public void getByAccountNumber_notFound() throws Exception {
        when(bankingService.findByAccountNumber(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 9999 not found."));

        mockMvc.perform(get("/accounts/{accountNumber}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account Number 9999 not found."));
    }

    @Test
    public void addNewAccount_success() throws Exception {
        AccountInformation accountInfo = buildAccountInfo();
        when(bankingService.addNewAccount(any(AccountInformation.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/{customerNumber}", 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountInfo)))
                .andExpect(status().isCreated())
                .andExpect(content().string("New Account created successfully."));

        verify(bankingService).addNewAccount(any(AccountInformation.class), eq(1001L));
    }

    @Test
    public void transferDetails_success() throws Exception {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(100.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 1001"));

        mockMvc.perform(put("/accounts/transfer/{customerNumber}", 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Amount transferred for Customer Number 1001"));

        verify(bankingService).transferDetails(any(TransferDetails.class), eq(1001L));
    }

    @Test
    public void transferDetails_customerNotFound() throws Exception {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(100.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(9999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 9999 not found."));

        mockMvc.perform(put("/accounts/transfer/{customerNumber}", 9999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer Number 9999 not found."));
    }

    @Test
    public void transferDetails_insufficientFunds() throws Exception {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(5001L);
        transfer.setToAccountNumber(5002L);
        transfer.setTransferAmount(999999.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/{customerNumber}", 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient Funds."));
    }

    @Test
    public void getTransactionByAccountNumber_returnsList() throws Exception {
        TransactionDetails t1 = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("DEBIT")
                .txAmount(100.0)
                .txDateTime(new Date())
                .build();
        TransactionDetails t2 = TransactionDetails.builder()
                .accountNumber(5001L)
                .txType("CREDIT")
                .txAmount(200.0)
                .txDateTime(new Date())
                .build();

        when(bankingService.findTransactionsByAccountNumber(5001L))
                .thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(get("/accounts/transactions/{accountNumber}", 5001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].txType", is("DEBIT")))
                .andExpect(jsonPath("$[1].txType", is("CREDIT")));

        verify(bankingService).findTransactionsByAccountNumber(5001L);
    }

    @Test
    public void getTransactionByAccountNumber_emptyList() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(5001L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/transactions/{accountNumber}", 5001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
