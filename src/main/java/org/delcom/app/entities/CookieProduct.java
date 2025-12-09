package org.delcom.app.entities;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name = "cookie_products")
public class CookieProduct {
    @Id @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "uuid")
    private UUID id;
    private UUID userId;
    private String name;
    private String flavor;
    @Column(columnDefinition = "TEXT") private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer soldCount = 0;
    private String imagePath;

    // Getters & Setters (Generate otomatis di IDE atau ketik manual)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    @PrePersist void onCreate() { if(soldCount==null) soldCount=0; }
}