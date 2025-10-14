package com.coding.exercise.bankapp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private BankingServiceImpl bankingService;

    @InjectMocks
    private CustomerController controller;

    @Test
    @DisplayName("getAllCustomers delegates to service and returns customer list")
    void getAllCustomers() {
        CustomerDetails customer1 = CustomerDetails.builder()
                .customerNumber(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
        CustomerDetails customer2 = CustomerDetails.builder()
                .customerNumber(2L)
                .firstName("Jane")
                .lastName("Smith")
                .build();
        List<CustomerDetails> customers = Arrays.asList(customer1, customer2);
        
        when(bankingService.findAll()).thenReturn(customers);

        List<CustomerDetails> result = controller.getAllCustomers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(customer1, customer2);
        verify(bankingService, times(1)).findAll();
        verifyNoMoreInteractions(bankingService);
    }

    @Test
    @DisplayName("addCustomer delegates to service with customer details")
    void addCustomer() {
        CustomerDetails customerDetails = CustomerDetails.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .build();
        CustomerDetails createdCustomer = CustomerDetails.builder()
                .customerNumber(999L)
                .firstName("Alice")
                .lastName("Johnson")
                .build();
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        
        when(bankingService.addCustomer(customerDetails)).thenReturn(mockResponse);

        ResponseEntity<Object> result = controller.addCustomer(customerDetails);

        assertThat(result).isEqualTo(mockResponse);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(createdCustomer);
        verify(bankingService, times(1)).addCustomer(customerDetails);
        verifyNoMoreInteractions(bankingService);
    }

    @Test
    @DisplayName("getCustomer delegates to service and returns customer details")
    void getCustomer() {
        Long customerNumber = 12345L;
        CustomerDetails mockCustomer = CustomerDetails.builder()
                .customerNumber(customerNumber)
                .firstName("Bob")
                .lastName("Brown")
                .build();
        
        when(bankingService.findByCustomerNumber(customerNumber)).thenReturn(mockCustomer);

        CustomerDetails result = controller.getCustomer(customerNumber);

        assertThat(result).isEqualTo(mockCustomer);
        assertThat(result.getCustomerNumber()).isEqualTo(customerNumber);
        verify(bankingService, times(1)).findByCustomerNumber(customerNumber);
        verifyNoMoreInteractions(bankingService);
    }

    @Test
    @DisplayName("updateCustomer delegates to service with customer details and customer number")
    void updateCustomer() {
        Long customerNumber = 12345L;
        CustomerDetails updateDetails = CustomerDetails.builder()
                .firstName("Robert")
                .lastName("Brown")
                .build();
        CustomerDetails updatedCustomer = CustomerDetails.builder()
                .customerNumber(customerNumber)
                .firstName("Robert")
                .lastName("Brown")
                .build();
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        
        when(bankingService.updateCustomer(updateDetails, customerNumber)).thenReturn(mockResponse);

        ResponseEntity<Object> result = controller.updateCustomer(updateDetails, customerNumber);

        assertThat(result).isEqualTo(mockResponse);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(updatedCustomer);
        verify(bankingService, times(1)).updateCustomer(updateDetails, customerNumber);
        verifyNoMoreInteractions(bankingService);
    }

    @Test
    @DisplayName("deleteCustomer delegates to service and returns response")
    void deleteCustomer() {
        Long customerNumber = 12345L;
        ResponseEntity<Object> mockResponse = new ResponseEntity<>("Customer deleted", HttpStatus.OK);
        
        when(bankingService.deleteCustomer(customerNumber)).thenReturn(mockResponse);

        ResponseEntity<Object> result = controller.deleteCustomer(customerNumber);

        assertThat(result).isEqualTo(mockResponse);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo("Customer deleted");
        verify(bankingService, times(1)).deleteCustomer(customerNumber);
        verifyNoMoreInteractions(bankingService);
    }
}
