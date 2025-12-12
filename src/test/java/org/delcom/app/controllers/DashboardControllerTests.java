package org.delcom.app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

public class DashboardControllerTests {

    @Test
    @DisplayName("Pengujian DashboardController (100% Coverage)")
    public void testVariousDashboardController() throws Exception {

        // 1. Setup Mocks Service
        ProductService productService = mock(ProductService.class);
        UserService userService = mock(UserService.class);

        // 2. Setup Controller Manual
        DashboardController dashboardController = new DashboardController();

        // 3. Inject Mocks via Reflection
        injectField(dashboardController, "productService", productService);
        injectField(dashboardController, "userService", userService);

        // 4. Mock Static JwtUtil (Wrap seluruh test karena hampir semua method butuh ini)
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {

            // ==========================================
            // 1. HALAMAN UTAMA DASHBOARD
            // ==========================================
            {
                // Skenario: Token Kosong (Redirect ke login)
                {
                    Model model = mock(Model.class);
                    String token = ""; // Kosong
                    
                    String viewName = dashboardController.dashboard(model, token);
                    assertEquals("redirect:/auth/login", viewName);
                }

                // Skenario: Token Invalid (Redirect ke login)
                {
                    Model model = mock(Model.class);
                    String token = "invalid_token";
                    
                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(false);
                    
                    String viewName = dashboardController.dashboard(model, token);
                    assertEquals("redirect:/auth/login", viewName);
                }

                // Skenario: Token Valid tapi User Null (Redirect ke login)
                {
                    Model model = mock(Model.class);
                    String token = "valid_token";
                    Long userId = 10L;

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(userId);
                    when(userService.getUserById(userId)).thenReturn(null);

                    String viewName = dashboardController.dashboard(model, token);
                    assertEquals("redirect:/auth/login", viewName);
                }

                // Skenario: Sukses (Token Valid & User Ada)
                {
                    Model model = mock(Model.class);
                    String token = "valid_token";
                    Long userId = 1L;
                    User user = new User();
                    List<Product> products = Collections.emptyList();

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(userId);
                    when(userService.getUserById(userId)).thenReturn(user);
                    when(productService.findAll()).thenReturn(products);

                    String viewName = dashboardController.dashboard(model, token);
                    
                    assertEquals("pages/dashboard/index", viewName);
                    verify(model).addAttribute("user", user);
                    verify(model).addAttribute("products", products);
                }
            }

            // ==========================================
            // 2. HALAMAN PROFIL
            // ==========================================
            {
                // Skenario: Tidak Login
                {
                    Model model = mock(Model.class);
                    String token = "";
                    String viewName = dashboardController.profile(model, token);
                    assertEquals("redirect:/auth/login", viewName);
                }

                // Skenario: Sukses
                {
                    Model model = mock(Model.class);
                    String token = "valid_token";
                    User user = new User();

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(1L);
                    when(userService.getUserById(1L)).thenReturn(user);

                    String viewName = dashboardController.profile(model, token);
                    assertEquals("pages/dashboard/profile", viewName);
                    verify(model).addAttribute("user", user);
                }
            }

            // ==========================================
            // 3. FORM TAMBAH (ADD)
            // ==========================================
            {
                // Skenario: Tidak Login
                {
                    Model model = mock(Model.class);
                    String token = "";
                    String viewName = dashboardController.addForm(model, token);
                    assertEquals("redirect:/auth/login", viewName);
                }

                // Skenario: Sukses
                {
                    Model model = mock(Model.class);
                    String token = "valid_token";
                    User user = new User();

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(1L);
                    when(userService.getUserById(1L)).thenReturn(user);

                    String viewName = dashboardController.addForm(model, token);
                    assertEquals("pages/dashboard/form", viewName);
                    verify(model).addAttribute(eq("product"), any(Product.class));
                }
            }

            // ==========================================
            // 4. FORM EDIT
            // ==========================================
            {
                // Skenario: Tidak Login
                {
                    Model model = mock(Model.class);
                    String token = "";
                    String viewName = dashboardController.editForm(1L, model, token);
                    assertEquals("redirect:/auth/login", viewName);
                }

                // Skenario: Sukses
                {
                    Model model = mock(Model.class);
                    String token = "valid_token";
                    Product product = new Product();
                    Long prodId = 5L;

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(1L);
                    when(userService.getUserById(1L)).thenReturn(new User());
                    when(productService.findById(prodId)).thenReturn(product);

                    String viewName = dashboardController.editForm(prodId, model, token);
                    assertEquals("pages/dashboard/form", viewName);
                    verify(model).addAttribute("product", product);
                }
            }

            // ==========================================
            // 5. SIMPAN DATA (SAVE - POST)
            // ==========================================
            {
                // Note: Method saveProduct di controller tidak punya parameter @CookieValue/Token check
                // Reset mock agar bersih dari interaksi sebelumnya
                reset(productService);

                // Skenario: Sukses
                {
                    Product product = new Product();
                    MultipartFile file = mock(MultipartFile.class);

                    // PERBAIKAN DI SINI: Gunakan when...thenReturn (Bukan doNothing)
                    when(productService.saveProduct(product, file)).thenReturn(product);

                    String viewName = dashboardController.saveProduct(product, file);
                    assertEquals("redirect:/dashboard", viewName);
                    verify(productService, times(1)).saveProduct(product, file);
                }
                // Skenario: IOException (Trigger catch block)
                {
                    Product product = new Product();
                    MultipartFile file = mock(MultipartFile.class);

                    // Mock exception
                    doThrow(new IOException("Disk Full")).when(productService).saveProduct(product, file);

                    String viewName = dashboardController.saveProduct(product, file);
                    
                    // Controller menangkap error dan tetap redirect ke dashboard (sesuai kode)
                    // (Biasanya stacktrace dicetak di console)
                    assertEquals("redirect:/dashboard", viewName);
                }
            }

            // ==========================================
            // 6. HAPUS DATA (DELETE)
            // ==========================================
            {
                // Skenario: Tidak Login
                {
                    String token = "";
                    String viewName = dashboardController.delete(1L, token);
                    assertEquals("redirect:/auth/login", viewName);
                    verify(productService, never()).deleteProduct(anyLong());
                }

                // Skenario: Sukses
                {
                    String token = "valid_token";
                    Long prodId = 10L;

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(1L);
                    when(userService.getUserById(1L)).thenReturn(new User());

                    String viewName = dashboardController.delete(prodId, token);
                    assertEquals("redirect:/dashboard", viewName);
                    verify(productService, times(1)).deleteProduct(prodId);
                }
            }

            // ==========================================
            // 7. HALAMAN DETAIL
            // ==========================================
            {
                // Skenario: Tidak Login
                {
                    Model model = mock(Model.class);
                    String token = "";
                    String viewName = dashboardController.detail(1L, model, token);
                    assertEquals("redirect:/auth/login", viewName);
                }

                // Skenario: Login Sukses, tapi Produk Tidak Ditemukan (Null)
                {
                    Model model = mock(Model.class);
                    String token = "valid_token";
                    Long prodId = 99L;

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(1L);
                    when(userService.getUserById(1L)).thenReturn(new User());
                    
                    // Product Service return null
                    when(productService.findById(prodId)).thenReturn(null);

                    String viewName = dashboardController.detail(prodId, model, token);
                    assertEquals("redirect:/dashboard", viewName); // Logic: if (product == null) redirect dashboard
                    verify(model, never()).addAttribute(anyString(), any());
                }

                // Skenario: Sukses
                {
                    Model model = mock(Model.class);
                    String token = "valid_token";
                    Long prodId = 5L;
                    Product product = new Product();

                    jwtUtilMock.when(() -> JwtUtil.validateToken(token)).thenReturn(true);
                    jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(token)).thenReturn(1L);
                    when(userService.getUserById(1L)).thenReturn(new User());
                    when(productService.findById(prodId)).thenReturn(product);

                    String viewName = dashboardController.detail(prodId, model, token);
                    assertEquals("pages/dashboard/detail", viewName);
                    verify(model).addAttribute("product", product);
                }
            }
        }
    }

    /**
     * Helper method untuk inject field private tanpa Spring Context
     */
    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}