package org.example.dto.response;

import org.example.enums.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long          id;
    private String        name;
    private String        sku;
    private String        description;
    private BigDecimal    price;
    private Integer       stockQuantity;
    private String        category;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
    private String        updatedBy;
}