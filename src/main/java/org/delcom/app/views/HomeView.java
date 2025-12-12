package org.delcom.app.views;

import org.delcom.app.entities.Product;
import org.delcom.app.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component // Menandakan ini adalah komponen Spring agar bisa di-inject
public class HomeView {

    private final ProductService productService;

    // Constructor Injection
    public HomeView(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Mengambil semua daftar menu/produk untuk ditampilkan di halaman Home
     */
    public List<Product> getFeaturedProducts() {
        return productService.findAll();
    }

    /**
     * Contoh method tambahan untuk judul halaman (Opsional)
     */
    public String getPageTitle() {
        return "Selamat Datang di Lapar.id";
    }
}