package com.coding.exercise.bankapp.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DomainClassesTest {

    @Test
    void transferDetails_constructorAndGetters_workCorrectly() {
        TransferDetails transferDetails = new TransferDetails(123L, 456L, 500.0);

        assertEquals(Long.valueOf(123L), transferDetails.getFromAccountNumber());
        assertEquals(Long.valueOf(456L), transferDetails.getToAccountNumber());
        assertEquals(Double.valueOf(500.0), transferDetails.getTransferAmount());
    }

    @Test
    void transferDetails_setters_workCorrectly() {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setFromAccountNumber(789L);
        transferDetails.setToAccountNumber(101112L);
        transferDetails.setTransferAmount(1000.0);

        assertEquals(Long.valueOf(789L), transferDetails.getFromAccountNumber());
        assertEquals(Long.valueOf(101112L), transferDetails.getToAccountNumber());
        assertEquals(Double.valueOf(1000.0), transferDetails.getTransferAmount());
    }

    @Test
    void transferDetails_toStringAndBasicMethods_workCorrectly() {
        TransferDetails transfer1 = new TransferDetails(123L, 456L, 500.0);
        TransferDetails transfer2 = new TransferDetails(789L, 456L, 500.0);

        assertNotNull(transfer1.toString());
        
        assertNotNull(transfer1.hashCode());
        assertNotNull(transfer2.hashCode());
    }

    @Test
    void transferDetails_edgeCases_nullValues() {
        TransferDetails transfer1 = new TransferDetails(null, 456L, null);

        assertNotNull(transfer1.toString());
        assertNotNull(transfer1.hashCode());
    }

    @Test
    void contactDetails_toStringAndBasicMethods_workCorrectly() {
        ContactDetails contact1 = ContactDetails.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();

        assertNotNull(contact1.toString());
        assertNotNull(contact1.hashCode());
    }

    @Test
    void contactDetails_edgeCases_nullFields() {
        ContactDetails contact1 = ContactDetails.builder()
            .emailId(null)
            .homePhone("123-456-7890")
            .workPhone(null)
            .build();

        assertNotNull(contact1.toString());
        assertNotNull(contact1.hashCode());
    }

    @Test
    void addressDetails_toStringAndBasicMethods_workCorrectly() {
        AddressDetails address1 = AddressDetails.builder()
            .address1("123 Main St")
            .address2("Apt 4B")
            .city("Anytown")
            .state("NY")
            .zip("12345")
            .country("USA")
            .build();

        assertNotNull(address1.toString());
        assertNotNull(address1.hashCode());
    }

    @Test
    void addressDetails_edgeCases_mixedNullFields() {
        AddressDetails address1 = AddressDetails.builder()
            .address1("789 Test Ave")
            .address2(null)
            .city("Test City")
            .state(null)
            .zip("54321")
            .country("USA")
            .build();

        assertNotNull(address1.toString());
        assertNotNull(address1.hashCode());
    }

    @Test
    void bankInformation_toStringAndBasicMethods_workCorrectly() {
        AddressDetails branchAddress = AddressDetails.builder()
            .address1("100 Bank St")
            .city("Financial District")
            .state("NY")
            .zip("10001")
            .country("USA")
            .build();

        BankInformation bank1 = BankInformation.builder()
            .branchCode(1001)
            .branchName("Test Bank")
            .routingNumber(123456789)
            .branchAddress(branchAddress)
            .build();

        assertNotNull(bank1.toString());
        assertNotNull(bank1.hashCode());
    }

    @Test
    void bankInformation_edgeCases_nullAddress() {
        BankInformation bank1 = BankInformation.builder()
            .branchCode(5005)
            .branchName("Central Bank")
            .routingNumber(555666777)
            .branchAddress(null)
            .build();

        assertNotNull(bank1.toString());
        assertNotNull(bank1.hashCode());
    }
}
