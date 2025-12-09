package org.delcom.app.controllers;

import org.delcom.app.entities.CookieProduct;
import org.delcom.app.entities.User;
import org.delcom.app.services.CookieService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/shop")
public class ShopController {

    private final CookieService cookieService;

    public ShopController(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    // HALAMAN KATALOG
    @GetMapping
    public String catalog(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            model.addAttribute("user", user);       
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("cookies", cookieService.getAllCookiesForShop());
        return "shop/catalog"; 
    }

    // LOGIKA BELI VIA WHATSAPP
    @PostMapping("/buy/{id}")
    public String buy(@PathVariable UUID id) {
        try {
            // 1. Kurangi Stok di Database (Biar Laporan Dosen jalan)
            cookieService.sellCookie(id, 1);
            
            // 2. Ambil Data Produk
            CookieProduct product = cookieService.getCookieById(id);
            
            // --- GANTI DENGAN NOMOR WA KAMU ---
            String nomorPenjual = "6281234567890"; // Format: 628...
            
            // 3. Buat Pesan WA
            String pesan = "Halo Kak! 👋\n" +
                           "Saya mau pesan menu ini:\n\n" +
                           "🍽️ *Menu:* " + product.getName() + "\n" +
                           "✨ *Varian:* " + product.getFlavor() + "\n" +
                           "💰 *Harga:* Rp " + product.getPrice() + "\n\n" +
                           "Apakah masih tersedia?";

            String encodedPesan = URLEncoder.encode(pesan, StandardCharsets.UTF_8);

            // 4. Buka WhatsApp
            return "redirect:https://wa.me/" + nomorPenjual + "?text=" + encodedPesan;

        } catch (Exception e) {
            return "redirect:/shop?error=stok_habis";
        }
    }
}