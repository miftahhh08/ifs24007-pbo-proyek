package org.delcom.app.repositories;

import org.delcom.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // QUERY MANUAL: Paksa cari berdasarkan angka ID User
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId")
    List<Product> findByUserIdCustom(@Param("userId") Long userId);
}