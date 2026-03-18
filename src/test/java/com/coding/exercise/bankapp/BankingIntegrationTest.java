package com.coding.exercise.bankapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransactionDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;

/**
 * Integration tests for the banking application.
 *
 * Note: The application has an existing JPA bug where {@code @OneToOne(cascade=ALL)}
 * on the Contact entity does not correctly persist with H2. Retrieving a Customer
 * after creation fails with EntityNotFoundException for the Contact. This limits
 * the scope of integration tests that involve customer retrieval after creation.
 * The bug is in the source code entity mappings and is NOT introduced by these tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BankingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplate auth() {
        return restTemplate.withBasicAuth("bankapp", "changeit");
    }

    @Test
    void createCustomer_shouldReturn201() {
        CustomerDetails customer = buildCustomer(10001L);
        ResponseEntity<String> response = auth().postForEntity(
                "/customers/add", customer, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getCustomerNotFound_shouldReturnOkWithNullBody() {
        ResponseEntity<String> response = auth().getForEntity(
                "/customers/99999", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteNonExistentCustomer_shouldReturnBadRequest() {
        ResponseEntity<String> response = auth().exchange(
                "/customers/99999", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAccountNotFound_shouldReturnNotFound() {
        ResponseEntity<String> response = auth().getForEntity(
                "/accounts/99999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getTransactionsForNonExistentAccount_shouldReturnEmptyList() {
        ResponseEntity<List<TransactionDetails>> response = auth().exchange(
                "/accounts/transactions/99999",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<TransactionDetails>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void transferWithNonExistentCustomer_shouldReturnNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(1L);
        transfer.setToAccountNumber(2L);
        transfer.setTransferAmount(100.0);

        ResponseEntity<String> response = auth().exchange(
                "/accounts/transfer/99999",
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(transfer),
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private CustomerDetails buildCustomer(Long customerNumber) {
        ContactDetails contact = ContactDetails.builder()
                .emailId("integration@test.com")
                .homePhone("555-1111")
                .workPhone("555-2222")
                .build();

        AddressDetails address = AddressDetails.builder()
                .address1("100 Integration Ave")
                .address2("Suite 1")
                .city("TestCity")
                .state("TS")
                .zip("99999")
                .country("US")
                .build();

        return CustomerDetails.builder()
                .firstName("Integration")
                .middleName("T")
                .lastName("Test")
                .customerNumber(customerNumber)
                .status("ACTIVE")
                .contactDetails(contact)
                .customerAddress(address)
                .build();
    }
}
