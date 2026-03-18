package com.coding.exercise.bankapp.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.Collections;

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

import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDetails createSampleCustomerDetails() {
        return CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(12345L)
                .status("Active")
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com")
                        .homePhone("555-1234")
                        .workPhone("555-5678")
                        .build())
                .build();
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetAllCustomers_ReturnsList() throws Exception {
        CustomerDetails customer = createSampleCustomerDetails();
        when(bankingService.findAll()).thenReturn(Arrays.asList(customer));

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].customerNumber", is(12345)));

        verify(bankingService).findAll();
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetAllCustomers_ReturnsEmptyList() throws Exception {
        when(bankingService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testAddCustomer_Success() throws Exception {
        CustomerDetails customer = createSampleCustomerDetails();
        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());

        verify(bankingService).addCustomer(any(CustomerDetails.class));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetCustomer_Found() throws Exception {
        CustomerDetails customer = createSampleCustomerDetails();
        when(bankingService.findByCustomerNumber(12345L)).thenReturn(customer);

        mockMvc.perform(get("/customers/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.customerNumber", is(12345)));

        verify(bankingService).findByCustomerNumber(12345L);
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testGetCustomer_NotFound() throws Exception {
        when(bankingService.findByCustomerNumber(99999L)).thenReturn(null);

        mockMvc.perform(get("/customers/99999"))
                .andExpect(status().isOk());

        verify(bankingService).findByCustomerNumber(99999L);
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testUpdateCustomer_Success() throws Exception {
        CustomerDetails customer = createSampleCustomerDetails();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(12345L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/12345").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());

        verify(bankingService).updateCustomer(any(CustomerDetails.class), eq(12345L));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testUpdateCustomer_NotFound() throws Exception {
        CustomerDetails customer = createSampleCustomerDetails();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(99999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 99999 not found."));

        mockMvc.perform(put("/customers/99999").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());

        verify(bankingService).updateCustomer(any(CustomerDetails.class), eq(99999L));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testDeleteCustomer_Success() throws Exception {
        when(bankingService.deleteCustomer(12345L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/12345").with(csrf()))
                .andExpect(status().isOk());

        verify(bankingService).deleteCustomer(12345L);
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    public void testDeleteCustomer_NotFound() throws Exception {
        when(bankingService.deleteCustomer(99999L))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist."));

        mockMvc.perform(delete("/customers/99999").with(csrf()))
                .andExpect(status().isBadRequest());

        verify(bankingService).deleteCustomer(99999L);
    }

}
