package org.delcom.app.controllers;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.services.ProductService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/dashboard") // <--- Ini kuncinya agar link /dashboard jalan
public class DashboardController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // Helper cek login
    private User getLoggedInUser(String token) {
        if (token.isEmpty() || !JwtUtil.validateToken(token)) return null;
        Long userId = JwtUtil.getUserIdFromToken(token);
        return userService.getUserById(userId);
    }

    // 1. HALAMAN UTAMA DASHBOARD (Toko Saya)
    @GetMapping
    public String dashboard(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        User user = getLoggedInUser(token);
        if (user == null) return "redirect:/auth/login"; // Tendang ke login jika belum masuk

        model.addAttribute("user", user);
        model.addAttribute("products", productService.findAll()); 
        
        // MENGARAH KE FILE HTML DI FOLDER: resources/templates/pages/dashboard/index.html
        return "pages/dashboard/index"; 
    }

    // 2. HALAMAN PROFIL (Chart)
    @GetMapping("/profile")
    public String profile(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        User user = getLoggedInUser(token);
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("user", user);
        
        // MENGARAH KE FILE HTML DI FOLDER: resources/templates/pages/dashboard/profile.html
        return "pages/dashboard/profile"; 
    }

    // 3. Form Tambah
    @GetMapping("/add")
    public String addForm(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";
        model.addAttribute("product", new Product());
        return "pages/dashboard/form";
    }

    // 4. Form Edit
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";
        model.addAttribute("product", productService.findById(id)); // Pastikan findById ada di Service
        return "pages/dashboard/form";
    }

    // 5. Simpan Data
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("imageFile") MultipartFile file) {
        try {
            productService.saveProduct(product, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/dashboard";
    }
    
    // 6. Hapus Data
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";
        productService.deleteProduct(id); // Pastikan deleteProduct ada di Service
        return "redirect:/dashboard";
    }
    // 7. HALAMAN DETAIL (View Detail - Poin 7)
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        // Cek Login
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";

        Product product = productService.findById(id);
        
        // Cek jika produk tidak ditemukan (misal ID ngawur)
        if (product == null) return "redirect:/dashboard";

        model.addAttribute("product", product);
        return "pages/dashboard/detail"; // Kita akan buat file ini di langkah 2
    }
}