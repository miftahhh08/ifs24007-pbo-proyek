package org.delcom.app.repositories;
import org.delcom.app.entities.CookieProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface CookieProductRepository extends JpaRepository<CookieProduct, UUID> {
    List<CookieProduct> findAllByUserId(UUID userId);
}