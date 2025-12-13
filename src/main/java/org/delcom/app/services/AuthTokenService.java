package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    /**
     * Simpan token baru ke database
     */
    @Transactional
    public void saveToken(AuthToken authToken) {
        // Hapus token lama user ini dulu (jika ada)
        try {
            authTokenRepository.deleteByUserId(authToken.getUserId());
        } catch (Exception e) {
            // Ignore jika tidak ada token lama
        }
        
        // Simpan token baru
        authTokenRepository.save(authToken);
        System.out.println(">>> TOKEN DISIMPAN: User ID = " + authToken.getUserId());
    }

    /**
     * Cari token berdasarkan string token
     */
    public Optional<AuthToken> findByToken(String token) {
        return authTokenRepository.findByToken(token);
    }

    /**
     * Validasi apakah token ada di database
     */
    public boolean isValidToken(String token) {
        return authTokenRepository.findByToken(token).isPresent();
    }

    /**
     * Hapus token berdasarkan string token
     */
    @Transactional
    public void deleteToken(String token) {
        if (token == null || token.isEmpty()) {
            return; // Tidak ada token untuk dihapus
        }
        
        try {
            Optional<AuthToken> authToken = authTokenRepository.findByToken(token);
            if (authToken.isPresent()) {
                authTokenRepository.delete(authToken.get());
                System.out.println(">>> TOKEN DIHAPUS DARI DATABASE");
            }
        } catch (Exception e) {
            System.err.println(">>> ERROR HAPUS TOKEN: " + e.getMessage());
        }
    }

    /**
     * Hapus semua token milik user tertentu
     */
    @Transactional
    public void deleteByUserId(Long userId) {
        try {
            authTokenRepository.deleteByUserId(userId);
            System.out.println(">>> SEMUA TOKEN USER ID " + userId + " DIHAPUS");
        } catch (Exception e) {
            System.err.println(">>> ERROR HAPUS TOKEN BY USER ID: " + e.getMessage());
        }
    }
}