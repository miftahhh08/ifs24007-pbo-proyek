package org.delcom.app.services;

import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterForm registerForm;

    @BeforeEach
    void setUp() {
        testUser = new User("Test", "test@example.com", "pass");
        testUser.setId(1L);

        registerForm = new RegisterForm();
        registerForm.setName("New");
        registerForm.setEmail("new@example.com");
        registerForm.setPassword("pass");
    }

    // --- 1. REGISTER ---

    @Test
    void testRegister_Success() throws Exception {
        when(userRepository.findByEmail(registerForm.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.register(registerForm);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_EmailExists() {
        when(userRepository.findByEmail(registerForm.getEmail())).thenReturn(Optional.of(testUser));

        Exception e = assertThrows(Exception.class, () -> userService.register(registerForm));
        assertEquals("Email sudah terdaftar", e.getMessage());
    }

    // --- 2. LOGIN (INI KRUSIAL UNTUK 100%) ---

    @Test
    void testLogin_Success() {
        // Skenario 1: User Ada, Password Benar
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        User result = userService.login("test@example.com", "pass");
        assertNotNull(result);
        assertEquals(testUser, result);
    }

    @Test
    void testLogin_WrongPassword() {
        // Skenario 2: User Ada, Password SALAH
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        User result = userService.login("test@example.com", "wrongpass");
        assertNull(result); // Harus return null
    }

    @Test
    void testLogin_UserNotFound() {
        // Skenario 3: User TIDAK ADA
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        User result = userService.login("unknown@example.com", "pass");
        assertNull(result); // Harus return null
    }

    // --- 3. GET USER BY ID ---

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User result = userService.getUserById(1L);
        assertNotNull(result);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        User result = userService.getUserById(99L);
        assertNull(result);
    }

    // --- 4. SAVE USER ---

    @Test
    void testSaveUser() {
        when(userRepository.save(testUser)).thenReturn(testUser);
        userService.saveUser(testUser);
        verify(userRepository).save(testUser);
    }
}