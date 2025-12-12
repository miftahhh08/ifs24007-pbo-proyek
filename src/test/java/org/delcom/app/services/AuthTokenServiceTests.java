package org.delcom.app.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AuthTokenServiceTests {

    @Test
    @DisplayName("Pengujian AuthTokenService (100% Coverage)")
    public void testAuthTokenService() {

        // 1. Setup Mock Repository
        AuthTokenRepository tokenRepository = mock(AuthTokenRepository.class);

        // 2. Setup Service (Constructor Injection)
        // Ini sekaligus menguji Constructor
        AuthTokenService service = new AuthTokenService(tokenRepository);

        // ==========================================
        // SKENARIO: SAVE TOKEN
        // ==========================================
        {
            // Dummy Data
            AuthToken token = new AuthToken();
            token.setUserId(1L);
            token.setToken("sample-jwt-token");

            // Execute Method
            service.saveToken(token);

            // Verify Interaction
            // Pastikan method repository.save(token) terpanggil tepat 1 kali
            verify(tokenRepository, times(1)).save(token);
        }
    }
}