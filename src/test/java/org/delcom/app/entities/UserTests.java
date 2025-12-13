package org.delcom.app.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTests {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testConstructor_NoArgs() {
        // Act
        User emptyUser = new User();

        // Assert
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getName());
        assertNull(emptyUser.getEmail());
        assertNull(emptyUser.getPassword());
        assertNull(emptyUser.getShopName());
        assertNull(emptyUser.getShopDescription());
        assertNull(emptyUser.getAddress());
        assertNull(emptyUser.getWhatsappNumber());
    }

    @Test
    void testConstructor_WithArgs() {
        // Act
        User newUser = new User("John Doe", "john@example.com", "password123");

        // Assert
        assertNotNull(newUser);
        assertEquals("John Doe", newUser.getName());
        assertEquals("john@example.com", newUser.getEmail());
        assertEquals("password123", newUser.getPassword());
        assertNull(newUser.getShopName());
    }

    @Test
    void testSettersAndGetters_BasicInfo() {
        // Act
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("testpass");

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testpass", user.getPassword());
    }

    @Test
    void testSettersAndGetters_ShopInfo() {
        // Act
        user.setShopName("My Shop");
        user.setShopDescription("This is my shop");
        user.setAddress("123 Street Name");
        user.setWhatsappNumber("628123456789");

        // Assert
        assertEquals("My Shop", user.getShopName());
        assertEquals("This is my shop", user.getShopDescription());
        assertEquals("123 Street Name", user.getAddress());
        assertEquals("628123456789", user.getWhatsappNumber());
    }

    @Test
    void testSetId() {
        // Act
        user.setId(100L);

        // Assert
        assertEquals(100L, user.getId());
    }

    @Test
    void testSetName() {
        // Act
        user.setName("Jane Smith");

        // Assert
        assertEquals("Jane Smith", user.getName());
    }

    @Test
    void testSetEmail() {
        // Act
        user.setEmail("jane@example.com");

        // Assert
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    void testSetPassword() {
        // Act
        user.setPassword("securepass");

        // Assert
        assertEquals("securepass", user.getPassword());
    }

    @Test
    void testSetShopName() {
        // Act
        user.setShopName("Tech Store");

        // Assert
        assertEquals("Tech Store", user.getShopName());
    }

    @Test
    void testSetShopDescription() {
        // Act
        user.setShopDescription("Electronics and gadgets");

        // Assert
        assertEquals("Electronics and gadgets", user.getShopDescription());
    }

    @Test
    void testSetAddress() {
        // Act
        user.setAddress("456 Main Street");

        // Assert
        assertEquals("456 Main Street", user.getAddress());
    }

    @Test
    void testSetWhatsappNumber() {
        // Act
        user.setWhatsappNumber("628987654321");

        // Assert
        assertEquals("628987654321", user.getWhatsappNumber());
    }

    @Test
    void testFullUserProfile() {
        // Arrange & Act
        user.setId(5L);
        user.setName("Complete User");
        user.setEmail("complete@example.com");
        user.setPassword("completepass");
        user.setShopName("Complete Shop");
        user.setShopDescription("A complete shop");
        user.setAddress("789 Complete St");
        user.setWhatsappNumber("628111222333");

        // Assert
        assertEquals(5L, user.getId());
        assertEquals("Complete User", user.getName());
        assertEquals("complete@example.com", user.getEmail());
        assertEquals("completepass", user.getPassword());
        assertEquals("Complete Shop", user.getShopName());
        assertEquals("A complete shop", user.getShopDescription());
        assertEquals("789 Complete St", user.getAddress());
        assertEquals("628111222333", user.getWhatsappNumber());
    }

    @Test
    void testNullValues() {
        // Act
        user.setName(null);
        user.setEmail(null);
        user.setShopName(null);

        // Assert
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getShopName());
    }
}