package org.example.service.impl;

import org.example.dto.request.CreateProductRequest;
import org.example.dto.request.UpdateProductRequest;
import org.example.dto.response.PagedResponse;
import org.example.dto.response.ProductResponse;
import org.example.entity.Product;
import org.example.enums.ProductStatus;
import org.example.exception.DuplicateResourceException;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.ProductMapper;
import org.example.repository.ProductRepository;
import org.example.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // default read-only; writes override below
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper     productMapper;

    // ── CREATE ───────────────────────────────────────────────────

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        log.info("Creating product with SKU: {}", request.getSku());

        String normalizedSku = request.getSku().trim().toUpperCase();

        if (productRepository.existsBySku(normalizedSku)) {
            throw new DuplicateResourceException("Product", "SKU", normalizedSku);
        }

        Product product   = productMapper.toEntity(request);
        Product saved     = productRepository.save(product);

        log.info("Product created with id: {}", saved.getId());
        return productMapper.toResponse(saved);
    }

    // ── READ ─────────────────────────────────────────────────────

    @Override
    public ProductResponse findById(Long id) {
        log.debug("Fetching product by id: {}", id);
        Product product = getProductOrThrow(id);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse findBySku(String sku) {
        log.debug("Fetching product by SKU: {}", sku);
        Product product = productRepository.findBySku(sku.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "SKU", sku));
        return productMapper.toResponse(product);
    }

    @Override
    public PagedResponse<ProductResponse> findAll(Pageable pageable) {
        log.debug("Fetching all products, page: {}", pageable.getPageNumber());
        Page<ProductResponse> page = productRepository
                .findAll(pageable)
                .map(productMapper::toResponse);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ProductResponse> search(
            String keyword,
            ProductStatus status,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        log.debug("Searching products — keyword: {}, status: {}, category: {}", keyword, status, category);

        Page<ProductResponse> page = productRepository
                .search(keyword, status, category, minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);

        return PagedResponse.of(page);
    }

    // ── UPDATE ───────────────────────────────────────────────────

    @Override
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        log.info("Updating product id: {}", id);

        Product product = getProductOrThrow(id);
        productMapper.updateEntity(product, request);
        Product updated = productRepository.save(product);

        log.info("Product updated id: {}", updated.getId());
        return productMapper.toResponse(updated);
    }

    // ── DELETE ───────────────────────────────────────────────────

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting product id: {}", id);

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted id: {}", id);
    }

    // ── Private helpers ──────────────────────────────────────────

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }
}
