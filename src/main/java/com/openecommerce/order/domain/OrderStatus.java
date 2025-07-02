package com.openecommerce.order.domain;

/**
 * Order Status Enumeration
 * 订单状态枚举
 */
public enum OrderStatus {
    /**
     * 待处理
     */
    PENDING,
    
    /**
     * 已确认
     */
    CONFIRMED,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 已取消
     */
    CANCELLED
}