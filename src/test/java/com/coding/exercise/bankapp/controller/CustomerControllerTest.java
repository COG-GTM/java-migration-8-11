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

import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingServiceImpl bankingService;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================================================
    // GET /customers/all
    // ============================================================

    @Test
    public void testGetAllCustomers_returnsList() throws Exception {
        CustomerDetails c1 = CustomerDetails.builder()
                .firstName("John").lastName("Doe").customerNumber(1001L).status("Active").build();
        CustomerDetails c2 = CustomerDetails.builder()
                .firstName("Jane").lastName("Smith").customerNumber(1002L).status("Active").build();

        when(bankingService.findAll()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    public void testGetAllCustomers_emptyList() throws Exception {
        when(bankingService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ============================================================
    // POST /customers/add
    // ============================================================

    @Test
    public void testAddCustomer_returns201() throws Exception {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .customerNumber(1001L)
                .status("Active")
                .customerAddress(AddressDetails.builder().address1("123 Main St").city("Springfield").state("IL").zip("62704").country("US").build())
                .contactDetails(ContactDetails.builder().emailId("john@example.com").homePhone("555-1234").workPhone("555-5678").build())
                .build();

        when(bankingService.addCustomer(any(CustomerDetails.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDetails)))
                .andExpect(status().isCreated());
    }

    // ============================================================
    // GET /customers/{customerNumber}
    // ============================================================

    @Test
    public void testGetCustomer_found() throws Exception {
        CustomerDetails cd = CustomerDetails.builder()
                .firstName("John").lastName("Doe").customerNumber(1001L).status("Active").build();

        when(bankingService.findByCustomerNumber(1001L)).thenReturn(cd);

        mockMvc.perform(get("/customers/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.customerNumber").value(1001));
    }

    @Test
    public void testGetCustomer_notFound_returnsEmptyBody() throws Exception {
        // Documents current behavior: no 404 is returned for missing customer
        when(bankingService.findByCustomerNumber(9999L)).thenReturn(null);

        mockMvc.perform(get("/customers/9999"))
                .andExpect(status().isOk());
    }

    // ============================================================
    // PUT /customers/{customerNumber}
    // ============================================================

    @Test
    public void testUpdateCustomer_returns200() throws Exception {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("John").lastName("Doe").status("Active").build();

        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(1001L)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

        mockMvc.perform(put("/customers/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDetails)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCustomer_returns404() throws Exception {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("John").lastName("Doe").status("Active").build();

        when(bankingService.updateCustomer(any(CustomerDetails.class), eq(9999L)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Number 9999 not found."));

        mockMvc.perform(put("/customers/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDetails)))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // DELETE /customers/{customerNumber}
    // ============================================================

    @Test
    public void testDeleteCustomer_returns200() throws Exception {
        when(bankingService.deleteCustomer(1001L))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

        mockMvc.perform(delete("/customers/1001"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCustomer_returns400() throws Exception {
        when(bankingService.deleteCustomer(9999L))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer does not exist."));

        mockMvc.perform(delete("/customers/9999"))
                .andExpect(status().isBadRequest());
    }
}
