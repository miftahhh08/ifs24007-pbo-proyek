package org.delcom.app.services;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("User Test");

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setName("Produk Test");
        testProduct.setPrice(100.0);
        testProduct.setUser(testUser);
    }

    // ==========================================
    // 1. VALIDATION TESTS (INI YANG MERAH DI SCREENSHOT)
    // ==========================================

    @Test
    void testSaveProduct_ProductNull() {
        // Menguji if (product == null)
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            productService.saveProduct(null, null, testUser)
        );
        assertEquals("Product tidak boleh null", e.getMessage());
    }

    @Test
    void testSaveProduct_UserNull() {
        // Menguji if (user == null)
        // Product harus ada isinya supaya lolos cek pertama, tapi user null
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            productService.saveProduct(testProduct, null, null)
        );
        assertEquals("User tidak boleh null", e.getMessage());
    }

    @Test
    void testFindProductsByUser_Null() {
        assertThrows(IllegalArgumentException.class, () -> productService.findProductsByUser(null));
        assertThrows(IllegalArgumentException.class, () -> productService.findProductsByUser(new User())); // ID null
    }

    @Test
    void testDeleteProduct_NullId() {
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(null));
    }

    @Test
    void testFindById_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> productService.findById(null));
    }
    
    @Test
    void testUpdateProduct_NullChecks() {
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(null));
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(new Product())); // ID Null
    }

    // ==========================================
    // 2. LOGIC TESTS (SAVE, DELETE, FIND)
    // ==========================================

    @Test
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(new ArrayList<>());
        assertNotNull(productService.findAll());
    }
    
    @Test
    void testFindById() {
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        assertNotNull(productService.findById(10L));
    }
    
    @Test
    void testFindProductsByUser() {
        when(productRepository.findByUserIdCustom(1L)).thenReturn(new ArrayList<>());
        assertNotNull(productService.findProductsByUser(testUser));
    }

    // --- DELETE PRODUCT ---

    @Test
    void testDeleteProduct_Success_WithImage() {
        testProduct.setImage("img.jpg");
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).deleteById(10L);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class);
             MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get(anyString())).thenReturn(mockPath);
            when(mockPath.resolve(anyString())).thenReturn(mockPath);
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            
            productService.deleteProduct(10L);
            
            filesMock.verify(() -> Files.delete(any(Path.class)), times(1));
        }
    }

    @Test
    void testDeleteProduct_ImageDeleteFail() {
        testProduct.setImage("img.jpg");
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).deleteById(10L);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class);
             MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get(anyString())).thenReturn(mockPath);
            when(mockPath.resolve(anyString())).thenReturn(mockPath);
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            
            filesMock.when(() -> Files.delete(any(Path.class))).thenThrow(new IOException("Locked"));
            
            assertDoesNotThrow(() -> productService.deleteProduct(10L));
            verify(productRepository).deleteById(10L);
        }
    }

    // --- SAVE PRODUCT ---

    @Test
    void testSaveProduct_NewFile_CreateDir() throws IOException {
        testProduct.setId(null);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("new.jpg");
        when(file.getBytes()).thenReturn("content".getBytes());
        when(productRepository.save(any())).thenReturn(testProduct);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class);
             MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get(anyString())).thenReturn(mockPath);
            when(mockPath.resolve(anyString())).thenReturn(mockPath);
            
            // FOLDER BELUM ADA
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            
            productService.saveProduct(testProduct, file, testUser);
            
            filesMock.verify(() -> Files.createDirectories(any(Path.class)));
        }
    }
    
    @Test
    void testSaveProduct_EditWithFile_DeleteOldSuccess() throws IOException {
        testProduct.setId(10L);
        Product old = new Product(); old.setId(10L); old.setImage("old.jpg");
        
        when(productRepository.findById(10L)).thenReturn(Optional.of(old));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("new.jpg");
        when(file.getBytes()).thenReturn("content".getBytes());

        try (MockedStatic<Files> filesMock = mockStatic(Files.class);
             MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get(anyString())).thenReturn(mockPath);
            when(mockPath.resolve(anyString())).thenReturn(mockPath);
            
            // FILE LAMA ADA
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            
            productService.saveProduct(testProduct, file, testUser);
            
            filesMock.verify(() -> Files.delete(any(Path.class)));
        }
    }
    
    @Test
    void testSaveProduct_EditWithFile_DeleteOldFail() throws IOException {
        testProduct.setId(10L);
        Product old = new Product(); old.setId(10L); old.setImage("old.jpg");
        when(productRepository.findById(10L)).thenReturn(Optional.of(old));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("new.jpg");
        when(file.getBytes()).thenReturn("content".getBytes());

        try (MockedStatic<Files> filesMock = mockStatic(Files.class);
             MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get(anyString())).thenReturn(mockPath);
            when(mockPath.resolve(anyString())).thenReturn(mockPath);
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            
            // ERROR DELETE
            filesMock.when(() -> Files.delete(any(Path.class))).thenThrow(new IOException("Cannot delete"));

            assertDoesNotThrow(() -> productService.saveProduct(testProduct, file, testUser));
        }
    }
    
    @Test
    void testSaveProduct_NoFile_ExistingImageNotNull() throws IOException {
        testProduct.setId(10L);
        testProduct.setImage(null);
        
        Product old = new Product(); old.setId(10L); old.setImage("keep_me.jpg");
        
        when(productRepository.findById(10L)).thenReturn(Optional.of(old));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        productService.saveProduct(testProduct, null, testUser);
        
        assertEquals("keep_me.jpg", testProduct.getImage());
    }

    // --- UPDATE HELPER ---

    @Test
    void testUpdateProduct() {
        Product p = new Product(); p.setId(10L); p.setName("Up");
        
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        productService.updateProduct(p);
        
        assertEquals("Up", testProduct.getName());
    }
    
    @Test
    void testUpdateProduct_NotFound() {
        Product p = new Product(); p.setId(99L);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(p));
    }
}