package com.openecommerce.order.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository Interface
 * 订单仓储接口
 */
public interface OrderRepository {
    
    /**
     * 保存订单
     */
    Order save(Order order);
    
    /**
     * 根据ID查找订单
     */
    Optional<Order> findById(Long id);
    
    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查找订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据商家ID查找订单列表
     */
    List<Order> findByMerchantId(Long merchantId);
    
    /**
     * 根据状态查找订单列表
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * 根据时间范围查找订单列表
     */
    List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据商家ID和时间范围查找已完成订单
     */
    List<Order> findByMerchantIdAndStatusAndOrderTimeBetween(
        Long merchantId, OrderStatus status, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 检查订单号是否存在
     */
    boolean existsByOrderNumber(String orderNumber);
}