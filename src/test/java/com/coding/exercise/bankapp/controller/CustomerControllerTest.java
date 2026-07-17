package com.coding.exercise.bankapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.service.BankingServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

	@Mock
	private BankingServiceImpl bankingService;

	private CustomerController controller;

	@BeforeEach
	public void setUp() {
		controller = new CustomerController();
		ReflectionTestUtils.setField(controller, "bankingService", bankingService);
	}

	@Test
	public void testGetAllCustomers() {
		CustomerDetails details = CustomerDetails.builder().customerNumber(1001L).build();
		when(bankingService.findAll()).thenReturn(Arrays.asList(details));

		List<CustomerDetails> result = controller.getAllCustomers();

		assertEquals(1, result.size());
		assertEquals(Long.valueOf(1001L), result.get(0).getCustomerNumber());
	}

	@Test
	public void testAddCustomer() {
		CustomerDetails details = CustomerDetails.builder().customerNumber(1001L).build();
		when(bankingService.addCustomer(details))
				.thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("New Customer created successfully."));

		ResponseEntity<Object> response = controller.addCustomer(details);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	public void testGetCustomer() {
		CustomerDetails details = CustomerDetails.builder().customerNumber(1001L).build();
		when(bankingService.findByCustomerNumber(1001L)).thenReturn(details);

		CustomerDetails result = controller.getCustomer(1001L);

		assertEquals(Long.valueOf(1001L), result.getCustomerNumber());
	}

	@Test
	public void testUpdateCustomer() {
		CustomerDetails details = CustomerDetails.builder().customerNumber(1001L).build();
		when(bankingService.updateCustomer(details, 1001L))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer updated."));

		ResponseEntity<Object> response = controller.updateCustomer(details, 1001L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testDeleteCustomer() {
		when(bankingService.deleteCustomer(1001L))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success: Customer deleted."));

		ResponseEntity<Object> response = controller.deleteCustomer(1001L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
