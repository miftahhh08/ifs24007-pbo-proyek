package org.delcom.app.controllers;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.services.ProductService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTests {

    @Mock
    private ProductService productService;
    @Mock
    private UserService userService;
    @Mock
    private Model model;

    @InjectMocks
    private DashboardController dashboardController;

    private User testUser;
    private Product testProduct;
    private final String VALID_TOKEN = "valid_token";

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "password");
        testUser.setId(1L);
        testUser.setShopName("My Shop");

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setName("Produk Test");
        testProduct.setUser(testUser);
    }

    private void setupMockUser(MockedStatic<JwtUtil> jwt) {
        jwt.when(() -> JwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
        jwt.when(() -> JwtUtil.getUserIdFromToken(VALID_TOKEN)).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(testUser);
    }

    // --- 1. SAVE PRODUCT (INI KUNCINYA) ---

    @Test
    void testSaveProduct_New() throws IOException {
        testProduct.setId(null);
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);

            assertEquals("redirect:/dashboard", dashboardController.saveProduct(testProduct, file, VALID_TOKEN));
            verify(productService).saveProduct(eq(testProduct), eq(file), eq(testUser));
        }
    }

    @Test
    void testSaveProduct_Edit_NoFile_UseOldImage() throws IOException {
        testProduct.setId(10L);
        Product old = new Product();
        old.setId(10L);
        old.setImage("old.jpg");

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findById(10L)).thenReturn(old);
            
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(true); // File kosong

            dashboardController.saveProduct(testProduct, file, VALID_TOKEN);
            assertEquals("old.jpg", testProduct.getImage());
        }
    }
    
    @Test
    void testSaveProduct_Edit_NoFile_NoOldImage() throws IOException {
        // INI TEST YANG SERING TERLEWAT!
        // Kasus: Edit produk, file upload kosong, DAN produk lama juga tidak punya gambar.
        testProduct.setId(10L);
        Product old = new Product();
        old.setId(10L);
        old.setImage(null); // Gambar lama null

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findById(10L)).thenReturn(old);
            
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(true); // File kosong

            dashboardController.saveProduct(testProduct, file, VALID_TOKEN);
            
            // Verifikasi saveProduct tetap dipanggil (service yang handle null nya)
            verify(productService).saveProduct(eq(testProduct), eq(file), eq(testUser));
        }
    }
    
    @Test
    void testSaveProduct_Edit_WithNewFile() throws IOException {
        testProduct.setId(10L);
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findById(10L)).thenReturn(testProduct);

            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false); // File ada

            dashboardController.saveProduct(testProduct, file, VALID_TOKEN);
            verify(productService).saveProduct(eq(testProduct), eq(file), eq(testUser));
        }
    }

    @Test
    void testSaveProduct_Exception() throws IOException {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            doThrow(new IOException("Error")).when(productService).saveProduct(any(), any(), any());
            assertEquals("redirect:/dashboard", dashboardController.saveProduct(testProduct, mock(MultipartFile.class), VALID_TOKEN));
        }
    }
    
    @Test
    void testSaveProduct_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.saveProduct(new Product(), mock(MultipartFile.class), null));
    }

    // --- 2. EDIT FORM ---

    @Test
    void testEditForm_ProductFound() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findById(10L)).thenReturn(testProduct);
            assertEquals("pages/dashboard/form", dashboardController.editForm(10L, model, VALID_TOKEN));
        }
    }

    @Test
    void testEditForm_ProductNotFound() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findById(99L)).thenReturn(null);
            assertEquals("redirect:/dashboard", dashboardController.editForm(99L, model, VALID_TOKEN));
        }
    }
    
    @Test
    void testEditForm_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.editForm(1L, model, null));
    }

    // --- 3. OTHER METHODS ---

    @Test
    void testDashboard_Success() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findProductsByUser(testUser)).thenReturn(new ArrayList<>());
            assertEquals("pages/dashboard/index", dashboardController.dashboard(model, VALID_TOKEN));
        }
    }

    @Test
    void testDashboard_NoShop() {
        testUser.setShopName(null);
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            assertEquals("redirect:/dashboard/setup-shop", dashboardController.dashboard(model, VALID_TOKEN));
        }
    }
    
    @Test
    void testDashboard_InvalidToken() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.validateToken("invalid")).thenReturn(false);
            assertEquals("redirect:/auth/login", dashboardController.dashboard(model, "invalid"));
        }
    }

    @Test
    void testSetupShopForm() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            assertEquals("pages/dashboard/setup-shop", dashboardController.setupShopForm(model, VALID_TOKEN));
        }
    }
    
    @Test
    void testSetupShopForm_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.setupShopForm(model, null));
    }

    @Test
    void testSaveShopProfile() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            assertEquals("redirect:/dashboard", dashboardController.saveShopProfile(new User(), VALID_TOKEN));
        }
    }
    
    @Test
    void testSaveShopProfile_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.saveShopProfile(new User(), null));
    }

    @Test
    void testDelete() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            assertEquals("redirect:/dashboard", dashboardController.delete(10L, VALID_TOKEN));
        }
    }
    
    @Test
    void testDelete_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.delete(10L, null));
    }

    @Test
    void testDetail_Found() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findById(10L)).thenReturn(testProduct);
            assertEquals("pages/dashboard/detail", dashboardController.detail(10L, model, VALID_TOKEN));
        }
    }

    @Test
    void testDetail_NotFound() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            when(productService.findById(99L)).thenReturn(null);
            assertEquals("redirect:/dashboard", dashboardController.detail(99L, model, VALID_TOKEN));
        }
    }
    
    @Test
    void testDetail_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.detail(10L, model, null));
    }

    @Test
    void testProfile() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            assertEquals("pages/dashboard/profile", dashboardController.profile(model, VALID_TOKEN));
        }
    }
    
    @Test
    void testProfile_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.profile(model, null));
    }

    @Test
    void testAddForm() {
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            setupMockUser(jwt);
            assertEquals("pages/dashboard/form", dashboardController.addForm(model, VALID_TOKEN));
        }
    }
    
    @Test
    void testAddForm_NotLoggedIn() {
        assertEquals("redirect:/auth/login", dashboardController.addForm(model, null));
    }
    
    @Test
    void testHelper_TokenNull() {
        assertEquals("redirect:/auth/login", dashboardController.dashboard(model, null));
    }
    
    @Test
    void testHelper_TokenEmpty() {
        assertEquals("redirect:/auth/login", dashboardController.dashboard(model, ""));
    }
}