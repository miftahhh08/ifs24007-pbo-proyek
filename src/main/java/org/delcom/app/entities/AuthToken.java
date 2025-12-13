package org.delcom.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 500)
    private String token;
    
    // Constructor kosong (WAJIB untuk JPA)
    public AuthToken() {
    }
    
    // Constructor dengan parameter
    public AuthToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
    
    // Getters dan Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}