package com.coding.exercise.bankapp.integration;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.exercise.bankapp.domain.AccountInformation;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.domain.BankInformation;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.TransferDetails;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankingIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/bank-api";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private TestRestTemplate getAuthenticatedRestTemplate() {
        return restTemplate.withBasicAuth("bankapp", "changeit");
    }

    private CustomerDetails createCustomerDetails(String firstName, String lastName, Long customerNumber) {
        return CustomerDetails.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName("M")
                .customerNumber(customerNumber)
                .status("Active")
                .customerAddress(AddressDetails.builder()
                        .address1("123 Main St")
                        .city("Springfield")
                        .state("IL")
                        .zip("62701")
                        .country("US")
                        .build())
                .contactDetails(ContactDetails.builder()
                        .emailId(firstName.toLowerCase() + "@example.com")
                        .homePhone("555-1234")
                        .workPhone("555-5678")
                        .build())
                .build();
    }

    private AccountInformation createAccountInfo(Long accountNumber, Double balance) {
        return AccountInformation.builder()
                .accountNumber(accountNumber)
                .bankInformation(BankInformation.builder()
                        .branchName("Main Branch")
                        .branchCode(1001)
                        .routingNumber(123456789)
                        .branchAddress(AddressDetails.builder()
                                .address1("456 Bank St")
                                .city("Springfield")
                                .state("IL")
                                .zip("62701")
                                .country("US")
                                .build())
                        .build())
                .accountStatus("Active")
                .accountType("SAVINGS")
                .accountBalance(balance)
                .build();
    }

    // ========== Customer Integration Tests ==========

    @Test
    public void testCreateCustomer() {
        CustomerDetails customer = createCustomerDetails("John", "Doe", 10001L);
        HttpEntity<CustomerDetails> request = new HttpEntity<>(customer, createHeaders());

        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .postForEntity(getBaseUrl() + "/customers/add", request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testGetAllCustomers() {
        // Create a customer first
        CustomerDetails customer = createCustomerDetails("Alice", "Smith", 20001L);
        HttpEntity<CustomerDetails> request = new HttpEntity<>(customer, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/customers/add", request, String.class);

        // Get all customers
        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .getForEntity(getBaseUrl() + "/customers/all", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetCustomerByNumber_NotFound() {
        ResponseEntity<CustomerDetails> response = getAuthenticatedRestTemplate()
                .getForEntity(getBaseUrl() + "/customers/999999", CustomerDetails.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Returns null body when not found (controller returns null)
    }

    @Test
    public void testUpdateCustomer() {
        // Create a customer first
        CustomerDetails customer = createCustomerDetails("Bob", "Jones", 30001L);
        HttpEntity<CustomerDetails> createRequest = new HttpEntity<>(customer, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/customers/add", createRequest, String.class);

        // Update the customer
        CustomerDetails updatedCustomer = createCustomerDetails("Robert", "Jones", 30001L);
        HttpEntity<CustomerDetails> updateRequest = new HttpEntity<>(updatedCustomer, createHeaders());

        ResponseEntity<String> updateResponse = getAuthenticatedRestTemplate()
                .exchange(getBaseUrl() + "/customers/30001", HttpMethod.PUT, updateRequest, String.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    public void testDeleteCustomer() {
        // Create a customer first
        CustomerDetails customer = createCustomerDetails("Charlie", "Brown", 40001L);
        HttpEntity<CustomerDetails> createRequest = new HttpEntity<>(customer, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/customers/add", createRequest, String.class);

        // Delete the customer
        ResponseEntity<String> deleteResponse = getAuthenticatedRestTemplate()
                .exchange(getBaseUrl() + "/customers/40001", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    @Test
    public void testDeleteCustomer_NotExists() {
        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .exchange(getBaseUrl() + "/customers/888888", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== Account Integration Tests ==========

    @Test
    public void testCreateAccount() {
        // Create a customer first
        CustomerDetails customer = createCustomerDetails("Dave", "Wilson", 50001L);
        HttpEntity<CustomerDetails> customerRequest = new HttpEntity<>(customer, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/customers/add", customerRequest, String.class);

        // Create an account for that customer
        AccountInformation account = createAccountInfo(500001L, 1000.0);
        HttpEntity<AccountInformation> accountRequest = new HttpEntity<>(account, createHeaders());

        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .postForEntity(getBaseUrl() + "/accounts/add/50001", accountRequest, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testGetAccountByNumber_NotFound() {
        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .getForEntity(getBaseUrl() + "/accounts/999999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetTransactions_Empty() {
        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .getForEntity(getBaseUrl() + "/accounts/transactions/999999", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ========== Transfer Integration Tests ==========

    @Test
    public void testFullTransferFlow() {
        // 1. Create a customer
        CustomerDetails customer = createCustomerDetails("Eve", "Taylor", 60001L);
        HttpEntity<CustomerDetails> customerRequest = new HttpEntity<>(customer, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/customers/add", customerRequest, String.class);

        // 2. Create two accounts for the customer
        AccountInformation account1 = createAccountInfo(600001L, 5000.0);
        HttpEntity<AccountInformation> accRequest1 = new HttpEntity<>(account1, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/accounts/add/60001", accRequest1, String.class);

        AccountInformation account2 = createAccountInfo(600002L, 1000.0);
        HttpEntity<AccountInformation> accRequest2 = new HttpEntity<>(account2, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/accounts/add/60001", accRequest2, String.class);

        // 3. Transfer funds between accounts
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(600001L);
        transfer.setToAccountNumber(600002L);
        transfer.setTransferAmount(2000.0);
        HttpEntity<TransferDetails> transferRequest = new HttpEntity<>(transfer, createHeaders());

        ResponseEntity<String> transferResponse = getAuthenticatedRestTemplate()
                .exchange(getBaseUrl() + "/accounts/transfer/60001", HttpMethod.PUT, transferRequest, String.class);

        assertEquals(HttpStatus.OK, transferResponse.getStatusCode());

        // 4. Verify account balances via GET
        ResponseEntity<String> acc1Response = getAuthenticatedRestTemplate()
                .getForEntity(getBaseUrl() + "/accounts/600001", String.class);
        assertEquals(HttpStatus.FOUND, acc1Response.getStatusCode());

        ResponseEntity<String> acc2Response = getAuthenticatedRestTemplate()
                .getForEntity(getBaseUrl() + "/accounts/600002", String.class);
        assertEquals(HttpStatus.FOUND, acc2Response.getStatusCode());

        // 5. Check transaction history
        ResponseEntity<String> txResponse = getAuthenticatedRestTemplate()
                .getForEntity(getBaseUrl() + "/accounts/transactions/600001", String.class);
        assertEquals(HttpStatus.OK, txResponse.getStatusCode());
    }

    @Test
    public void testTransfer_InsufficientFunds() {
        // Create customer and accounts
        CustomerDetails customer = createCustomerDetails("Frank", "Garcia", 70001L);
        HttpEntity<CustomerDetails> customerRequest = new HttpEntity<>(customer, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/customers/add", customerRequest, String.class);

        AccountInformation account1 = createAccountInfo(700001L, 100.0);
        HttpEntity<AccountInformation> accRequest1 = new HttpEntity<>(account1, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/accounts/add/70001", accRequest1, String.class);

        AccountInformation account2 = createAccountInfo(700002L, 500.0);
        HttpEntity<AccountInformation> accRequest2 = new HttpEntity<>(account2, createHeaders());
        getAuthenticatedRestTemplate().postForEntity(getBaseUrl() + "/accounts/add/70001", accRequest2, String.class);

        // Try to transfer more than available
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(700001L);
        transfer.setToAccountNumber(700002L);
        transfer.setTransferAmount(5000.0);
        HttpEntity<TransferDetails> transferRequest = new HttpEntity<>(transfer, createHeaders());

        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .exchange(getBaseUrl() + "/accounts/transfer/70001", HttpMethod.PUT, transferRequest, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testTransfer_CustomerNotFound() {
        TransferDetails transfer = new TransferDetails();
        transfer.setFromAccountNumber(100001L);
        transfer.setToAccountNumber(100002L);
        transfer.setTransferAmount(500.0);
        HttpEntity<TransferDetails> transferRequest = new HttpEntity<>(transfer, createHeaders());

        ResponseEntity<String> response = getAuthenticatedRestTemplate()
                .exchange(getBaseUrl() + "/accounts/transfer/999999", HttpMethod.PUT, transferRequest, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
