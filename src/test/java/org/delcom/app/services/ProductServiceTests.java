package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset; // WAJIB IMPORT INI
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.delcom.app.entities.Product;
import org.delcom.app.repositories.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

public class ProductServiceTests {

    @Test
    @DisplayName("Pengujian ProductService (100% Coverage)")
    public void testVariousProductService() throws IOException {

        // 1. Setup Mocks
        ProductRepository repository = mock(ProductRepository.class);
        FileStorageService fileService = mock(FileStorageService.class);

        // 2. Instantiate Service
        ProductService service = new ProductService(repository, fileService);

        // ==========================================
        // 1. METHOD: findAll
        // ==========================================
        {
            List<Product> mockList = Collections.singletonList(new Product());
            when(repository.findAll()).thenReturn(mockList);

            List<Product> result = service.findAll();

            assertEquals(1, result.size());
            verify(repository, times(1)).findAll();
        }

        // ==========================================
        // 2. METHOD: saveProduct
        // ==========================================
        {
            // Skenario A: File NULL
            {
                reset(repository); // RESET MOCK AGAR COUNTER JADI 0

                Product product = new Product();
                MultipartFile file = null;

                when(repository.save(product)).thenReturn(product);

                Product result = service.saveProduct(product, file);

                assertNotNull(result);
                verify(fileService, never()).store(any());
                verify(repository, times(1)).save(product);
            }

            // Skenario B: File EMPTY
            {
                reset(repository); // RESET MOCK LAGI

                Product product = new Product();
                MultipartFile file = mock(MultipartFile.class);
                
                when(file.isEmpty()).thenReturn(true); 
                when(repository.save(product)).thenReturn(product);

                service.saveProduct(product, file);

                verify(fileService, never()).store(any());
                
                // KARENA SUDAH DI-RESET, KITA EKSPEKTASI 1 KALI SAJA (BUKAN 2)
                verify(repository, times(1)).save(product); 
            }

            // Skenario C: File VALID
            {
                reset(repository); // RESET MOCK

                Product product = new Product();
                MultipartFile file = mock(MultipartFile.class);
                String generatedFileName = "uuid-gambar.jpg";

                when(file.isEmpty()).thenReturn(false);
                when(fileService.store(file)).thenReturn(generatedFileName);
                when(repository.save(product)).thenReturn(product);

                Product result = service.saveProduct(product, file);

                assertEquals(generatedFileName, result.getImage());
                verify(fileService, times(1)).store(file);
                verify(repository, times(1)).save(product);
            }

            // Skenario D: IOException
            {
                reset(repository); // RESET MOCK

                Product product = new Product();
                MultipartFile file = mock(MultipartFile.class);

                when(file.isEmpty()).thenReturn(false);
                when(fileService.store(file)).thenThrow(new IOException("Disk Full"));

                assertThrows(IOException.class, () -> {
                    service.saveProduct(product, file);
                });
            }
        }

        // ==========================================
        // 3. METHOD: findById
        // ==========================================
        {
            // Skenario A: Ada
            {
                Long id = 1L;
                Product product = new Product();
                when(repository.findById(id)).thenReturn(Optional.of(product));
                
                Product result = service.findById(id);
                assertEquals(product, result);
            }

            // Skenario B: Tidak Ada
            {
                Long id = 99L;
                when(repository.findById(id)).thenReturn(Optional.empty());
                
                Product result = service.findById(id);
                assertNull(result);
            }
        }

        // ==========================================
        // 4. METHOD: deleteProduct
        // ==========================================
        {
            Long id = 5L;
            service.deleteProduct(id);
            verify(repository, times(1)).deleteById(id);
        }
    }
}