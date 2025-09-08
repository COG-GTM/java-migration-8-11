package com.coding.exercise.bankapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void testContactBuilder_ShouldCreateContactWithAllFields() {
        Contact contact = Contact.builder()
            .emailId("test@example.com")
            .homePhone("123-456-7890")
            .workPhone("098-765-4321")
            .build();
        
        assertNotNull(contact);
        assertEquals("test@example.com", contact.getEmailId());
        assertEquals("123-456-7890", contact.getHomePhone());
        assertEquals("098-765-4321", contact.getWorkPhone());
    }
    
    @Test
    void testContactNoArgsConstructor_ShouldCreateEmptyContact() {
        Contact contact = new Contact();
        
        assertNotNull(contact);
        assertNull(contact.getEmailId());
        assertNull(contact.getHomePhone());
        assertNull(contact.getWorkPhone());
    }
    
    @Test
    void testContactSettersAndGetters_ShouldWorkCorrectly() {
        Contact contact = new Contact();
        
        contact.setEmailId("setter@example.com");
        contact.setHomePhone("555-1234");
        contact.setWorkPhone("555-5678");
        
        assertEquals("setter@example.com", contact.getEmailId());
        assertEquals("555-1234", contact.getHomePhone());
        assertEquals("555-5678", contact.getWorkPhone());
    }
    
    @Test
    void testContactEqualsAndHashCode_ShouldWorkCorrectly() {
        Contact contact1 = Contact.builder()
            .emailId("equals@test.com")
            .homePhone("111-222-3333")
            .workPhone("444-555-6666")
            .build();
        
        Contact contact2 = Contact.builder()
            .emailId("equals@test.com")
            .homePhone("111-222-3333")
            .workPhone("444-555-6666")
            .build();
        
        Contact contact3 = Contact.builder()
            .emailId("different@test.com")
            .homePhone("111-222-3333")
            .workPhone("444-555-6666")
            .build();
        
        assertEquals(contact1, contact2);
        assertNotEquals(contact1, contact3);
        assertEquals(contact1.hashCode(), contact2.hashCode());
    }
    
    @Test
    void testContactToString_ShouldContainAllFields() {
        Contact contact = Contact.builder()
            .emailId("toString@example.com")
            .homePhone("777-888-9999")
            .workPhone("000-111-2222")
            .build();
        
        String contactString = contact.toString();
        
        assertNotNull(contactString);
        assertTrue(contactString.contains("toString@example.com"));
        assertTrue(contactString.contains("777-888-9999"));
        assertTrue(contactString.contains("000-111-2222"));
    }
}
