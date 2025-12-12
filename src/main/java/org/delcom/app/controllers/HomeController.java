package org.delcom.app.controllers;

import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model, 
                        @CookieValue(value = "AUTH_TOKEN", defaultValue = "") String token) {
        
        System.out.println("--- AKSES HOME ---");
        
        // 1. Cek keberadaan Token
        if (token.isEmpty()) {
            System.out.println("Status: Tidak ada token (Guest Mode)");
            return "home";
        }

        System.out.println("Token ditemukan: " + token.substring(0, 10) + "...");

        // 2. Validasi Token
        if (!JwtUtil.validateToken(token)) {
            System.out.println("Status: Token TIDAK VALID / Expired");
            return "home";
        }

        // 3. Ambil ID & User
        try {
            Long userId = JwtUtil.getUserIdFromToken(token);
            System.out.println("User ID dari Token: " + userId);

            User user = userService.getUserById(userId);
            
            if (user != null) {
                System.out.println("User ditemukan di DB: " + user.getName());
                model.addAttribute("user", user);
            } else {
                System.out.println("Status: User ID " + userId + " tidak ditemukan di database!");
            }
        } catch (Exception e) {
            System.out.println("Error saat mengambil user: " + e.getMessage());
            e.printStackTrace();
        }

        return "home"; 
    }
}