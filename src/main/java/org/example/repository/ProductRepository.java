package org.example.repository;

import org.example.entity.Product;
import org.example.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByCategory(String category, Pageable pageable);

    // Case-insensitive search across name, sku, category
    @Query("""
            SELECT p FROM Product p
            WHERE (:keyword IS NULL
                   OR LOWER(p.name)     LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.sku)      LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:status   IS NULL OR p.status   = :status)
              AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> search(
            @Param("keyword")  String keyword,
            @Param("status")   ProductStatus status,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}
