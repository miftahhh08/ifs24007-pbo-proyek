package org.delcom.app.controllers;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm; 
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthTokenService tokenService; 

    // 1. TAMPILKAN FORM REGISTER
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "pages/auth/register"; 
    }

    // 2. PROSES SIMPAN REGISTER
    @PostMapping("/register/save") 
    public String registerSave(@Valid @ModelAttribute RegisterForm form, BindingResult res) {
        if (res.hasErrors()) {
            return "pages/auth/register";
        }
        try {
            userService.register(form); 
            return "redirect:/auth/login?success_register";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/auth/register?error=" + e.getMessage();
        }
    }

    // 3. TAMPILKAN FORM LOGIN
    @GetMapping("/login")
    public String loginForm(Model model, HttpServletResponse response) {
        // Hapus cookie lama saat buka halaman login
        clearAuthCookie(response);
        model.addAttribute("loginForm", new LoginForm());
        return "pages/auth/login"; 
    }

    // 4. PROSES LOGIN - FIXED VERSION
    @PostMapping("/login/post")
    public String loginPost(@Valid @ModelAttribute LoginForm form, 
                           BindingResult res, 
                           HttpServletRequest request,
                           HttpServletResponse response) {
        if (res.hasErrors()) return "pages/auth/login";

        try {
            System.out.println("=".repeat(60));
            System.out.println(">>> PROSES LOGIN DIMULAI");
            System.out.println(">>> Email: " + form.getEmail());
            
            // STEP 1: Hapus semua cookie AUTH_TOKEN yang mungkin ada
            clearAuthCookie(response);
            System.out.println(">>> Cookie lama dihapus");
            
            // STEP 2: Hapus token lama dari database (jika ada)
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("AUTH_TOKEN".equals(cookie.getName()) && !cookie.getValue().isEmpty()) {
                        tokenService.deleteToken(cookie.getValue());
                        System.out.println(">>> Token lama dihapus dari database");
                    }
                }
            }
            
            // STEP 3: Login user
            User user = userService.login(form.getEmail(), form.getPassword());
            
            if (user == null) {
                System.out.println(">>> LOGIN GAGAL: Email/Password salah");
                System.out.println("=".repeat(60));
                return "redirect:/auth/login?error";
            }
            
            System.out.println(">>> LOGIN BERHASIL");
            System.out.println(">>> User ID: " + user.getId());
            System.out.println(">>> User Name: " + user.getName());
            System.out.println(">>> User Email: " + user.getEmail());

            // STEP 4: Generate token baru
            String newToken = JwtUtil.generateToken(user.getId());
            System.out.println(">>> Token baru: " + newToken.substring(0, 30) + "...");
            
            // STEP 5: Simpan token ke database
            tokenService.saveToken(new AuthToken(user.getId(), newToken));
            System.out.println(">>> Token disimpan ke database");

            // STEP 6: Set cookie baru dengan konfigurasi yang benar
            Cookie newCookie = new Cookie("AUTH_TOKEN", newToken);
            newCookie.setPath("/");
            newCookie.setHttpOnly(true);
            newCookie.setMaxAge(86400); // 24 jam
            newCookie.setSecure(false); // Set true jika pakai HTTPS
            response.addCookie(newCookie);
            System.out.println(">>> Cookie baru di-set dengan nilai: " + newToken.substring(0, 30) + "...");
            System.out.println(">>> Cookie Path: " + newCookie.getPath());
            System.out.println(">>> Cookie MaxAge: " + newCookie.getMaxAge());
            System.out.println("=".repeat(60));

            return "redirect:/";

        } catch (Exception e) {
            System.err.println(">>> ERROR LOGIN: " + e.getMessage());
            e.printStackTrace(); 
            System.out.println("=".repeat(60));
            return "redirect:/auth/login?error=server_error";
        }
    }
    
    // 5. PROSES LOGOUT - ENHANCED VERSION
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("=".repeat(60));
        System.out.println(">>> PROSES LOGOUT DIMULAI");
        
        try {
            // Hapus token dari database
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("AUTH_TOKEN".equals(cookie.getName()) && !cookie.getValue().isEmpty()) {
                        tokenService.deleteToken(cookie.getValue());
                        System.out.println(">>> Token dihapus dari database");
                    }
                }
            }
            
            // Hapus cookie
            clearAuthCookie(response);
            System.out.println(">>> Cookie dihapus");
            System.out.println(">>> LOGOUT BERHASIL");
            
        } catch (Exception e) {
            System.err.println(">>> ERROR LOGOUT: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=".repeat(60));
        return "redirect:/auth/login";
    }
    
    // HELPER: Clear Auth Cookie - ENHANCED
    private void clearAuthCookie(HttpServletResponse response) {
        // Clear cookie with null value
        Cookie cookie1 = new Cookie("AUTH_TOKEN", null);
        cookie1.setPath("/");
        cookie1.setHttpOnly(true);
        cookie1.setMaxAge(0);
        response.addCookie(cookie1);
        
        // Clear cookie with empty value (double protection)
        Cookie cookie2 = new Cookie("AUTH_TOKEN", "");
        cookie2.setPath("/");
        cookie2.setHttpOnly(true);
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);
        
        System.out.println(">>> Cookie AUTH_TOKEN dihapus (null + empty)");
    }
}