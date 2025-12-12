package org.delcom.app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.delcom.app.entities.Product;
import org.delcom.app.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

public class ProductControllerTests {

    @Test
    @DisplayName("Pengujian ProductController (100% Coverage)")
    public void testVariousProductController() throws Exception {

        // 1. Setup Mock Service
        ProductService productService = mock(ProductService.class);

        // 2. Setup Controller (Menggunakan Constructor Injection)
        ProductController productController = new ProductController(productService);

        // ==========================================
        // 1. TEST METHOD INDEX (GET /products)
        // ==========================================
        {
            Model model = mock(Model.class);
            List<Product> mockProducts = Collections.singletonList(new Product());

            // Mock Service behavior
            when(productService.findAll()).thenReturn(mockProducts);

            // Execute
            String viewName = productController.index(model);

            // Verify
            assertEquals("product_index", viewName);
            verify(model, times(1)).addAttribute("products", mockProducts);
            verify(productService, times(1)).findAll();
        }

        // ==========================================
        // 2. TEST METHOD ADD (GET /products/add)
        // ==========================================
        {
            Model model = mock(Model.class);

            // Execute
            String viewName = productController.add(model);

            // Verify
            assertEquals("product_form", viewName);
            // Pastikan atribut "product" dengan objek baru ditambahkan ke model
            verify(model, times(1)).addAttribute(eq("product"), any(Product.class));
        }

        // ==========================================
        // 3. TEST METHOD SAVE (POST /products/save)
        // ==========================================
        {
            // Skenario 1: Simpan Sukses
            {
                Product product = new Product();
                MultipartFile file = mock(MultipartFile.class);

                // Mock void method behavior
                when(productService.saveProduct(product, file)).thenReturn(product);

                // Execute
                String viewName = productController.save(product, file);

                // Verify
                assertEquals("redirect:/products", viewName);
                verify(productService, times(1)).saveProduct(product, file);
            }

            // Skenario 2: Simpan Gagal (IOException) -> Masuk Catch Block
            {
                Product product = new Product();
                MultipartFile file = mock(MultipartFile.class);

                // Mock Exception behavior
                doThrow(new IOException("File Error")).when(productService).saveProduct(product, file);

                // Execute
                String viewName = productController.save(product, file);

                // Verify
                // Tetap redirect walaupun error (sesuai kode Anda catch hanya printStackTrace)
                assertEquals("redirect:/products", viewName);
                
                // Memastikan method tetap dipanggil meskipun gagal
                verify(productService, times(1)).saveProduct(product, file);
            doThrow(new IOException("File Error")).when(productService).saveProduct(product, file);
            }
        }
    }
}