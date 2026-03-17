package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDetails createCustomerDetails() {
        return CustomerDetails.builder()
                .firstName("John").middleName("M").lastName("Doe")
                .customerNumber(1001L).status("Active")
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com").homePhone("555-1234").workPhone("555-5678")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St").address2("Apt 1")
                        .city("Springfield").state("IL").zip("62701").country("US")
                        .build())
                .build();
    }

    @Test
    @WithMockUser
    void getAllCustomers_shouldReturnListOfCustomers() throws Exception {
        CustomerDetails customer = createCustomerDetails();
        when(bankingService.findAll()).thenReturn(Arrays.asList(customer));

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser
    void getAllCustomers_shouldReturnEmptyList() throws Exception {
        when(bankingService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void addCustomer_shouldReturnCreated() throws Exception {
        CustomerDetails customer = createCustomerDetails();
        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void getCustomer_shouldReturnCustomer() throws Exception {
        CustomerDetails customer = createCustomerDetails();
        when(bankingService.findByCustomerNumber(1001L)).thenReturn(customer);

        mockMvc.perform(get("/customers/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser
    void getCustomer_shouldReturnNullWhenNotFound() throws Exception {
        when(bankingService.findByCustomerNumber(9999L)).thenReturn(null);

        mockMvc.perform(get("/customers/9999"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateCustomer_shouldReturnOk() throws Exception {
        CustomerDetails customer = createCustomerDetails();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.ok("Success: Customer updated."));

        mockMvc.perform(put("/customers/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteCustomer_shouldReturnOk() throws Exception {
        when(bankingService.deleteCustomer(1001L))
                .thenReturn(ResponseEntity.ok("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/1001"))
                .andExpect(status().isOk());
    }

}
