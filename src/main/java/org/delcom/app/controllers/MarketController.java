package org.delcom.app.controllers;

import org.delcom.app.entities.User;
import org.delcom.app.services.ProductService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String index(Model model, @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        // Cek user (agar navbar ada namanya)
        if (!token.isEmpty() && JwtUtil.validateToken(token)) {
            try {
                Long userId = JwtUtil.getUserIdFromToken(token);
                User user = userService.getUserById(userId);
                model.addAttribute("user", user);
            } catch (Exception e) {}
        }

        // Ambil Produk
        model.addAttribute("products", productService.findAll());

        // PENTING: Arahkan ke folder shop/catalog.html
        // JANGAN arahkan ke "market" atau "home"
        return "shop/catalog"; 
    }
}