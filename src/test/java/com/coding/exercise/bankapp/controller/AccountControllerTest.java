package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    private AccountInformation createTestAccountInformation() {
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

        return AccountInformation.builder()
                .accountNumber(1000001L)
                .accountType("Savings")
                .accountStatus("Active")
                .accountBalance(5000.00)
                .bankInformation(bankInformation)
                .build();
    }

    private TransferDetails createTestTransferDetails() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(1000001L);
        transferDetails.setToAccountNumber(2000002L);
        transferDetails.setTransferAmount(500.00);
        return transferDetails;
    }

    private TransactionDetails createTestTransactionDetails() {
        return TransactionDetails.builder()
                .accountNumber(1000001L)
                .txDateTime(new Date())
                .txType("DEBIT")
                .txAmount(100.00)
                .build();
    }

    @Test
    @WithMockUser
    public void testGetByAccountNumber() throws Exception {
        AccountInformation accountInfo = createTestAccountInformation();

        when(bankingService.findByAccountNumber(1000001L))
                .thenReturn(ResponseEntity.status(HttpStatus.FOUND).body(accountInfo));

        mockMvc.perform(get("/accounts/1000001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser
    public void testGetByAccountNumberNotFound() throws Exception {
        when(bankingService.findByAccountNumber(99999L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Number 99999 not found."));

        mockMvc.perform(get("/accounts/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testAddNewAccount() throws Exception {
        AccountInformation accountInfo = createTestAccountInformation();

        when(bankingService.addNewAccount(any(AccountInformation.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Account created successfully."));

        mockMvc.perform(post("/accounts/add/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountInfo)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testTransferDetails() throws Exception {
        TransferDetails transferDetails = createTestTransferDetails();

        when(bankingService.transferDetails(any(TransferDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Amount transferred for Customer Number 12345"));

        mockMvc.perform(put("/accounts/transfer/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testTransferDetailsCustomerNotFound() throws Exception {
        TransferDetails transferDetails = createTestTransferDetails();

        when(bankingService.transferDetails(any(TransferDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 99999 not found."));

        mockMvc.perform(put("/accounts/transfer/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testTransferDetailsInsufficientFunds() throws Exception {
        TransferDetails transferDetails = createTestTransferDetails();

        when(bankingService.transferDetails(any(TransferDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds."));

        mockMvc.perform(put("/accounts/transfer/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testGetTransactionByAccountNumber() throws Exception {
        TransactionDetails transactionDetails = createTestTransactionDetails();
        List<TransactionDetails> transactions = Arrays.asList(transactionDetails);

        when(bankingService.findTransactionsByAccountNumber(1000001L)).thenReturn(transactions);

        mockMvc.perform(get("/accounts/transactions/1000001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(1000001))
                .andExpect(jsonPath("$[0].txType").value("DEBIT"));
    }

    @Test
    @WithMockUser
    public void testGetTransactionByAccountNumberEmpty() throws Exception {
        when(bankingService.findTransactionsByAccountNumber(99999L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/accounts/transactions/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
