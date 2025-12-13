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
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // Helper
    private User getLoggedInUser(String token) {
        if (token == null || token.isEmpty() || !JwtUtil.validateToken(token)) return null;
        Long userId = JwtUtil.getUserIdFromToken(token);
        return userService.getUserById(userId);
    }

    // 1. DASHBOARD
    @GetMapping
    public String dashboard(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        User user = getLoggedInUser(token);
        if (user == null) return "redirect:/auth/login";

        if (user.getShopName() == null || user.getShopName().isEmpty()) {
            return "redirect:/dashboard/setup-shop";
        }

        model.addAttribute("user", user);
        
        System.out.println(">>> MEMBUKA DASHBOARD UNTUK: " + user.getName() + " (ID: " + user.getId() + ")");
        List<Product> products = productService.findProductsByUser(user);
        System.out.println(">>> JUMLAH PRODUK DITEMUKAN: " + products.size());

        model.addAttribute("products", products);
        return "pages/dashboard/index"; 
    }

    // 2. SAVE PRODUCT - FIXED VERSION
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, 
                              @RequestParam("imageFile") MultipartFile file,
                              @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        
        User userFromToken = getLoggedInUser(token);
        if (userFromToken == null) return "redirect:/auth/login";

        try {
            System.out.println(">>> MULAI SIMPAN PRODUK...");
            System.out.println(">>> Product ID: " + product.getId());
            System.out.println(">>> Old Image Name: " + product.getImage());
            System.out.println(">>> New File Empty?: " + file.isEmpty());
            
            User realUser = userService.getUserById(userFromToken.getId());
            product.setUser(realUser);
            
            // PERBAIKAN: Handle edit dengan gambar lama
            if (product.getId() != null) {
                // Mode EDIT
                Product existingProduct = productService.findById(product.getId());
                
                if (file.isEmpty() && existingProduct != null && existingProduct.getImage() != null) {
                    // Jika tidak upload gambar baru, gunakan gambar lama
                    System.out.println(">>> MENGGUNAKAN GAMBAR LAMA: " + existingProduct.getImage());
                    product.setImage(existingProduct.getImage());
                } else if (!file.isEmpty()) {
                    // Jika upload gambar baru
                    System.out.println(">>> UPLOAD GAMBAR BARU");
                    // ProductService akan handle upload di method saveProduct
                }
            }
            
            productService.saveProduct(product, file, realUser);
            
            System.out.println(">>> SUKSES! Produk " + product.getName() + " disimpan dengan User ID " + realUser.getId());

        } catch (IOException e) {
            System.err.println(">>> ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/dashboard";
    }

    // 3. SETUP SHOP
    @GetMapping("/setup-shop")
    public String setupShopForm(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        User user = getLoggedInUser(token);
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        return "pages/dashboard/setup-shop";
    }

    @PostMapping("/setup-shop/save")
    public String saveShopProfile(@ModelAttribute User updatedUser, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        User user = getLoggedInUser(token);
        if (user == null) return "redirect:/auth/login";
        user.setShopName(updatedUser.getShopName());
        user.setShopDescription(updatedUser.getShopDescription());
        user.setAddress(updatedUser.getAddress());
        user.setWhatsappNumber(updatedUser.getWhatsappNumber());
        userService.saveUser(user); 
        return "redirect:/dashboard";
    }

    // 4. PROFILE
    @GetMapping("/profile")
    public String profile(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        User user = getLoggedInUser(token);
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        model.addAttribute("salesData", Arrays.asList(0, 0, 0, 0, 0)); 
        return "pages/dashboard/profile"; 
    }

    // 5. ADD FORM
    @GetMapping("/add")
    public String addForm(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";
        model.addAttribute("product", new Product());
        return "pages/dashboard/form";
    }

    // 6. EDIT FORM
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";
        Product product = productService.findById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "pages/dashboard/form";
        }
        return "redirect:/dashboard";
    }

    // 7. DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";
        productService.deleteProduct(id);
        return "redirect:/dashboard";
    }

    // 8. DETAIL
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        if (getLoggedInUser(token) == null) return "redirect:/auth/login";
        Product product = productService.findById(id);
        if (product == null) return "redirect:/dashboard";
        model.addAttribute("product", product);
        return "pages/dashboard/detail";
    }
}