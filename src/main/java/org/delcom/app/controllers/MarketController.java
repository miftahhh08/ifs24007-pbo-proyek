package org.delcom.app.controllers;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.services.ProductService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/market")
public class MarketController {

    private final ProductService productService;
    private final UserService userService;

    public MarketController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public String index(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        try {
            // 1. Cek User (Untuk Navbar)
            User loggedInUser = null;
            if (!token.isEmpty() && JwtUtil.validateToken(token)) {
                Long userId = JwtUtil.getUserIdFromToken(token);
                loggedInUser = userService.getUserById(userId);
            }
            model.addAttribute("user", loggedInUser);

            // 2. Ambil Produk (Safe Mode dengan null check)
            List<Product> allProducts = productService.findAll();
            
            // Tambahkan null check untuk allProducts
            List<Product> safeProducts = new ArrayList<>();
            if (allProducts != null) {
                safeProducts = allProducts.stream()
                        .filter(p -> p != null && p.getUser() != null)
                        .collect(Collectors.toList());
            }
            
            model.addAttribute("products", safeProducts);

           return "shop/catalog";
            
        } catch (Exception e) {
            // Log error untuk debugging
            System.err.println("Error in MarketController: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: set empty list
            model.addAttribute("user", null);
            model.addAttribute("products", new ArrayList<>());
            return "shop/catalog";
        }
    }
    
    // Fitur Reset dengan error handling
    @GetMapping("/reset")
    public String fixDatabase() {
        try {
            List<Product> allProducts = productService.findAll();
            if (allProducts != null) {
                for (Product p : allProducts) {
                    if (p != null && p.getUser() == null) {
                        productService.deleteProduct(p.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error resetting database: " + e.getMessage());
        }
        return "redirect:/market";
    }
}