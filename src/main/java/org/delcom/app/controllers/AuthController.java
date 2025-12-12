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
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "pages/auth/login"; 
    }

    // 4. PROSES LOGIN
    @PostMapping("/login/post")
    public String loginPost(@Valid @ModelAttribute LoginForm form, BindingResult res, HttpServletResponse response) {
        if (res.hasErrors()) return "pages/auth/login";

        try {
            User user = userService.login(form.getEmail(), form.getPassword());
            
            // Jika user tidak ditemukan atau password salah
            if (user == null) return "redirect:/auth/login?error";

            // Buat Token
            String tokenStr = JwtUtil.generateToken(user.getId());
            tokenService.saveToken(new AuthToken(user.getId(), tokenStr));

            // --- PERBAIKAN DI SINI ---
            Cookie httpCookie = new Cookie("AUTH_TOKEN", tokenStr);
            httpCookie.setPath("/");
            httpCookie.setHttpOnly(true);
            
            // UBAH INI: Set ke -1 agar cookie hilang saat browser ditutup
            // Atau set ke waktu pendek misal 3600 (1 jam)
            httpCookie.setMaxAge(-1); 
            
            response.addCookie(httpCookie);

            return "redirect:/"; // Masuk ke Home

        } catch (Exception e) {
            e.printStackTrace(); 
            return "redirect:/auth/login?error=server_error";
        }
    }
    
    // 5. PROSES LOGOUT (PENTING)
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Timpa cookie lama dengan cookie kosong yang umurnya 0 detik
        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Langsung hapus
        response.addCookie(cookie);
        
        return "redirect:/auth/login"; // Kembali ke halaman login
    }
}