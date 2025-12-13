package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTests {

    @Mock
    private AuthTokenRepository tokenRepository;

    @InjectMocks
    private AuthTokenService tokenService;

    // --- 1. SAVE TOKEN (UPDATE DI SINI) ---
    
    @Test
    void testSaveToken() {
        AuthToken token = new AuthToken();
        token.setUserId(1L); // Set ID
        tokenService.saveToken(token);
        verify(tokenRepository).save(token);
    }

    @Test
    void testSaveToken_WithDeleteException() {
        // INI TEST BARU YANG AKAN MENGHIJAUKAN BLOK CATCH DI method saveToken
        AuthToken token = new AuthToken();
        token.setUserId(1L);

        // Simulasi: Saat mau hapus token lama, terjadi error (misal DB connection glitch)
        // Kita paksa deleteByUserId melempar exception
        doThrow(new RuntimeException("Ignore me")).when(tokenRepository).deleteByUserId(1L);

        // Act: Panggil saveToken
        // Assert: Tidak boleh error (karena exception di-catch/di-ignore di dalam method)
        assertDoesNotThrow(() -> tokenService.saveToken(token));

        // Verify: Pastikan kode tetap lanjut menyimpan token baru meskipun delete gagal
        verify(tokenRepository).save(token);
    }

    // --- 2. FIND BY TOKEN ---
    @Test
    void testFindByToken() {
        AuthToken token = new AuthToken();
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        
        Optional<AuthToken> result = tokenService.findByToken("token123");
        
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    // --- 3. IS VALID TOKEN ---
    @Test
    void testIsValidToken_True() {
        when(tokenRepository.findByToken("valid")).thenReturn(Optional.of(new AuthToken()));
        assertTrue(tokenService.isValidToken("valid"));
    }

    @Test
    void testIsValidToken_False() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        assertFalse(tokenService.isValidToken("invalid"));
    }

    // --- 4. DELETE TOKEN (BY STRING) ---
    
    @Test
    void testDeleteToken_Success() {
        AuthToken token = new AuthToken();
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        doNothing().when(tokenRepository).delete(token);

        tokenService.deleteToken("token123");
        
        verify(tokenRepository).delete(token);
    }
    
    @Test
    void testDeleteToken_NotFound() {
        tokenService.deleteToken(null);
        tokenService.deleteToken("");
        verify(tokenRepository, never()).delete(any());
        
        when(tokenRepository.findByToken("unknown")).thenReturn(Optional.empty());
        tokenService.deleteToken("unknown");
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void testDeleteToken_Exception() {
        AuthToken token = new AuthToken();
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
        
        doThrow(new RuntimeException("DB Error")).when(tokenRepository).delete(any(AuthToken.class));
        
        assertDoesNotThrow(() -> tokenService.deleteToken("token123"));
    }

    // --- 5. DELETE BY USER ID ---

    @Test
    void testDeleteByUserId_Success() {
        doNothing().when(tokenRepository).deleteByUserId(1L);
        tokenService.deleteByUserId(1L);
        verify(tokenRepository).deleteByUserId(1L);
    }

    @Test
    void testDeleteByUserId_Exception() {
        doThrow(new RuntimeException("DB Error")).when(tokenRepository).deleteByUserId(1L);
        assertDoesNotThrow(() -> tokenService.deleteByUserId(1L));
    }
}