package org.delcom.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nama Personal

    @Column(unique = true)
    private String email;

    private String password;

    // --- DATA TOKO (BARU) ---
    private String shopName;        // Nama Toko
    private String shopDescription; // Deskripsi Toko
    private String address;         // Alamat Toko
    private String whatsappNumber;  // Nomor WA (Simpan format 628xxx)

    // 1. CONSTRUCTOR KOSONG
    public User() {
    }

    // 2. CONSTRUCTOR ISI (Untuk Register awal)
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // 3. GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Getter & Setter Toko
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopDescription() { return shopDescription; }
    public void setShopDescription(String shopDescription) { this.shopDescription = shopDescription; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWhatsappNumber() { return whatsappNumber; }
    public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }
}