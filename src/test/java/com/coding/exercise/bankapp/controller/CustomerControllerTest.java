package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
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

import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
@WithMockUser(username = "testuser", password = "testpass")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDetails testCustomerDetails;

    @BeforeEach
    void setUp() {
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        testCustomerDetails = CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(addressDetails)
                .contactDetails(contactDetails)
                .build();
    }

    @Test
    void testGetAllCustomers() throws Exception {
        List<CustomerDetails> customers = Arrays.asList(testCustomerDetails);
        when(bankingService.findAll()).thenReturn(customers);

        mockMvc.perform(get("/customers/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].customerNumber").value(12345L));
    }

    @Test
    void testGetAllCustomers_Empty() throws Exception {
        when(bankingService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/customers/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testAddCustomer_Success() throws Exception {
        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDetails)))
                .andExpect(status().isCreated())
                .andExpect(content().string("New Customer created successfully."));
    }

    @Test
    void testGetCustomer_Found() throws Exception {
        when(bankingService.findByCustomerNumber(12345L)).thenReturn(testCustomerDetails);

        mockMvc.perform(get("/customers/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.customerNumber").value(12345L))
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    void testGetCustomer_NotFound() throws Exception {
        when(bankingService.findByCustomerNumber(99999L)).thenReturn(null);

        mockMvc.perform(get("/customers/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCustomer_Success() throws Exception {
        when(bankingService.updateCustomer(any(CustomerDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Customer updated."));
    }

    @Test
    void testUpdateCustomer_NotFound() throws Exception {
        when(bankingService.updateCustomer(any(CustomerDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 99999 not found."));

        mockMvc.perform(put("/customers/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDetails)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer Number 99999 not found."));
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        when(bankingService.deleteCustomer(12345L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Customer deleted."));
    }

    @Test
    void testDeleteCustomer_NotFound() throws Exception {
        when(bankingService.deleteCustomer(99999L))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist."));

        mockMvc.perform(delete("/customers/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Customer does not exist."));
    }
}
