package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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

    private CustomerDetails buildCustomerDetails() {
        return CustomerDetails.builder()
                .firstName("John").middleName("M").lastName("Doe")
                .customerNumber(1001L).status("ACTIVE")
                .contactDetails(ContactDetails.builder().emailId("j@d.com").homePhone("111").workPhone("222").build())
                .customerAddress(AddressDetails.builder().address1("123 St").city("NYC").state("NY").zip("10001").country("US").build())
                .build();
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void getAllCustomers_returns200WithList() throws Exception {
        CustomerDetails cd = buildCustomerDetails();
        when(bankingService.findAll()).thenReturn(Arrays.asList(cd));

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].customerNumber").value(1001));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void getAllCustomers_emptyList() throws Exception {
        when(bankingService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void addCustomer_returns201() throws Exception {
        CustomerDetails cd = buildCustomerDetails();
        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void getCustomer_returns200() throws Exception {
        CustomerDetails cd = buildCustomerDetails();
        when(bankingService.findByCustomerNumber(1001L)).thenReturn(cd);

        mockMvc.perform(get("/customers/1001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.customerNumber").value(1001));
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void updateCustomer_returns200() throws Exception {
        CustomerDetails cd = buildCustomerDetails();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "bankapp", password = "changeit")
    void deleteCustomer_returns200() throws Exception {
        when(bankingService.deleteCustomer(1001L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/1001"))
                .andExpect(status().isOk());
    }
}
