package org.delcom.app.services;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    // Folder upload gambar
    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    /**
     * 1. Cari Produk Milik User (Query Manual)
     */
    public List<Product> findProductsByUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User tidak boleh null");
        }
        return productRepository.findByUserIdCustom(user.getId());
    }

    /**
     * 2. Cari Semua Produk
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    /**
     * 3. Simpan Produk - FIXED VERSION
     * Handle create dan update dengan benar
     */
    public Product saveProduct(Product product, MultipartFile file, User user) throws IOException {
        
        if (product == null) {
            throw new IllegalArgumentException("Product tidak boleh null");
        }
        
        if (user == null) {
            throw new IllegalArgumentException("User tidak boleh null");
        }

        System.out.println(">>> [ProductService] Menyimpan produk: " + product.getName());
        System.out.println(">>> [ProductService] Harga: " + product.getPrice());
        System.out.println(">>> [ProductService] Product ID: " + product.getId());
        
        // A. Set Pemilik (Selalu set user)
        product.setUser(user);

        // B. Logika Gambar
        String existingImageName = null;
        
        // Jika mode EDIT, ambil gambar lama terlebih dahulu
        if (product.getId() != null) {
            Product existingProduct = productRepository.findById(product.getId()).orElse(null);
            if (existingProduct != null) {
                existingImageName = existingProduct.getImage();
                System.out.println(">>> [ProductService] Gambar lama: " + existingImageName);
            }
        }
        
        // Upload gambar baru jika ada
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            
            // Buat folder jika belum ada
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            product.setImage(fileName);
            
            System.out.println(">>> [ProductService] Gambar baru diupload: " + fileName);
            
            // Hapus gambar lama jika ada (opsional)
            if (existingImageName != null && !existingImageName.isEmpty()) {
                try {
                    Path oldFilePath = uploadPath.resolve(existingImageName);
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                        System.out.println(">>> [ProductService] Gambar lama dihapus: " + existingImageName);
                    }
                } catch (IOException e) {
                    System.err.println(">>> [ProductService] Gagal menghapus gambar lama: " + e.getMessage());
                }
            }
        } else {
            // Tidak ada file baru, gunakan gambar lama
            if (existingImageName != null) {
                product.setImage(existingImageName);
                System.out.println(">>> [ProductService] Menggunakan gambar lama: " + existingImageName);
            }
        }
        
        Product savedProduct = productRepository.save(product);
        System.out.println(">>> [ProductService] Produk berhasil disimpan dengan ID: " + savedProduct.getId());
        System.out.println(">>> [ProductService] Harga tersimpan: " + savedProduct.getPrice());
        
        return savedProduct;
    }

    /**
     * 4. Hapus Produk
     * Enhanced dengan delete image file
     */
    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID tidak boleh null");
        }
        
        // Ambil produk terlebih dahulu untuk hapus gambar
        Product product = productRepository.findById(id).orElse(null);
        
        if (product != null) {
            // Hapus file gambar jika ada
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                try {
                    Path filePath = Paths.get(UPLOAD_DIR).resolve(product.getImage());
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        System.out.println(">>> [ProductService] File gambar dihapus: " + product.getImage());
                    }
                } catch (IOException e) {
                    System.err.println(">>> [ProductService] Gagal menghapus file gambar: " + e.getMessage());
                }
            }
            
            // Hapus dari database
            productRepository.deleteById(id);
            System.out.println(">>> [ProductService] Produk ID " + id + " berhasil dihapus");
        }
    }

    /**
     * 5. Cari Produk by ID
     */
    public Product findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID tidak boleh null");
        }
        return productRepository.findById(id).orElse(null);
    }
    
    /**
     * 6. Update Product (tanpa file) - Helper method
     */
    public Product updateProduct(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Product atau ID tidak boleh null");
        }
        
        Product existing = findById(product.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Product dengan ID " + product.getId() + " tidak ditemukan");
        }
        
        // Update field yang diizinkan
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());
        
        return productRepository.save(existing);
    }
}