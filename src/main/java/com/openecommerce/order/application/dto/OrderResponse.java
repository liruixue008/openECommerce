package com.openecommerce.order.application.dto;

import com.openecommerce.order.domain.Order;
import com.openecommerce.order.domain.OrderItem;
import com.openecommerce.order.domain.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Response DTO
 * 订单响应DTO
 */
@Data
public class OrderResponse {
    
    private Long id;
    private String orderNumber;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    private String totalCurrency;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUserId());
        response.setMerchantId(order.getMerchantId());
        if (order.getTotalAmount() != null) {
            response.setTotalAmount(order.getTotalAmount().getAmount());
            response.setTotalCurrency(order.getTotalAmount().getCurrency());
        }
        response.setStatus(order.getStatus());
        response.setOrderTime(order.getOrderTime());
        response.setItems(order.getOrderItems().stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toList()));
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
    
    @Data
    public static class OrderItemResponse {
        private Long id;
        private String sku;
        private String productName;
        private BigDecimal unitPriceAmount;
        private String unitPriceCurrency;
        private Integer quantity;
        private BigDecimal totalPriceAmount;
        private String totalPriceCurrency;
        
        public static OrderItemResponse from(OrderItem item) {
            OrderItemResponse response = new OrderItemResponse();
            response.setId(item.getId());
            response.setSku(item.getSku());
            response.setProductName(item.getProductName());
            response.setUnitPriceAmount(item.getUnitPrice().getAmount());
            response.setUnitPriceCurrency(item.getUnitPrice().getCurrency());
            response.setQuantity(item.getQuantity());
            response.setTotalPriceAmount(item.getTotalPrice().getAmount());
            response.setTotalPriceCurrency(item.getTotalPrice().getCurrency());
            return response;
        }
    }
}