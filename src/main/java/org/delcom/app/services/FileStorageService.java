package org.delcom.app.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    // Folder tempat simpan gambar
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Tidak bisa membuat folder upload.", ex);
        }
    }

    // Method ini yang dipanggil oleh ProductService
    public String store(MultipartFile file) throws IOException {
        // Bersihkan nama file
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Buat nama unik agar tidak bentrok (misal: gambar.jpg jadi uuid-gambar.jpg)
        String fileName = UUID.randomUUID().toString() + "-" + originalFileName;

        // Lokasi tujuan
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        
        // Copy file ke folder uploads
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}