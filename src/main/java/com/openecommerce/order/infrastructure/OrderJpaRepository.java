package com.openecommerce.order.infrastructure;

import com.openecommerce.order.domain.Order;
import com.openecommerce.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order JPA Repository
 * 订单JPA仓储
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByMerchantId(Long merchantId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Order> findByMerchantIdAndStatusAndOrderTimeBetween(
        Long merchantId, OrderStatus status, LocalDateTime startTime, LocalDateTime endTime);
    
    boolean existsByOrderNumber(String orderNumber);
}