package com.openecommerce.product.application.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Add Stock Request DTO
 * 添加库存请求DTO
 */
@Data
public class AddStockRequest {
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be positive")
    private Integer quantity;
}