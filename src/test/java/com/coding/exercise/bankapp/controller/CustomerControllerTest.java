package com.coding.exercise.bankapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@WithMockUser
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDetails buildCustomerDetails() {
        return CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(1001L)
                .status("Active")
                .contactDetails(ContactDetails.builder()
                        .emailId("john@example.com")
                        .homePhone("111-222-3333")
                        .workPhone("444-555-6666")
                        .build())
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St")
                        .address2("Apt 4")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .build();
    }

    @Test
    public void getAllCustomers_returnsList() throws Exception {
        CustomerDetails c1 = buildCustomerDetails();
        CustomerDetails c2 = CustomerDetails.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(1002L)
                .status("Active")
                .build();

        when(bankingService.findAll()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));

        verify(bankingService).findAll();
    }

    @Test
    public void getAllCustomers_emptyList() throws Exception {
        when(bankingService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void addCustomer_returnsCreated() throws Exception {
        CustomerDetails customer = buildCustomerDetails();
        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(content().string("New Customer created successfully."));

        verify(bankingService).addCustomer(any(CustomerDetails.class));
    }

    @Test
    public void getCustomer_found() throws Exception {
        CustomerDetails customer = buildCustomerDetails();
        when(bankingService.findByCustomerNumber(1001L)).thenReturn(customer);

        mockMvc.perform(get("/customers/{customerNumber}", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.customerNumber", is(1001)));

        verify(bankingService).findByCustomerNumber(1001L);
    }

    @Test
    public void getCustomer_notFound_returnsNull() throws Exception {
        when(bankingService.findByCustomerNumber(9999L)).thenReturn(null);

        mockMvc.perform(get("/customers/{customerNumber}", 9999L))
                .andExpect(status().isOk());

        verify(bankingService).findByCustomerNumber(9999L);
    }

    @Test
    public void updateCustomer_success() throws Exception {
        CustomerDetails customer = buildCustomerDetails();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/{customerNumber}", 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Customer updated."));

        verify(bankingService).updateCustomer(any(CustomerDetails.class), eq(1001L));
    }

    @Test
    public void updateCustomer_notFound() throws Exception {
        CustomerDetails customer = buildCustomerDetails();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(9999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 9999 not found."));

        mockMvc.perform(put("/customers/{customerNumber}", 9999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer Number 9999 not found."));
    }

    @Test
    public void deleteCustomer_success() throws Exception {
        when(bankingService.deleteCustomer(1001L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/{customerNumber}", 1001L))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Customer deleted."));

        verify(bankingService).deleteCustomer(1001L);
    }

    @Test
    public void deleteCustomer_notFound() throws Exception {
        when(bankingService.deleteCustomer(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist."));

        mockMvc.perform(delete("/customers/{customerNumber}", 9999L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Customer does not exist."));
    }
}
