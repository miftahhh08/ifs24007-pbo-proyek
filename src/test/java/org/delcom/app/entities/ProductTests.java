package org.delcom.app.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductTests {

    @Test
    @DisplayName("Pengujian Entity Product (100% Coverage)")
    public void testProductEntity() {

        // ==========================================
        // 1. PENGUJIAN DEFAULT CONSTRUCTOR & SETTERS
        // ==========================================
        {
            // Panggil Default Constructor
            Product product = new Product();

            // Verifikasi state awal (harus null)
            assertNull(product.getId());
            assertNull(product.getName());
            assertNull(product.getPrice());

            // Data dummy
            Long id = 10L;
            String name = "Nasi Goreng Delcom";
            String description = "Spesial Telur Dadar";
            Double price = 25000.0;
            String image = "nasigoreng.jpg";

            // Eksekusi Setters
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImage(image);

            // Verifikasi Getters (Assert bahwa nilai yang keluar sama dengan yang masuk)
            assertEquals(id, product.getId());
            assertEquals(name, product.getName());
            assertEquals(description, product.getDescription());
            assertEquals(price, product.getPrice());
            assertEquals(image, product.getImage());
        }

        // ==========================================
        // 2. PENGUJIAN PARAMETERIZED CONSTRUCTOR
        // ==========================================
        {
            // Data dummy
            String name = "Ayam Penyet";
            String description = "Sambal Ijo";
            Double price = 18000.0;
            String image = "ayampenyet.jpg";

            // Panggil Constructor dengan parameter
            Product product = new Product(name, description, price, image);

            // Verifikasi Field terisi otomatis lewat constructor
            assertEquals(name, product.getName());
            assertEquals(description, product.getDescription());
            assertEquals(price, product.getPrice());
            assertEquals(image, product.getImage());

            // Pastikan ID null (karena constructor ini tidak set ID)
            assertNull(product.getId());
        }
    }
}