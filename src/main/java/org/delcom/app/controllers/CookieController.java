package org.delcom.app.controllers;
import org.delcom.app.entities.*;
import org.delcom.app.services.CookieService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.io.IOException; import java.util.*;

@Controller @RequestMapping("/cookies")
public class CookieController {
    private final CookieService service;
    public CookieController(CookieService s) { this.service = s; }

    private User getUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth!=null && auth.getPrincipal() instanceof User) ? (User) auth.getPrincipal() : null;
    }

    @GetMapping
    public String index(Model model) {
        User u = getUser();
        if (u == null) return "redirect:/auth/login";
        if ("BUYER".equals(u.getRole())) return "redirect:/shop";
        model.addAttribute("user", u);
        model.addAttribute("cookies", service.getAllCookies(u.getId()));
        return "cookie_index"; // Pastikan nama file HTML sama
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("cookie", new CookieProduct());
        model.addAttribute("user", getUser());
        return "cookie_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CookieProduct c, @RequestParam("imageFile") MultipartFile f) throws IOException {
        service.saveCookie(c, f, getUser().getId());
        return "redirect:/cookies";
    }
    
    // ... Tambahkan method detail, chart, dan api chart (Copy dari chat sebelumnya)
}