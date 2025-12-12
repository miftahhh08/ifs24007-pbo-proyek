package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

public class FileStorageServiceTests {

    @Test
    @DisplayName("Pengujian FileStorageService (100% Coverage)")
    public void testVariousFileStorageService() throws IOException {

        // Kita perlu mem-mock Class statis 'Files' karena logic inti ada di sana
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            // ==========================================
            // 1. TEST CONSTRUCTOR - EXCEPTION (GAGAL BUAT FOLDER)
            // ==========================================
            {
                // Simulasi: Saat Files.createDirectories dipanggil, lempar IOException
                filesMock.when(() -> Files.createDirectories(any(Path.class)))
                         .thenThrow(new IOException("Permission Denied"));

                // Verifikasi: Constructor melempar RuntimeException sesuai kode catch block
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                    new FileStorageService();
                });

                assertTrue(exception.getMessage().contains("Tidak bisa membuat folder upload"));
            }

            // ==========================================
            // 2. TEST CONSTRUCTOR - SUKSES
            // ==========================================
            // Reset mock behavior agar tidak error lagi
            filesMock.reset(); 
            // Default behavior mock static void method adalah doNothing(), jadi aman.

            FileStorageService service = new FileStorageService();
            assertNotNull(service);

            // ==========================================
            // 3. TEST STORE - SUKSES
            // ==========================================
            {
                MultipartFile file = mock(MultipartFile.class);
                String originalName = "foto-liburan.jpg";
                InputStream dummyContent = new ByteArrayInputStream("test-content".getBytes());

                // Setup Mock MultipartFile
                when(file.getOriginalFilename()).thenReturn(originalName);
                when(file.getInputStream()).thenReturn(dummyContent);

                // Simulasi Files.copy agar tidak benar-benar copy file ke disk
                filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                         .thenReturn(1L); // return bytes written (dummy)

                // Execute
                String resultFileName = service.store(file);

                // Verify
                assertNotNull(resultFileName);
                // Pastikan format UUID + nama asli (contains karena UUID random)
                assertTrue(resultFileName.contains(originalName));
                assertTrue(resultFileName.length() > originalName.length()); // Pastikan ada tambahan UUID

                // Verifikasi Files.copy dipanggil
                filesMock.verify(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)), times(1));
            }

            // ==========================================
            // 4. TEST STORE - IO EXCEPTION (GAGAL COPY)
            // ==========================================
            {
                MultipartFile file = mock(MultipartFile.class);
                when(file.getOriginalFilename()).thenReturn("error.png");
                
                // Simulasi getInputStream melempar IOException
                when(file.getInputStream()).thenThrow(new IOException("Disk Full"));

                // Execute & Verify
                // Method store melempar IOException (throws IOException)
                assertThrows(IOException.class, () -> {
                    service.store(file);
                });
            }
        }
    }
}