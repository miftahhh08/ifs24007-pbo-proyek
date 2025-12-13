package org.delcom.app.controllers;

import org.delcom.app.entities.Product;
import org.delcom.app.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @InjectMocks
    private ProductController productController;

    @Test
    void testIndex() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        
        when(productService.findAll()).thenReturn(products);

        // Act
        String viewName = productController.index(model);

        // Assert
        // Sesuai dengan screenshot: return "product_index";
        assertEquals("product_index", viewName);
        
        // Verifikasi service dipanggil
        verify(productService).findAll();
        
        // Verifikasi model attribute ditambahkan
        verify(model).addAttribute(eq("products"), eq(products));
    }
}