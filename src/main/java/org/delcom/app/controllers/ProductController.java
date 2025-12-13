package org.delcom.app.controllers;

import org.delcom.app.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // Hanya untuk menampilkan semua produk (Public View)
    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", service.findAll());
        return "product_index"; 
    }
    
    // HAPUS method add() dan save() dari sini karena sudah dipindah ke DashboardController
}