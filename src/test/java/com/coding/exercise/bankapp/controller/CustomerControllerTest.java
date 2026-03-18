package com.coding.exercise.bankapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== GET /customers/all ==========

    @Test
    void getAllCustomers_shouldReturnListOfCustomers() throws Exception {
        CustomerDetails c1 = CustomerDetails.builder().firstName("John").lastName("Doe").customerNumber(1L).build();
        CustomerDetails c2 = CustomerDetails.builder().firstName("Jane").lastName("Smith").customerNumber(2L).build();

        when(bankingService.findAll()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void getAllCustomers_shouldReturnEmptyList() throws Exception {
        when(bankingService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========== POST /customers/add ==========

    @Test
    void addCustomer_shouldReturnCreated() throws Exception {
        CustomerDetails customer = CustomerDetails.builder()
                .firstName("John").lastName("Doe").status("ACTIVE").build();

        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());
    }

    @Test
    void addCustomer_shouldDeserializeRequestBody() throws Exception {
        CustomerDetails customer = CustomerDetails.builder()
                .firstName("Alice").middleName("B").lastName("Cooper").status("ACTIVE").customerNumber(123L).build();

        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());
    }

    // ========== GET /customers/{customerNumber} ==========

    @Test
    void getCustomer_shouldReturnCustomerDetails() throws Exception {
        CustomerDetails details = CustomerDetails.builder()
                .firstName("John").lastName("Doe").customerNumber(1L).status("ACTIVE").build();

        when(bankingService.findByCustomerNumber(1L)).thenReturn(details);

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.customerNumber").value(1));
    }

    @Test
    void getCustomer_shouldReturnOkWithNullBodyWhenNotFound() throws Exception {
        when(bankingService.findByCustomerNumber(999L)).thenReturn(null);

        mockMvc.perform(get("/customers/999"))
                .andExpect(status().isOk());
    }

    // ========== PUT /customers/{customerNumber} ==========

    @Test
    void updateCustomer_shouldReturnOkOnSuccess() throws Exception {
        CustomerDetails updateDetails = CustomerDetails.builder()
                .firstName("Updated").lastName("Name").status("ACTIVE").build();

        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(1L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void updateCustomer_shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        CustomerDetails updateDetails = CustomerDetails.builder()
                .firstName("Updated").build();

        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 999 not found."));

        mockMvc.perform(put("/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE /customers/{customerNumber} ==========

    @Test
    void deleteCustomer_shouldReturnOkOnSuccess() throws Exception {
        when(bankingService.deleteCustomer(1L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCustomer_shouldReturnBadRequestWhenCustomerDoesNotExist() throws Exception {
        when(bankingService.deleteCustomer(999L))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist."));

        mockMvc.perform(delete("/customers/999"))
                .andExpect(status().isBadRequest());
    }
}
