package org.delcom.app.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime; // Pastikan import ini ada
import java.util.UUID;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "uuid")
    private UUID id;

    private UUID userId;

    @Column(columnDefinition = "TEXT") 
    private String token;

    // INI YANG TADI ERROR (Ketinggalan diisi)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AuthToken() {
    }

    public AuthToken(UUID userId, String token) {
        this.userId = userId;
        this.token = token;
        // Solusi Cepat: Isi waktu saat objek dibuat
        this.createdAt = LocalDateTime.now(); 
    }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // JAGA-JAGA: Kalau constructor lupa ngisi, ini yang ngisi otomatis sebelum masuk DB
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}