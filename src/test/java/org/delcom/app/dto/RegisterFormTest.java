package org.delcom.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class RegisterFormTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==========================================
    // TEST: VALID FORM
    // ==========================================
    @Test
    @DisplayName("Validation Success - All Fields Valid")
    void validation_Success() {
        RegisterForm form = new RegisterForm("Budi", "budi@test.com", "password123");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);
        
        // Harusnya tidak ada error
        assertEquals(0, violations.size());
    }

    // ==========================================
    // TEST: NAME VALIDATION
    // ==========================================
    @Test
    @DisplayName("Validation Fail - Name is Null")
    void validation_Fail_WhenNameIsNull() {
        RegisterForm form = new RegisterForm(null, "valid@test.com", "pass123");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("Nama wajib diisi", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Validation Fail - Name is Empty")
    void validation_Fail_WhenNameIsEmpty() {
        RegisterForm form = new RegisterForm("", "valid@test.com", "pass123");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("Nama wajib diisi", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Validation Fail - Name is Blank")
    void validation_Fail_WhenNameIsBlank() {
        RegisterForm form = new RegisterForm("   ", "valid@test.com", "pass123");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("Nama wajib diisi", violations.iterator().next().getMessage());
    }

    // ==========================================
    // TEST: EMAIL VALIDATION
    // ==========================================
    @Test
    @DisplayName("Validation Fail - Email is Null")
    void validation_Fail_WhenEmailIsNull() {
        RegisterForm form = new RegisterForm("Budi", null, "pass123");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("Email wajib diisi", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Validation Fail - Email is Empty")
    void validation_Fail_WhenEmailIsEmpty() {
        RegisterForm form = new RegisterForm("Budi", "", "pass123");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("Email wajib diisi", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Validation Fail - Email Format Invalid")
    void validation_Fail_WhenEmailFormatInvalid() {
        RegisterForm form = new RegisterForm("Budi", "budi-bukan-email", "pass123");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("Format email tidak valid", violations.iterator().next().getMessage());
    }

    // ==========================================
    // TEST: PASSWORD VALIDATION
    // ==========================================
    @Test
    @DisplayName("Validation Fail - Password is Null")
    void validation_Fail_WhenPasswordIsNull() {
        RegisterForm form = new RegisterForm("Budi", "budi@test.com", null);
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("Password wajib diisi", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Validation Fail - Password is Empty")
    void validation_Fail_WhenPasswordIsEmpty() {
        RegisterForm form = new RegisterForm("Budi", "budi@test.com", "");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Expect 2 errors: @NotBlank and @Size(min=6) because length is 0
        assertEquals(2, violations.size()); 
        // Use anyMatch to find the specific error regardless of order
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password wajib diisi")));
    }

    @Test
    @DisplayName("Validation Fail - Password is Blank")
    void validation_Fail_WhenPasswordIsBlank() {
        RegisterForm form = new RegisterForm("Budi", "budi@test.com", "   ");
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Expect 2 errors: @NotBlank and @Size(min=6) because length is 3 (spaces)
        assertEquals(2, violations.size());
        
        // PERBAIKAN: Menggunakan stream().anyMatch() agar tidak peduli urutan error (NotBlank atau Size duluan)
        boolean hasNotBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password wajib diisi"));
        
        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password minimal 6 karakter"));

        assertTrue(hasNotBlankError, "Harusnya ada error: Password wajib diisi");
        assertTrue(hasSizeError, "Harusnya ada error: Password minimal 6 karakter");
    }

    // ==========================================
    // TEST: MULTIPLE ERRORS
    // ==========================================
    @Test
    @DisplayName("Validation Fail - All Fields Null")
    void validation_Fail_WhenAllFieldsNull() {
        RegisterForm form = new RegisterForm(null, null, null);
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertEquals(3, violations.size());
    }
}