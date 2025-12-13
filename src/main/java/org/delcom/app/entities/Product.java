package org.delcom.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        
    private String description; 
    private Double price;       
    private String image;       

    // RELASI KE USER (PEMILIK TOKO)
    // Ubah LAZY menjadi EAGER agar data user langsung terbaca saat produk diambil
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "user_id") 
    private User user;

    public Product() {}

    public Product(String name, String description, Double price, String image, User user) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.user = user;
    }

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}