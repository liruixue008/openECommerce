package com.openecommerce.order.domain;

import com.openecommerce.shared.domain.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Order Item Entity
 * 订单项实体
 */
@Entity
@Table(name = "order_items")
@Getter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "order")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(name = "sku", nullable = false, length = 50)
    private String sku;
    
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency"))
    })
    private Money unitPrice;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_price_currency"))
    })
    private Money totalPrice;
    
    // JPA required
    protected OrderItem() {}
    
    public OrderItem(Order order, String sku, String productName, Money unitPrice, Integer quantity) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        this.order = order;
        this.sku = sku.trim().toUpperCase();
        this.productName = productName.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(quantity);
    }
    
    /**
     * 更新数量
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        this.quantity = newQuantity;
        this.totalPrice = this.unitPrice.multiply(newQuantity);
    }
}