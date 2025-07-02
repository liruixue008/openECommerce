package com.openecommerce.product.application.dto;

import com.openecommerce.product.domain.Product;
import com.openecommerce.product.domain.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Response DTO
 * 商品响应DTO
 */
@Data
public class ProductResponse {
    
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private Integer stockQuantity;
    private Long merchantId;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProductResponse from(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPriceAmount(product.getPrice().getAmount());
        response.setPriceCurrency(product.getPrice().getCurrency());
        response.setStockQuantity(product.getStockQuantity());
        response.setMerchantId(product.getMerchantId());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}