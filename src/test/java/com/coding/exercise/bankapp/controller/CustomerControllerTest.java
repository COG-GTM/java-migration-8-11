package com.coding.exercise.bankapp.controller;

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private BankingServiceImpl bankingService;
    
    @InjectMocks
    private CustomerController customerController;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }
    
    @Test
    void testGetAllCustomers_ShouldReturnCustomerList() throws Exception {
        List<CustomerDetails> customers = new ArrayList<>();
        CustomerDetails customer = new CustomerDetails();
        customer.setCustomerNumber(1001L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customers.add(customer);
        
        when(bankingService.findAll()).thenReturn(customers);
        
        mockMvc.perform(get("/customers/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(bankingService, times(1)).findAll();
    }
    
    @Test
    void testAddCustomer_ValidCustomer_ShouldCreateCustomer() throws Exception {
        when(bankingService.addCustomer(any(CustomerDetails.class)))
            .thenReturn(ResponseEntity.status(201).body("New Customer created successfully"));
        
        mockMvc.perform(post("/customers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated());
        
        verify(bankingService, times(1)).addCustomer(any(CustomerDetails.class));
    }
    
    @Test
    void testAddCustomer_InvalidCustomerData_ShouldReturnBadRequest() {
    }
    
    @Test
    void testGetCustomerDetails_ValidCustomerNumber_ShouldReturnCustomer() throws Exception {
        CustomerDetails customer = new CustomerDetails();
        customer.setCustomerNumber(1001L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        
        when(bankingService.findByCustomerNumber(1001L)).thenReturn(customer);
        
        mockMvc.perform(get("/customers/1001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(bankingService, times(1)).findByCustomerNumber(1001L);
    }
    
    @Test
    void testGetCustomerDetails_InvalidCustomerNumber_ShouldReturnNotFound() {
    }
    
    @Test
    void testUpdateCustomer_ValidUpdate_ShouldReturnUpdatedCustomer() throws Exception {
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(1001L)))
            .thenReturn(ResponseEntity.status(200).body("Success: Customer updated"));
        
        mockMvc.perform(put("/customers/1001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
        
        verify(bankingService, times(1)).updateCustomer(any(CustomerDetails.class), eq(1001L));
    }
    
    @Test
    void testUpdateCustomer_InvalidCustomerData_ShouldReturnBadRequest() {
    }
    
    @Test
    void testDeleteCustomer_ValidCustomerNumber_ShouldReturnSuccess() throws Exception {
        when(bankingService.deleteCustomer(1001L))
            .thenReturn(ResponseEntity.status(200).body("Success: Customer deleted"));
        
        mockMvc.perform(delete("/customers/1001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(bankingService, times(1)).deleteCustomer(1001L);
    }
    
    @Test
    void testDeleteCustomer_InvalidCustomerNumber_ShouldReturnNotFound() {
    }
}
