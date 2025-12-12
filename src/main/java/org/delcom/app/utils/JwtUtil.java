package org.delcom.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // Gunakan kunci yang cukup panjang agar aman dan valid untuk algoritma HS256
    private static final String SECRET_STRING = "LaparIdRahasiaSuperAmanKunciUntukEnkripsiYangSangatPanjang123";
    
    // Buat Key Object yang konsisten
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 86400000L; // 1 Hari

    // Generate Token
    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256) // Gunakan KEY object
                .compact();
    }

    // Ambil ID dari Token
    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder() // Gunakan parserBuilder (versi baru)
                .setSigningKey(KEY)          // Gunakan KEY yang sama
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    // Validasi Token
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Jika token salah/expired, code akan masuk sini
            System.out.println("Token Error: " + e.getMessage()); 
            return false;
        }
    }
}