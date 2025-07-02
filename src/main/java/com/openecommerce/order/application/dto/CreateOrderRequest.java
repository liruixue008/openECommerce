package com.openecommerce.order.application.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Create Order Request DTO
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;
}