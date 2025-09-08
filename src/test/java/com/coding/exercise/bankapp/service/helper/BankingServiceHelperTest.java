package com.coding.exercise.bankapp.service.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coding.exercise.bankapp.domain.CustomerDetails;
import com.coding.exercise.bankapp.domain.ContactDetails;
import com.coding.exercise.bankapp.domain.AddressDetails;
import com.coding.exercise.bankapp.model.Customer;
import com.coding.exercise.bankapp.model.Contact;
import com.coding.exercise.bankapp.model.Address;

class BankingServiceHelperTest {

    private BankingServiceHelper bankingServiceHelper;
    
    @BeforeEach
    void setUp() {
        bankingServiceHelper = new BankingServiceHelper();
    }
    
    @Test
    void convertToCustomerDomain_ShouldConvertEntityToDomain() {
        Contact contact = Contact.builder()
                .emailId("john@example.com")
                .homePhone("123-456-7890")
                .workPhone("098-765-4321")
                .build();
        
        Address address = Address.builder()
                .address1("123 Main St")
                .city("Anytown")
                .state("CA")
                .zip("12345")
                .country("USA")
                .build();
        
        Customer customer = Customer.builder()
                .customerNumber(123L)
                .firstName("John")
                .lastName("Doe")
                .status("ACTIVE")
                .contactDetails(contact)
                .customerAddress(address)
                .build();
        
        CustomerDetails result = bankingServiceHelper.convertToCustomerDomain(customer);
        
        assertNotNull(result);
        assertEquals(123L, result.getCustomerNumber().longValue());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertEquals("john@example.com", result.getContactDetails().getEmailId());
        assertNotNull(result.getCustomerAddress());
        assertEquals("123 Main St", result.getCustomerAddress().getAddress1());
    }
    
    @Test
    void convertToCustomerEntity_ShouldConvertDomainToEntity() {
        ContactDetails contactDetails = ContactDetails.builder()
                .emailId("jane@example.com")
                .homePhone("555-123-4567")
                .workPhone("555-987-6543")
                .build();
        
        AddressDetails addressDetails = AddressDetails.builder()
                .address1("456 Oak Ave")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .country("USA")
                .build();
        
        CustomerDetails customerDetails = CustomerDetails.builder()
                .customerNumber(456L)
                .firstName("Jane")
                .lastName("Smith")
                .status("INACTIVE")
                .contactDetails(contactDetails)
                .customerAddress(addressDetails)
                .build();
        
        Customer result = bankingServiceHelper.convertToCustomerEntity(customerDetails);
        
        assertNotNull(result);
        assertEquals(456L, result.getCustomerNumber().longValue());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("INACTIVE", result.getStatus());
        assertNotNull(result.getContactDetails());
        assertEquals("jane@example.com", result.getContactDetails().getEmailId());
        assertNotNull(result.getCustomerAddress());
        assertEquals("456 Oak Ave", result.getCustomerAddress().getAddress1());
    }
    
    @Test
    void convertToContactDomain_ShouldHandleNullInput() {
        assertThrows(NullPointerException.class, () -> {
            bankingServiceHelper.convertToContactDomain(null);
        });
    }
}
