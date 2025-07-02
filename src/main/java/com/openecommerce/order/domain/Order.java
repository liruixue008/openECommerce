package com.openecommerce.order.domain;

import com.openecommerce.shared.domain.AggregateRoot;
import com.openecommerce.shared.domain.Money;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order Aggregate Root
 * 订单聚合根
 */
@Entity
@Table(name = "orders")
@Getter
@ToString(callSuper = true, exclude = "orderItems")
public class Order extends AggregateRoot {
    
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_currency"))
    })
    private Money totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // JPA required
    protected Order() {
        super();
    }
    
    public Order(String orderNumber, Long userId, Long merchantId) {
        super();
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Order number cannot be null or empty");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (merchantId == null) {
            throw new IllegalArgumentException("Merchant ID cannot be null");
        }
        
        this.orderNumber = orderNumber.trim();
        this.userId = userId;
        this.merchantId = merchantId;
        this.status = OrderStatus.PENDING;
        this.orderTime = LocalDateTime.now();
    }
    
    /**
     * 添加订单项
     */
    public void addOrderItem(String sku, String productName, Money unitPrice, Integer quantity) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to non-pending order");
        }
        
        OrderItem orderItem = new OrderItem(this, sku, productName, unitPrice, quantity);
        this.orderItems.add(orderItem);
        recalculateTotalAmount();
    }
    
    /**
     * 确认订单
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        if (this.orderItems.isEmpty()) {
            throw new IllegalStateException("Cannot confirm order without items");
        }
        
        this.status = OrderStatus.CONFIRMED;
    }
    
    /**
     * 完成订单
     */
    public void complete() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed orders can be completed");
        }
        
        this.status = OrderStatus.COMPLETED;
    }
    
    /**
     * 取消订单
     */
    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed order");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        
        this.status = OrderStatus.CANCELLED;
    }
    
    /**
     * 获取订单项（只读）
     */
    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(this.orderItems);
    }
    
    /**
     * 重新计算总金额
     */
    private void recalculateTotalAmount() {
        if (this.orderItems.isEmpty()) {
            this.totalAmount = null;
            return;
        }
        
        Money total = null;
        for (OrderItem item : this.orderItems) {
            Money itemTotal = item.getTotalPrice();
            if (total == null) {
                total = itemTotal;
            } else {
                total = total.add(itemTotal);
            }
        }
        this.totalAmount = total;
    }
    
    /**
     * 检查订单是否可以支付
     */
    public boolean canBePaid() {
        return this.status == OrderStatus.PENDING && this.totalAmount != null && !this.totalAmount.isZero();
    }
}