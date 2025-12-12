package org.delcom.app.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTests {

    @Test
    @DisplayName("Pengujian Entity User (100% Coverage)")
    public void testUserEntity() {

        // ==========================================
        // 1. PENGUJIAN DEFAULT CONSTRUCTOR & SETTERS
        // ==========================================
        {
            // Panggil Constructor Kosong
            User user = new User();

            // Verifikasi state awal (harus null)
            assertNull(user.getId());
            assertNull(user.getName());
            assertNull(user.getEmail());
            assertNull(user.getPassword());

            // Data dummy
            Long id = 55L;
            String name = "Budi Santoso";
            String email = "budi@delcom.org";
            String password = "rahasia123";

            // Eksekusi Setters
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);

            // Verifikasi Getters (Nilai harus sama dengan yang di-set)
            assertEquals(id, user.getId());
            assertEquals(name, user.getName());
            assertEquals(email, user.getEmail());
            assertEquals(password, user.getPassword());
        }

        // ==========================================
        // 2. PENGUJIAN CONSTRUCTOR DENGAN PARAMETER
        // ==========================================
        {
            // Data dummy
            String name = "Siti Aminah";
            String email = "siti@delcom.org";
            String password = "password456";

            // Panggil Constructor Isi
            User user = new User(name, email, password);

            // Verifikasi Field terisi otomatis lewat constructor
            assertEquals(name, user.getName());
            assertEquals(email, user.getEmail());
            assertEquals(password, user.getPassword());

            // Pastikan ID null (karena constructor ini tidak menyertakan ID)
            assertNull(user.getId());
        }
    }
}