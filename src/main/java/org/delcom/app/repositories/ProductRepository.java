package org.delcom.app.repositories;

import org.delcom.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Mencari makanan berdasarkan nama (opsional)
    List<Product> findByNameContaining(String keyword);
}