package com.openecommerce.product.application.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Create Product Request DTO
 * 创建商品请求DTO
 */
@Data
public class CreateProductRequest {
    
    @NotBlank(message = "SKU cannot be blank")
    private String sku;
    
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    private String currency = "CNY";
    
    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    @NotNull(message = "Merchant ID cannot be null")
    private Long merchantId;
}