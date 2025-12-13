package org.delcom.app.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class ProductTests {

    @Test
    public void testAllGettersSetters() {
        Product p = new Product();
        User u = new User();
        
        // Test Setters
        p.setId(1L);
        p.setName("Bakso");
        p.setDescription("Enak");
        p.setPrice(10000.0);
        p.setImage("img.jpg");
        p.setUser(u);
        
        // Test Getters
        assertEquals(1L, p.getId());
        assertEquals("Bakso", p.getName());
        assertEquals("Enak", p.getDescription());
        assertEquals(10000.0, p.getPrice());
        assertEquals("img.jpg", p.getImage());
        assertEquals(u, p.getUser());
    }

    @Test
    public void testConstructors() {
        User u = new User();
        // Test Constructor Lengkap
        Product p = new Product("Mie", "Pedas", 5000.0, "mie.jpg", u);
        assertEquals("Mie", p.getName());
        assertEquals(u, p.getUser());
        
        // Test Constructor Kosong
        Product pEmpty = new Product();
        assertNull(pEmpty.getName());
    }
}