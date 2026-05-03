package org.example.service;

import org.example.dto.request.CreateProductRequest;
import org.example.dto.request.UpdateProductRequest;
import org.example.dto.response.PagedResponse;
import org.example.dto.response.ProductResponse;
import org.example.enums.ProductStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponse        create(CreateProductRequest request);

    ProductResponse        findById(Long id);

    ProductResponse        findBySku(String sku);

    PagedResponse<ProductResponse> findAll(Pageable pageable);

    PagedResponse<ProductResponse> search(
            String keyword,
            ProductStatus status,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    ProductResponse        update(Long id, UpdateProductRequest request);

    void                   delete(Long id);
}
