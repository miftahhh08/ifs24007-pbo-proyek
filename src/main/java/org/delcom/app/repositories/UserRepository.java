package org.delcom.app.repositories;

import org.delcom.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // KITA UBAH JADI OPTIONAL AGAR KOMPATIBEL DENGAN LOGIKA LOGIN/REGISTER
    Optional<User> findByEmail(String email);
}