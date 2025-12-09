package org.delcom.app.services;

import org.delcom.app.entities.CookieProduct;
import org.delcom.app.repositories.CookieProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CookieService {

    private final CookieProductRepository repository;
    private final FileStorageService fileStorageService;

    public CookieService(CookieProductRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    // 1. Ambil Kue berdasarkan ID Penjual (Untuk Dashboard Admin)
    public List<CookieProduct> getAllCookies(UUID userId) {
        return repository.findAllByUserId(userId);
    }

    // 2. Ambil SEMUA Kue (Untuk Toko Pembeli)
    public List<CookieProduct> getAllCookiesForShop() {
        return repository.findAll();
    }

    // 3. Ambil detail 1 kue
    public CookieProduct getCookieById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    // 4. Simpan / Update Kue (Termasuk Upload Gambar)
    @Transactional
    public void saveCookie(CookieProduct cookie, MultipartFile imageFile, UUID userId) throws IOException {
        cookie.setUserId(userId);
        
        // Handle Image Upload
        if (imageFile != null && !imageFile.isEmpty()) {
            // Hapus gambar lama jika ada (untuk edit)
            if (cookie.getImagePath() != null) {
                fileStorageService.deleteFile(cookie.getImagePath());
            }
            // Generate dummy ID jika baru (karena ID belum ada saat pre-persist)
            UUID tempId = (cookie.getId() != null) ? cookie.getId() : UUID.randomUUID();
            String filename = fileStorageService.storeFile(imageFile, tempId);
            cookie.setImagePath(filename);
        }

        repository.save(cookie);
    }

    // 5. Hapus Kue
    @Transactional
    public void deleteCookie(UUID id) {
        CookieProduct cookie = getCookieById(id);
        if (cookie != null && cookie.getImagePath() != null) {
            fileStorageService.deleteFile(cookie.getImagePath());
        }
        repository.deleteById(id);
    }

    // 6. FITUR JUAL / BELI (Mengurangi Stok)
    @Transactional
    public void sellCookie(UUID id, int quantity) {
        CookieProduct cookie = getCookieById(id);
        
        // Cek apakah kue ada DAN stok mencukupi
        if (cookie != null && cookie.getStock() >= quantity) {
            // Kurangi stok
            cookie.setStock(cookie.getStock() - quantity);
            
            // Tambah counter terjual
            if (cookie.getSoldCount() == null) cookie.setSoldCount(0);
            cookie.setSoldCount(cookie.getSoldCount() + quantity);
            
            // Simpan perubahan
            repository.save(cookie);
        } else {
            // Jika stok habis
            throw new RuntimeException("Stok habis atau kue tidak ditemukan!");
        }
    }
}