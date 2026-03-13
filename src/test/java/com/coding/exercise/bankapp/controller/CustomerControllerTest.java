package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
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

    private CustomerDetails buildSampleCustomer() {
        AddressDetails address = AddressDetails.builder()
                .address1("123 Main St")
                .address2("Apt 4B")
                .city("New York")
                .state("NY")
                .zip("10001")
                .country("USA")
                .build();

        ContactDetails contact = ContactDetails.builder()
                .emailId("john.doe@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();

        return CustomerDetails.builder()
                .firstName("John")
                .middleName("M")
                .lastName("Doe")
                .customerNumber(100L)
                .status("ACTIVE")
                .customerAddress(address)
                .contactDetails(contact)
                .build();
    }

    @Test
    void getAllCustomers_returnsList() throws Exception {
        CustomerDetails customer1 = buildSampleCustomer();
        CustomerDetails customer2 = CustomerDetails.builder()
                .firstName("Jane")
                .lastName("Smith")
                .customerNumber(200L)
                .status("ACTIVE")
                .build();

        when(bankingService.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].customerNumber").value(100))
                .andExpect(jsonPath("$[0].contactDetails.emailId").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].customerNumber").value(200));
    }

    @Test
    void getAllCustomers_emptyList() throws Exception {
        when(bankingService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getCustomer_found_returns200() throws Exception {
        CustomerDetails customer = buildSampleCustomer();
        when(bankingService.findByCustomerNumber(100L)).thenReturn(customer);

        mockMvc.perform(get("/customers/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.middleName").value("M"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.customerNumber").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.customerAddress.address1").value("123 Main St"))
                .andExpect(jsonPath("$.customerAddress.city").value("New York"))
                .andExpect(jsonPath("$.contactDetails.emailId").value("john.doe@example.com"));
    }

    @Test
    void getCustomer_notFound_returnsNull() throws Exception {
        when(bankingService.findByCustomerNumber(9999L)).thenReturn(null);

        mockMvc.perform(get("/customers/9999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void addCustomer_validInput_returns201() throws Exception {
        CustomerDetails customer = buildSampleCustomer();
        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(content().string("New Customer created successfully."));
    }

    @Test
    void addCustomer_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer_existing_returns200() throws Exception {
        CustomerDetails customer = buildSampleCustomer();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(100L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Customer updated."));
    }

    @Test
    void updateCustomer_notFound_returns404() throws Exception {
        CustomerDetails customer = buildSampleCustomer();
        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(9999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 9999 not found."));

        mockMvc.perform(put("/customers/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer Number 9999 not found."));
    }

    @Test
    void deleteCustomer_existing_returns200() throws Exception {
        when(bankingService.deleteCustomer(100L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Success: Customer deleted."));
    }

    @Test
    void deleteCustomer_notFound_returns400() throws Exception {
        when(bankingService.deleteCustomer(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist."));

        mockMvc.perform(delete("/customers/9999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Customer does not exist."));
    }
}
