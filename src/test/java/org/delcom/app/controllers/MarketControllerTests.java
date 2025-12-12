package org.delcom.app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset; // <--- TAMBAHKAN IMPORT INI
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.services.ProductService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.ui.Model;

public class MarketControllerTests {

    @Test
    @DisplayName("Pengujian MarketController (100% Coverage)")
    public void testVariousMarketController() throws Exception {

        // 1. Setup Mocks Service
        ProductService productService = mock(ProductService.class);
        UserService userService = mock(UserService.class);

        // 2. Setup Controller Manual
        MarketController marketController = new MarketController();

        // 3. Inject Mocks via Reflection
        injectField(marketController, "productService", productService);
        injectField(marketController, "userService", userService);

        // 4. Mock Static JwtUtil
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {

            // ==========================================
            // SKENARIO 1: TOKEN KOSONG
            // ==========================================
            {
                Model model = mock(Model.class);
                String token = "";
                List<Product> products = Collections.emptyList();

                when(productService.findAll()).thenReturn(products);

                String viewName = marketController.index(model, token);

                assertEquals("shop/catalog", viewName);
                verify(model).addAttribute("products", products);
                verify(userService, never()).getUserById(anyLong());
            }

            // ==========================================
            // SKENARIO 2: TOKEN TIDAK VALID
            // ==========================================
            {
                Model model = mock(Model.class);
                String token = "invalid_token";

                jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(false);
                when(productService.findAll()).thenReturn(Collections.emptyList());

                String viewName = marketController.index(model, token);

                assertEquals("shop/catalog", viewName);
                verify(userService, never()).getUserById(anyLong());
            }

            // ==========================================
            // SKENARIO 3: TOKEN VALID & USER DITEMUKAN (SUKSES)
            // ==========================================
            {
                Model model = mock(Model.class);
                String token = "valid_token";
                Long userId = 101L;
                User user = new User();
                List<Product> products = Collections.singletonList(new Product());

                jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(userId);
                when(userService.getUserById(userId)).thenReturn(user);
                when(productService.findAll()).thenReturn(products);

                String viewName = marketController.index(model, token);

                assertEquals("shop/catalog", viewName);
                verify(model).addAttribute("user", user);      
                verify(model).addAttribute("products", products);
            }

            // ==========================================
            // SKENARIO 4: EXCEPTION HANDLING (Catch Block)
            // ==========================================
            {
                // --- PERBAIKAN: RESET MOCK ---
                // Hapus riwayat panggilan dari Skenario 3 agar verify(never()) valid
                reset(userService); 

                Model model = mock(Model.class);
                String token = "error_token";

                // Mock behavior: Validasi true, tapi saat ambil ID error
                jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenThrow(new RuntimeException("Parsing Error"));
                
                when(productService.findAll()).thenReturn(Collections.emptyList());

                String viewName = marketController.index(model, token);

                assertEquals("shop/catalog", viewName);
                
                // Pastikan user service tidak dipanggil karena error terjadi sebelumnya
                // (Tanpa reset(), ini akan gagal karena Skenario 3 sudah memanggilnya)
                verify(userService, never()).getUserById(anyLong());
                
                verify(model, times(1)).addAttribute(anyString(), any()); // addAttribute("products", ...)
            }
        }
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}