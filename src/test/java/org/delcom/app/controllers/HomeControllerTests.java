package org.delcom.app.controllers;

import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    private final String VALID_TOKEN = "valid_token_abc";
    private final String INVALID_TOKEN = "invalid_token_xyz";

    // --- SKENARIO 1: Token Kosong (Guest Mode) ---
    // Meng-cover: if (token.isEmpty())
    @Test
    void testIndex_TokenEmpty() {
        String view = homeController.index(model, "");
        
        assertEquals("home", view);
        verify(userService, never()).getUserById(any());
    }

    // --- SKENARIO 2: Token Tidak Valid ---
    // Meng-cover: if (!JwtUtil.validateToken(token))
    @Test
    void testIndex_TokenInvalid() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.validateToken(INVALID_TOKEN)).thenReturn(false);

            String view = homeController.index(model, INVALID_TOKEN);

            assertEquals("home", view);
            // Pastikan tidak lanjut mengambil ID
            jwt.verify(() -> JwtUtil.getUserIdFromToken(anyString()), never());
        }
    }

    // --- SKENARIO 3: Sukses (User Ditemukan) ---
    // Meng-cover: if (user != null)
    @Test
    void testIndex_Success_UserFound() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            // 1. Mock JwtUtil
            jwt.when(() -> JwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
            jwt.when(() -> JwtUtil.getUserIdFromToken(VALID_TOKEN)).thenReturn(100L);

            // 2. Mock UserService return User
            User mockUser = new User();
            mockUser.setName("Budi");
            when(userService.getUserById(100L)).thenReturn(mockUser);

            // Act
            String view = homeController.index(model, VALID_TOKEN);

            // Assert
            assertEquals("home", view);
            verify(model).addAttribute("user", mockUser); // Pastikan user masuk model
        }
    }

    // --- SKENARIO 4: User Tidak Ditemukan di DB ---
    // Meng-cover: else { ... tidak ditemukan ... }
    @Test
    void testIndex_UserNotFoundInDb() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
            jwt.when(() -> JwtUtil.getUserIdFromToken(VALID_TOKEN)).thenReturn(100L);

            // Mock UserService return NULL
            when(userService.getUserById(100L)).thenReturn(null);

            String view = homeController.index(model, VALID_TOKEN);

            assertEquals("home", view);
            verify(model, never()).addAttribute(eq("user"), any());
        }
    }

    // --- SKENARIO 5: Error / Exception ---
    // Meng-cover: catch (Exception e)
    @Test
    void testIndex_Exception() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
            jwt.when(() -> JwtUtil.getUserIdFromToken(VALID_TOKEN)).thenReturn(100L);

            // Paksa Service Error
            when(userService.getUserById(100L)).thenThrow(new RuntimeException("Database Down"));

            String view = homeController.index(model, VALID_TOKEN);

            // Controller harus tetap return "home" walau error (karena ada try-catch)
            assertEquals("home", view);
        }
    }
}