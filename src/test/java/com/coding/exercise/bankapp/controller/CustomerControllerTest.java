package com.coding.exercise.bankapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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

    private CustomerDetails createTestCustomerDetails() {
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

        return CustomerDetails.builder()
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
    @WithMockUser
    public void testGetAllCustomers() throws Exception {
        CustomerDetails customer = createTestCustomerDetails();
        List<CustomerDetails> customers = Arrays.asList(customer);

        when(bankingService.findAll()).thenReturn(customers);

        mockMvc.perform(get("/customers/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    @WithMockUser
    public void testAddCustomer() throws Exception {
        CustomerDetails customer = createTestCustomerDetails();

        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testGetCustomer() throws Exception {
        CustomerDetails customer = createTestCustomerDetails();

        when(bankingService.findByCustomerNumber(12345L)).thenReturn(customer);

        mockMvc.perform(get("/customers/12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.customerNumber").value(12345));
    }

    @Test
    @WithMockUser
    public void testGetCustomerNotFound() throws Exception {
        when(bankingService.findByCustomerNumber(99999L)).thenReturn(null);

        mockMvc.perform(get("/customers/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testUpdateCustomer() throws Exception {
        CustomerDetails customer = createTestCustomerDetails();

        when(bankingService.updateCustomer(any(CustomerDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testUpdateCustomerNotFound() throws Exception {
        CustomerDetails customer = createTestCustomerDetails();

        when(bankingService.updateCustomer(any(CustomerDetails.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 99999 not found."));

        mockMvc.perform(put("/customers/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testDeleteCustomer() throws Exception {
        when(bankingService.deleteCustomer(12345L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDeleteCustomerNotFound() throws Exception {
        when(bankingService.deleteCustomer(99999L))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist."));

        mockMvc.perform(delete("/customers/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
