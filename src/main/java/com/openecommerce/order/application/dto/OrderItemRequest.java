package com.openecommerce.order.application.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Order Item Request DTO
 * 订单项请求DTO
 */
@Data
public class OrderItemRequest {
    
    @NotBlank(message = "SKU cannot be blank")
    private String sku;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be positive")
    private Integer quantity;
}