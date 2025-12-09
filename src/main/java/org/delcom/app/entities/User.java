package org.delcom.app.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "uuid")
    private UUID id;
    private String name;
    @Column(unique = true) private String email;
    private String password;
    private String role; // "BUYER" atau "ADMIN"
    private LocalDateTime createdAt;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); if(role==null) role="BUYER"; }
}