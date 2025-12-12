package org.delcom.app.repositories;

import org.delcom.app.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Pastikan <AuthToken, Long>
@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    AuthToken findByToken(String token);
}