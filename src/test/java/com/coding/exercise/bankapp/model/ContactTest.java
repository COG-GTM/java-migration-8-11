package com.coding.exercise.bankapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ContactTest {

    @Test
    void testContactBuilder() {
        UUID id = UUID.randomUUID();
        
        Contact contact = Contact.builder()
                .id(id)
                .emailId("test@example.com")
                .homePhone("555-1234")
                .workPhone("555-5678")
                .build();
        
        assertEquals(id, contact.getId());
        assertEquals("test@example.com", contact.getEmailId());
        assertEquals("555-1234", contact.getHomePhone());
        assertEquals("555-5678", contact.getWorkPhone());
    }

    @Test
    void testContactSetters() {
        Contact contact = new Contact();
        UUID id = UUID.randomUUID();
        
        contact.setId(id);
        contact.setEmailId("new@example.com");
        contact.setHomePhone("555-9999");
        contact.setWorkPhone("555-8888");
        
        assertEquals(id, contact.getId());
        assertEquals("new@example.com", contact.getEmailId());
        assertEquals("555-9999", contact.getHomePhone());
        assertEquals("555-8888", contact.getWorkPhone());
    }

    @Test
    void testContactEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        
        Contact contact1 = Contact.builder()
                .id(id)
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();
        
        Contact contact2 = Contact.builder()
                .id(id)
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();
        
        Contact contact3 = Contact.builder()
                .id(UUID.randomUUID())
                .emailId("other@example.com")
                .homePhone("555-9999")
                .build();
        
        assertEquals(contact1, contact2);
        assertEquals(contact1.hashCode(), contact2.hashCode());
        assertNotEquals(contact1, contact3);
        assertEquals(contact1, contact1);
        assertNotEquals(contact1, null);
        assertNotEquals(contact1, "string");
    }

    @Test
    void testContactToString() {
        Contact contact = Contact.builder()
                .emailId("test@example.com")
                .homePhone("555-1234")
                .build();
        
        String toString = contact.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("555-1234"));
    }

    @Test
    void testContactNoArgsConstructor() {
        Contact contact = new Contact();
        assertNotNull(contact);
        assertNull(contact.getId());
        assertNull(contact.getEmailId());
    }

    @Test
    void testContactAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        
        Contact contact = new Contact(id, "test@example.com", "555-1234", "555-5678");
        
        assertEquals(id, contact.getId());
        assertEquals("test@example.com", contact.getEmailId());
        assertEquals("555-1234", contact.getHomePhone());
        assertEquals("555-5678", contact.getWorkPhone());
    }
}
