package com.coding.exercise.bankapp.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.BankInformation;
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

    private AccountInformation buildSampleAccountInfo() {
        AddressDetails address = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Suite 100")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        BankInformation bankInfo = BankInformation.builder()
                .branchName("Main Branch")
                .branchCode(1001)
                .routingNumber(12345)
                .branchAddress(address)
                .build();

        return AccountInformation.builder()
                .accountNumber(1001L)
                .accountType("SAVINGS")
                .accountStatus("ACTIVE")
                .accountBalance(5000.0)
                .bankInformation(bankInfo)
                .build();
    }

    @Test
    void getByAccountNumber_found_returns200WithAccountData() throws Exception {
        AccountInformation accountInfo = buildSampleAccountInfo();
        when(bankingService.findByAccountNumber(1001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accountInfo));

        mockMvc.perform(get("/accounts/1001"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.accountNumber").value(1001))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.accountBalance").value(5000.0))
                .andExpect(jsonPath("$.bankInformation.branchName").value("Main Branch"))
                .andExpect(jsonPath("$.bankInformation.branchCode").value(1001))
                .andExpect(jsonPath("$.bankInformation.routingNumber").value(12345))
                .andExpect(jsonPath("$.bankInformation.branchAddress.city").value("New York"));
    }

    @Test
    void getByAccountNumber_notFound_returns404() throws Exception {
        when(bankingService.findByAccountNumber(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 9999 not found."));

        mockMvc.perform(get("/accounts/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account Number 9999 not found."));
    }

    @Test
    void addNewAccount_validInput_returns201() throws Exception {
        AccountInformation accountInfo = buildSampleAccountInfo();
        when(bankingService.addNewAccount(any(AccountInformation.class), eq(100L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountInfo)))
                .andExpect(status().isCreated())
                .andExpect(content().string("New Account created successfully."));
    }

    @Test
    void addNewAccount_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/accounts/add/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferDetails_validTransfer_returns200() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(500.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(100L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 100"));

        mockMvc.perform(put("/accounts/transfer/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Amount transferred for Customer Number 100"));
    }

    @Test
    void transferDetails_insufficientFunds_returns400() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(999999.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(100L)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient Funds."));
    }

    @Test
    void transferDetails_customerNotFound_returns404() throws Exception {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1001L);
        transferDetails.setToAccountNumber(1002L);
        transferDetails.setTransferAmount(100.0);

        when(bankingService.transferDetails(any(TransferDetails.class), eq(9999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 9999 not found."));

        mockMvc.perform(put("/accounts/transfer/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer Number 9999 not found."));
    }

    @Test
    void getTransactionByAccountNumber_returnsTransactions() throws Exception {
        TransactionDetails tx1 = TransactionDetails.builder()
                .accountNumber(1001L)
                .txType("CREDIT")
                .txAmount(500.0)
                .txDateTime(new Date())
                .build();
        TransactionDetails tx2 = TransactionDetails.builder()
                .accountNumber(1001L)
                .txType("DEBIT")
                .txAmount(200.0)
                .txDateTime(new Date())
                .build();

        when(bankingService.findTransactionsByAccountNumber(1001L))
                .thenReturn(Arrays.asList(tx1, tx2));

        mockMvc.perform(get("/accounts/transactions/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(1001))
                .andExpect(jsonPath("$[0].txType").value("CREDIT"))
                .andExpect(jsonPath("$[0].txAmount").value(500.0))
                .andExpect(jsonPath("$[1].txType").value("DEBIT"))
                .andExpect(jsonPath("$[1].txAmount").value(200.0));
    }

    @Test
    void getTransactionByAccountNumber_noTransactions_returnsEmptyList() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(1001L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/accounts/transactions/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
