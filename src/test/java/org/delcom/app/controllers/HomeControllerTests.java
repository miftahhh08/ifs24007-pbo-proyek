package org.delcom.app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset; // <--- TAMBAHKAN IMPORT INI
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.ui.Model;

public class HomeControllerTests {

    @Test
    @DisplayName("Pengujian HomeController (100% Coverage)")
    public void testVariousHomeController() throws Exception {

        // 1. Setup Mocks Service
        UserService userService = mock(UserService.class);

        // 2. Setup Controller Manual
        HomeController homeController = new HomeController();

        // 3. Inject Mocks via Reflection
        injectField(homeController, "userService", userService);

        // 4. Mock Static JwtUtil
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {

            // ==========================================
            // SKENARIO 1: TOKEN KOSONG (Guest Mode)
            // ==========================================
            {
                Model model = mock(Model.class);
                String token = "";

                String viewName = homeController.index(model, token);

                assertEquals("home", viewName);
                jwtUtilMock.verify(() -> JwtUtil.validateToken(any()), never());
                verify(userService, never()).getUserById(anyLong());
            }

            // ==========================================
            // SKENARIO 2: TOKEN TIDAK VALID
            // ==========================================
            {
                Model model = mock(Model.class);
                String token = "invalid_token_xyz";
                jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(false);

                String viewName = homeController.index(model, token);

                assertEquals("home", viewName);
                verify(userService, never()).getUserById(anyLong());
            }

            // ==========================================
            // SKENARIO 3: TOKEN VALID, TAPI USER TIDAK DITEMUKAN
            // ==========================================
            {
                Model model = mock(Model.class);
                String token = "valid_token_but_no_user";
                Long userId = 99L;

                jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(userId);
                when(userService.getUserById(userId)).thenReturn(null);

                String viewName = homeController.index(model, token);
                assertEquals("home", viewName);
                verify(userService, times(1)).getUserById(userId);
            }

            // ==========================================
            // SKENARIO 4: SUKSES (TOKEN VALID & USER ADA)
            // ==========================================
            {
                // RESET DULU AGAR INTERAKSI SKENARIO 3 HILANG
                reset(userService); 

                Model model = mock(Model.class);
                String token = "valid_token_success";
                Long userId = 1L;
                User user = new User();
                user.setName("Test User");

                jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(userId);
                when(userService.getUserById(userId)).thenReturn(user);

                String viewName = homeController.index(model, token);
                assertEquals("home", viewName);
                verify(model, times(1)).addAttribute("user", user);
            }

            // ==========================================
            // SKENARIO 5: EXCEPTION HANDLING (Catch Block)
            // ==========================================
            {
                // --- PERBAIKAN DI SINI: RESET MOCK ---
                // Kita harus menghapus riwayat pemanggilan dari Skenario 3 & 4
                // agar verify(never()) di bawah berhasil.
                reset(userService);

                Model model = mock(Model.class);
                String token = "error_token";

                // Mock JwtUtil valid, tapi saat ambil ID error
                jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenThrow(new RuntimeException("Parsing Failed"));

                String viewName = homeController.index(model, token);

                assertEquals("home", viewName);

                // Verifikasi: Catch block tereksekusi, sehingga service TIDAK BOLEH dipanggil
                // Karena sudah di-reset di atas, Mockito tidak akan komplain soal Skenario 3 & 4
                verify(userService, never()).getUserById(anyLong());
            }
        }
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}