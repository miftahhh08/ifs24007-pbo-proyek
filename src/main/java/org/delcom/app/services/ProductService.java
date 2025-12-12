package org.delcom.app.services;

import org.delcom.app.entities.Product;
import org.delcom.app.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional; // Pastikan import ini ada

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;
    private final FileStorageService fileService; 

    public ProductService(ProductRepository repository, FileStorageService fileService) {
        this.repository = repository;
        this.fileService = fileService;
    }

    // 1. Tampilkan Semua Data
    public List<Product> findAll() {
        return repository.findAll();
    }

    // 2. Simpan Data (Create / Update)
    public Product saveProduct(Product product, MultipartFile file) throws IOException {
        // Jika ada file gambar yang diupload
        if (file != null && !file.isEmpty()) {
            String fileName = fileService.store(file); 
            product.setImage(fileName);                
        }
        return repository.save(product);
    }

    // --- BAGIAN INI YANG KEMARIN BELUM ADA (Tambahkan ini) ---

    // 3. Cari Data Berdasarkan ID (Untuk Edit)
    public Product findById(Long id) {
        // Mengambil data dari database, jika tidak ada kembalikan null
        return repository.findById(id).orElse(null);
    }

    // 4. Hapus Data (Untuk Delete)
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}