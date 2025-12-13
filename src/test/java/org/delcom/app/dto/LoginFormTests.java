package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginFormTests {

    private Validator validator;
    private LoginForm loginForm;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        loginForm = new LoginForm();
    }

    @Test
    void testValidLoginForm() {
        // Arrange
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("password123");

        // Act
        Set<ConstraintViolation<LoginForm>> violations = validator.validate(loginForm);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        // Act
        loginForm.setEmail("user@test.com");
        loginForm.setPassword("testpass");

        // Assert
        assertEquals("user@test.com", loginForm.getEmail());
        assertEquals("testpass", loginForm.getPassword());
    }

    @Test
    void testSetEmail() {
        // Act
        loginForm.setEmail("newemail@example.com");

        // Assert
        assertEquals("newemail@example.com", loginForm.getEmail());
    }

    @Test
    void testSetPassword() {
        // Act
        loginForm.setPassword("newpassword");

        // Assert
        assertEquals("newpassword", loginForm.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        LoginForm form = new LoginForm();

        // Assert
        assertNotNull(form);
        assertNull(form.getEmail());
        assertNull(form.getPassword());
    }

    @Test
    void testNullValues() {
        // Act
        loginForm.setEmail(null);
        loginForm.setPassword(null);

        // Assert
        assertNull(loginForm.getEmail());
        assertNull(loginForm.getPassword());
    }

    @Test
    void testEmptyValues() {
        // Act
        loginForm.setEmail("");
        loginForm.setPassword("");

        // Assert
        assertEquals("", loginForm.getEmail());
        assertEquals("", loginForm.getPassword());
    }
}