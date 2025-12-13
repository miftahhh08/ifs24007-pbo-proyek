package org.delcom.app.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenTests {

    private AuthToken authToken;
    private Long userId;
    private String token;

    @BeforeEach
    void setUp() {
        userId = 1L;
        token = "test-jwt-token-12345";
    }

    @Test
    void testConstructor_NoArgs() {
        // Act
        authToken = new AuthToken();

        // Assert
        assertNotNull(authToken);
        assertNull(authToken.getUserId());
        assertNull(authToken.getToken());
        assertNull(authToken.getId());
    }

    @Test
    void testConstructor_WithArgs() {
        // Act
        authToken = new AuthToken(userId, token);

        // Assert
        assertNotNull(authToken);
        assertEquals(userId, authToken.getUserId());
        assertEquals(token, authToken.getToken());
        assertNull(authToken.getId()); // ID baru diisi setelah save ke database
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        authToken = new AuthToken();
        Long id = 10L;

        // Act
        authToken.setId(id);
        authToken.setUserId(userId);
        authToken.setToken(token);

        // Assert
        assertEquals(id, authToken.getId());
        assertEquals(userId, authToken.getUserId());
        assertEquals(token, authToken.getToken());
    }

    @Test
    void testTokenUniqueness() {
        // Arrange
        AuthToken token1 = new AuthToken(1L, "token-1");
        AuthToken token2 = new AuthToken(2L, "token-2");

        // Assert
        assertNotEquals(token1.getToken(), token2.getToken());
        assertNotEquals(token1.getUserId(), token2.getUserId());
    }

    @Test
    void testSetId() {
        // Arrange
        authToken = new AuthToken(userId, token);
        Long newId = 99L;

        // Act
        authToken.setId(newId);

        // Assert
        assertEquals(newId, authToken.getId());
    }

    @Test
    void testSetUserId() {
        // Arrange
        authToken = new AuthToken();
        Long newUserId = 5L;

        // Act
        authToken.setUserId(newUserId);

        // Assert
        assertEquals(newUserId, authToken.getUserId());
    }

    @Test
    void testSetToken() {
        // Arrange
        authToken = new AuthToken();
        String newToken = "new-test-token-67890";

        // Act
        authToken.setToken(newToken);

        // Assert
        assertEquals(newToken, authToken.getToken());
    }
}