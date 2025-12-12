package org.delcom.app.repositories;

import org.delcom.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Perhatikan bagian ini: <User, Long> BUKAN <User, UUID>
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}