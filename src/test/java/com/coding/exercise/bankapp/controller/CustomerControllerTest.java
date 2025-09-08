package com.coding.exercise.bankapp.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCustomers_delegatesToService_andReturnsCustomerList() throws Exception {
        CustomerDetails customer1 = new CustomerDetails();
        customer1.setCustomerNumber(123L);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");

        CustomerDetails customer2 = new CustomerDetails();
        customer2.setCustomerNumber(456L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");

        List<CustomerDetails> customers = Arrays.asList(customer1, customer2);

        when(bankingService.findAll()).thenReturn(customers);

        mockMvc.perform(get("/customers/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].customerNumber").value(123))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
            .andExpect(jsonPath("$[1].customerNumber").value(456))
            .andExpect(jsonPath("$[1].firstName").value("Jane"))
            .andExpect(jsonPath("$[1].lastName").value("Smith"));

        verify(bankingService).findAll();
    }

    @Test
    void getAllCustomers_emptyList_returnsEmptyArray() throws Exception {
        when(bankingService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/customers/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        verify(bankingService).findAll();
    }

    @Test
    void addCustomer_delegatesToService_andReturnsCreated() throws Exception {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Doe");

        when(bankingService.addCustomer(any(CustomerDetails.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("Customer created"));

        mockMvc.perform(post("/customers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDetails)))
            .andExpect(status().isCreated());

        verify(bankingService).addCustomer(any(CustomerDetails.class));
    }

    @Test
    void getCustomer_delegatesToService_andReturnsCustomer() throws Exception {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerNumber(123L);
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Doe");

        when(bankingService.findByCustomerNumber(123L)).thenReturn(customerDetails);

        mockMvc.perform(get("/customers/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerNumber").value(123))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(bankingService).findByCustomerNumber(123L);
    }

    @Test
    void getCustomer_notFound_returnsNull() throws Exception {
        when(bankingService.findByCustomerNumber(999L)).thenReturn(null);

        mockMvc.perform(get("/customers/999"))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(bankingService).findByCustomerNumber(999L);
    }

    @Test
    void updateCustomer_delegatesToService_andReturnsOk() throws Exception {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Doe");

        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(123L)))
            .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Customer updated"));

        mockMvc.perform(put("/customers/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDetails)))
            .andExpect(status().isOk());

        verify(bankingService).updateCustomer(any(CustomerDetails.class), eq(123L));
    }

    @Test
    void updateCustomer_notFound_returns404() throws Exception {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Doe");

        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(999L)))
            .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found"));

        mockMvc.perform(put("/customers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDetails)))
            .andExpect(status().isNotFound());

        verify(bankingService).updateCustomer(any(CustomerDetails.class), eq(999L));
    }

    @Test
    void deleteCustomer_delegatesToService_andReturnsOk() throws Exception {
        when(bankingService.deleteCustomer(123L))
            .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Customer deleted"));

        mockMvc.perform(delete("/customers/123"))
            .andExpect(status().isOk());

        verify(bankingService).deleteCustomer(123L);
    }

    @Test
    void deleteCustomer_notFound_returnsBadRequest() throws Exception {
        when(bankingService.deleteCustomer(999L))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist"));

        mockMvc.perform(delete("/customers/999"))
            .andExpect(status().isBadRequest());

        verify(bankingService).deleteCustomer(999L);
    }
}
