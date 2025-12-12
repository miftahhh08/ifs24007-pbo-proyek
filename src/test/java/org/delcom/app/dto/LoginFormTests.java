package org.delcom.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginFormTests {

    @Test
    @DisplayName("Pengujian LoginForm (100% Coverage)")
    public void testLoginForm() {
        
        // ==========================================
        // SKENARIO: GETTER & SETTER
        // ==========================================
        {
            // 1. Inisialisasi Object
            LoginForm form = new LoginForm();
            
            // 2. Cek Kondisi Awal (Harus Null)
            assertNull(form.getEmail());
            assertNull(form.getPassword());

            // 3. Set Data (Setter)
            String expectedEmail = "test@delcom.org";
            String expectedPassword = "password123";

            form.setEmail(expectedEmail);
            form.setPassword(expectedPassword);

            // 4. Ambil Data (Getter) & Verifikasi
            assertEquals(expectedEmail, form.getEmail());
            assertEquals(expectedPassword, form.getPassword());
        }
    }
}