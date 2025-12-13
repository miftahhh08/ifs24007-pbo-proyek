package org.delcom.app.repositories;

import org.delcom.app.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    
    /**
     * Cari token berdasarkan string token
     */
    Optional<AuthToken> findByToken(String token);
    
    /**
     * Cari token berdasarkan user ID
     */
    Optional<AuthToken> findByUserId(Long userId);
    
    /**
     * Hapus token berdasarkan string token
     */
    void deleteByToken(String token);
    
    /**
     * Hapus semua token milik user tertentu
     */
    void deleteByUserId(Long userId);
}