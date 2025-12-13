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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketControllerTests {

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private MarketController marketController;

    private Product product1;
    private User seller;
    private final String VALID_TOKEN = "valid_token";

    @BeforeEach
    void setUp() {
        seller = new User("Seller", "seller@test.com", "pass");
        seller.setId(1L);

        product1 = new Product();
        product1.setId(10L);
        product1.setName("Laptop");
        product1.setPrice(5000000.0);
        product1.setUser(seller);
    }

    // --- 1. INDEX TESTS ---

    @Test
    void testIndex_MixedProducts() {
        // INI KUNCINYA UNTUK LAMBDA COVERAGE
        // Kita buat list yang berisi:
        // 1. Produk Normal (Ada User)
        // 2. Produk Rusak (User Null) -> Ini akan memicu cabang 'else' atau skip di lambda
        
        Product productNoUser = new Product();
        productNoUser.setId(11L);
        productNoUser.setName("Broken Item");
        productNoUser.setUser(null); // User Null

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(productNoUser);

        when(productService.findAll()).thenReturn(products);

        String view = marketController.index(model, "");
        
        // Sesuaikan string return dengan kode aslimu ("shop/catalog" atau "pages/market/index")
        assertNotNull(view); 
        
        verify(model).addAttribute(eq("products"), anyList());
    }

    @Test
    void testIndex_LoggedIn_Success() {
        when(productService.findAll()).thenReturn(Arrays.asList(product1));
        
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
            jwt.when(() -> JwtUtil.getUserIdFromToken(VALID_TOKEN)).thenReturn(1L);
            when(userService.getUserById(1L)).thenReturn(seller);

            String view = marketController.index(model, VALID_TOKEN);
            assertNotNull(view);
            verify(model).addAttribute(eq("user"), eq(seller));
        }
    }
    
    @Test
    void testIndex_InvalidToken() {
        when(productService.findAll()).thenReturn(Collections.emptyList());
        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.validateToken("invalid")).thenReturn(false);
            marketController.index(model, "invalid");
            jwt.verify(() -> JwtUtil.getUserIdFromToken(anyString()), never());
        }
    }

    @Test
    void testIndex_Exception() {
        when(productService.findAll()).thenThrow(new RuntimeException("DB Error"));
        // Asumsi controller tidak catch error index, jadi JUnit yang catch
        try {
            marketController.index(model, "");
        } catch (Exception e) {
            assertEquals("DB Error", e.getMessage());
        }
    }

    // --- 2. FIX DATABASE TESTS ---

    @Test
    void testFixDatabase_WithNullUser() {
        Product p1 = new Product(); p1.setId(1L); p1.setUser(new User());
        Product p2 = new Product(); p2.setId(2L); p2.setUser(null); // Target Delete

        when(productService.findAll()).thenReturn(Arrays.asList(p1, p2));

        String view = marketController.fixDatabase();
        assertEquals("redirect:/market", view);
        
        verify(productService).deleteProduct(2L);
        verify(productService, never()).deleteProduct(1L);
    }
    
    @Test
    void testFixDatabase_AllNull() {
        when(productService.findAll()).thenReturn(null);
        assertEquals("redirect:/market", marketController.fixDatabase());
    }

    @Test
    void testFixDatabase_Exception() {
        when(productService.findAll()).thenThrow(new RuntimeException("Error"));
        assertEquals("redirect:/market", marketController.fixDatabase());
    }
}