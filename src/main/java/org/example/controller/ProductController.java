package org.example.controller;

import org.example.dto.request.CreateProductRequest;
import org.example.dto.request.UpdateProductRequest;
import org.example.dto.response.ApiResponse;
import org.example.dto.response.PagedResponse;
import org.example.dto.response.ProductResponse;
import org.example.enums.ProductStatus;
import org.example.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ── POST /api/v1/products ─────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = productService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    // ── GET /api/v1/products/{id} ─────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(
            @PathVariable Long id) {

        ProductResponse response = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── GET /api/v1/products/sku/{sku} ────────────────────────────
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> findBySku(
            @PathVariable String sku) {

        ProductResponse response = productService.findBySku(sku);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── GET /api/v1/products ──────────────────────────────────────
    // Optional search params: keyword, status, category, minPrice, maxPrice
    // Pagination: page, size, sort
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> findAll(
            @RequestParam(required = false)                        String        keyword,
            @RequestParam(required = false)                        ProductStatus status,
            @RequestParam(required = false)                        String        category,
            @RequestParam(required = false)                        BigDecimal    minPrice,
            @RequestParam(required = false)                        BigDecimal    maxPrice,
            @RequestParam(defaultValue = "0")                      int           page,
            @RequestParam(defaultValue = "10")                     int           size,
            @RequestParam(defaultValue = "createdAt")              String        sortBy,
            @RequestParam(defaultValue = "desc")                   String        sortDir) {

        Sort sort     = sortDir.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        boolean hasFilter = keyword != null || status != null
                            || category != null || minPrice != null || maxPrice != null;

        PagedResponse<ProductResponse> response = hasFilter
                ? productService.search(keyword, status, category, minPrice, maxPrice, pageable)
                : productService.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── PUT /api/v1/products/{id} ─────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse response = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", response));
    }

    // ── DELETE /api/v1/products/{id} ──────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }
}
