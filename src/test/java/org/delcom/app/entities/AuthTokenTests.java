package org.delcom.app.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AuthTokenTests {

    @Test
    @DisplayName("Pengujian Entity AuthToken (100% Coverage)")
    public void testAuthTokenEntity() {

        // ==========================================
        // 1. PENGUJIAN DEFAULT CONSTRUCTOR & SETTER
        // ==========================================
        {
            // Panggil Default Constructor
            AuthToken authToken = new AuthToken();

            // Verifikasi nilai awal (seharusnya null)
            assertNull(authToken.getId());
            assertNull(authToken.getUserId());
            assertNull(authToken.getToken());

            // Uji Setter
            Long id = 1L;
            Long userId = 100L;
            String tokenStr = "sample-token-123";

            authToken.setId(id);
            authToken.setUserId(userId);
            authToken.setToken(tokenStr);

            // Uji Getter (Verifikasi nilai masuk)
            assertEquals(id, authToken.getId());
            assertEquals(userId, authToken.getUserId());
            assertEquals(tokenStr, authToken.getToken());
        }

        // ==========================================
        // 2. PENGUJIAN PARAMETERIZED CONSTRUCTOR
        // ==========================================
        {
            Long userId = 200L;
            String tokenStr = "another-token-456";

            // Panggil Constructor dengan parameter
            AuthToken authToken = new AuthToken(userId, tokenStr);

            // Verifikasi bahwa field terisi dengan benar
            // ID null karena tidak diset di constructor (biasanya auto-generated DB)
            assertNull(authToken.getId()); 
            assertEquals(userId, authToken.getUserId());
            assertEquals(tokenStr, authToken.getToken());
        }
    }
}