package com.coding.exercise.bankapp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankingApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplate authedTemplate() {
        return restTemplate.withBasicAuth("bankapp", "changeit");
    }

    private String customerJson(long customerNumber) {
        return "{\"firstName\":\"IntTest\",\"middleName\":\"M\",\"lastName\":\"User\","
                + "\"customerNumber\":" + customerNumber + ",\"status\":\"ACTIVE\","
                + "\"contactDetails\":{\"emailId\":\"int@test.com\",\"homePhone\":\"111\",\"workPhone\":\"222\"},"
                + "\"customerAddress\":{\"address1\":\"123 Int St\",\"city\":\"TestCity\",\"state\":\"TS\",\"zip\":\"00000\",\"country\":\"US\"}}";
    }

    private String accountJson(long accountNumber, double balance) {
        return "{\"accountNumber\":" + accountNumber + ",\"accountStatus\":\"ACTIVE\","
                + "\"accountType\":\"SAVINGS\",\"accountBalance\":" + balance + ","
                + "\"bankInformation\":{\"branchName\":\"Test Branch\",\"branchCode\":1,\"routingNumber\":100,"
                + "\"branchAddress\":{\"address1\":\"Bank St\",\"city\":\"BankCity\",\"state\":\"BC\",\"zip\":\"11111\",\"country\":\"US\"}}}";
    }

    private HttpEntity<String> jsonEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    // ---- Customer Lifecycle ----

    @Test
    @Order(1)
    void createCustomer_returns201() {
        ResponseEntity<String> response = authedTemplate()
                .postForEntity("/customers/add", jsonEntity(customerJson(10001L)), String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("successfully"));
    }

    @Test
    @Order(2)
    void getCustomer_afterCreate_returns200() {
        ResponseEntity<String> response = authedTemplate()
                .getForEntity("/customers/10001", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("IntTest"));
    }

    @Test
    @Order(3)
    void updateCustomer_returns200() {
        String updateJson = "{\"firstName\":\"Updated\",\"middleName\":\"M\",\"lastName\":\"User\","
                + "\"customerNumber\":10001,\"status\":\"ACTIVE\","
                + "\"contactDetails\":{\"emailId\":\"updated@test.com\",\"homePhone\":\"333\",\"workPhone\":\"444\"},"
                + "\"customerAddress\":{\"address1\":\"456 New St\",\"city\":\"NewCity\",\"state\":\"NC\",\"zip\":\"11111\",\"country\":\"US\"}}";
        ResponseEntity<String> response = authedTemplate()
                .exchange("/customers/10001", HttpMethod.PUT, jsonEntity(updateJson), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void deleteCustomer_returns200() {
        authedTemplate().postForEntity("/customers/add", jsonEntity(customerJson(10099L)), String.class);
        ResponseEntity<String> response = authedTemplate()
                .exchange("/customers/10099", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    void getAllCustomers_returnsOk() {
        ResponseEntity<String> response = authedTemplate()
                .getForEntity("/customers/all", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // ---- Account Lifecycle ----

    @Test
    @Order(6)
    void addAccount_returns201() {
        authedTemplate().postForEntity("/customers/add", jsonEntity(customerJson(20001L)), String.class);
        ResponseEntity<String> response = authedTemplate()
                .postForEntity("/accounts/add/20001", jsonEntity(accountJson(20001L, 5000.0)), String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(7)
    void getAccount_afterCreate_returnsFound() {
        ResponseEntity<String> response = authedTemplate()
                .getForEntity("/accounts/20001", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful()
                || response.getStatusCode().is3xxRedirection(),
                "Expected 2xx or 3xx, got: " + response.getStatusCode());
    }

    // ---- Transfer Flow ----

    @Test
    @Order(8)
    void transferFunds_returns200() {
        authedTemplate().postForEntity("/customers/add", jsonEntity(customerJson(30001L)), String.class);
        authedTemplate().postForEntity("/accounts/add/30001", jsonEntity(accountJson(30001L, 5000.0)), String.class);
        authedTemplate().postForEntity("/accounts/add/30001", jsonEntity(accountJson(30002L, 1000.0)), String.class);

        String transferJson = "{\"fromAccountNumber\":30001,\"toAccountNumber\":30002,\"transferAmount\":500.0}";
        ResponseEntity<String> response = authedTemplate()
                .exchange("/accounts/transfer/30001", HttpMethod.PUT, jsonEntity(transferJson), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(9)
    void getTransactions_afterTransfer_returnsOk() {
        ResponseEntity<String> response = authedTemplate()
                .getForEntity("/accounts/transactions/30001", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // ---- Error Cases ----

    @Test
    @Order(10)
    void deleteNonExistentCustomer_returns400() {
        ResponseEntity<String> response = authedTemplate()
                .exchange("/customers/88888", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(11)
    void transferInsufficientFunds_returns400() {
        authedTemplate().postForEntity("/customers/add", jsonEntity(customerJson(40001L)), String.class);
        authedTemplate().postForEntity("/accounts/add/40001", jsonEntity(accountJson(40001L, 10.0)), String.class);
        authedTemplate().postForEntity("/accounts/add/40001", jsonEntity(accountJson(40002L, 1000.0)), String.class);

        String transferJson = "{\"fromAccountNumber\":40001,\"toAccountNumber\":40002,\"transferAmount\":999.0}";
        ResponseEntity<String> response = authedTemplate()
                .exchange("/accounts/transfer/40001", HttpMethod.PUT, jsonEntity(transferJson), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(12)
    void transferCustomerNotFound_returns404() {
        String transferJson = "{\"fromAccountNumber\":99001,\"toAccountNumber\":99002,\"transferAmount\":100.0}";
        ResponseEntity<String> response = authedTemplate()
                .exchange("/accounts/transfer/99999", HttpMethod.PUT, jsonEntity(transferJson), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(13)
    void accountNotFound_returns404() {
        ResponseEntity<String> response = authedTemplate()
                .getForEntity("/accounts/99999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(14)
    void getNonExistentCustomer_returnsOkWithNullBody() {
        ResponseEntity<String> response = authedTemplate()
                .getForEntity("/customers/77777", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
