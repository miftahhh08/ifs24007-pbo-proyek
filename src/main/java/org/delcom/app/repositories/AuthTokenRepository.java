package org.delcom.app.repositories;
import org.delcom.app.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {
    Optional<AuthToken> findByUserIdAndToken(UUID userId, String token);
}