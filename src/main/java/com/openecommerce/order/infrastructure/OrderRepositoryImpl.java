package com.openecommerce.order.infrastructure;

import com.openecommerce.order.domain.Order;
import com.openecommerce.order.domain.OrderRepository;
import com.openecommerce.order.domain.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository Implementation
 * 订单仓储实现
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrderJpaRepository orderJpaRepository;
    
    @Autowired
    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }
    
    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
    
    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }
    
    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber);
    }
    
    @Override
    public List<Order> findByUserId(Long userId) {
        return orderJpaRepository.findByUserId(userId);
    }
    
    @Override
    public List<Order> findByMerchantId(Long merchantId) {
        return orderJpaRepository.findByMerchantId(merchantId);
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderJpaRepository.findByStatus(status);
    }
    
    @Override
    public List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return orderJpaRepository.findByOrderTimeBetween(startTime, endTime);
    }
    
    @Override
    public List<Order> findByMerchantIdAndStatusAndOrderTimeBetween(
            Long merchantId, OrderStatus status, LocalDateTime startTime, LocalDateTime endTime) {
        return orderJpaRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            merchantId, status, startTime, endTime);
    }
    
    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        return orderJpaRepository.existsByOrderNumber(orderNumber);
    }
}