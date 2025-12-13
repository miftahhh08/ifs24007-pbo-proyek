package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFormTests {

    private Validator validator;
    private RegisterForm registerForm;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        registerForm = new RegisterForm();
    }

    @Test
    void testValidRegisterForm() {
        // Arrange
        registerForm.setName("John Doe");
        registerForm.setEmail("john@example.com");
        registerForm.setPassword("password123");

        // Act
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(registerForm);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        // Act
        registerForm.setName("Jane Smith");
        registerForm.setEmail("jane@example.com");
        registerForm.setPassword("securepass");

        // Assert
        assertEquals("Jane Smith", registerForm.getName());
        assertEquals("jane@example.com", registerForm.getEmail());
        assertEquals("securepass", registerForm.getPassword());
    }

    @Test
    void testSetName() {
        // Act
        registerForm.setName("Test User");

        // Assert
        assertEquals("Test User", registerForm.getName());
    }

    @Test
    void testSetEmail() {
        // Act
        registerForm.setEmail("test@example.com");

        // Assert
        assertEquals("test@example.com", registerForm.getEmail());
    }

    @Test
    void testSetPassword() {
        // Act
        registerForm.setPassword("testpassword");

        // Assert
        assertEquals("testpassword", registerForm.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        RegisterForm form = new RegisterForm();

        // Assert
        assertNotNull(form);
        assertNull(form.getName());
        assertNull(form.getEmail());
        assertNull(form.getPassword());
    }

    @Test
    void testNullValues() {
        // Act
        registerForm.setName(null);
        registerForm.setEmail(null);
        registerForm.setPassword(null);

        // Assert
        assertNull(registerForm.getName());
        assertNull(registerForm.getEmail());
        assertNull(registerForm.getPassword());
    }

    @Test
    void testEmptyValues() {
        // Act
        registerForm.setName("");
        registerForm.setEmail("");
        registerForm.setPassword("");

        // Assert
        assertEquals("", registerForm.getName());
        assertEquals("", registerForm.getEmail());
        assertEquals("", registerForm.getPassword());
    }

    @Test
    void testCompleteRegistrationData() {
        // Arrange & Act
        registerForm.setName("Complete User");
        registerForm.setEmail("complete@example.com");
        registerForm.setPassword("CompletePass123");

        // Assert
        assertEquals("Complete User", registerForm.getName());
        assertEquals("complete@example.com", registerForm.getEmail());
        assertEquals("CompletePass123", registerForm.getPassword());
    }
}