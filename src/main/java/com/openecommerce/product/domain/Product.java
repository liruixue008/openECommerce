package com.openecommerce.product.domain;

import com.openecommerce.shared.domain.AggregateRoot;
import com.openecommerce.shared.domain.Money;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Product Aggregate Root
 * 商品聚合根
 */
@Entity
@Table(name = "products")
@Getter
@ToString(callSuper = true)
public class Product extends AggregateRoot {
    
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    private Money price;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;
    
    // JPA required
    protected Product() {
        super();
    }
    
    public Product(String sku, String name, String description, Money price, 
                   Integer stockQuantity, Long merchantId) {
        super();
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be null or negative");
        }
        if (merchantId == null) {
            throw new IllegalArgumentException("Merchant ID cannot be null");
        }
        
        this.sku = sku.trim().toUpperCase();
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.merchantId = merchantId;
        this.status = ProductStatus.ACTIVE;
    }
    
    /**
     * 增加库存
     */
    public void addStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.status != ProductStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add stock to inactive product");
        }
        
        this.stockQuantity += quantity;
    }
    
    /**
     * 减少库存
     */
    public void reduceStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.status != ProductStatus.ACTIVE) {
            throw new IllegalStateException("Cannot reduce stock from inactive product");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        
        this.stockQuantity -= quantity;
    }
    
    /**
     * 检查是否有足够库存
     */
    public boolean hasEnoughStock(Integer quantity) {
        return this.stockQuantity >= quantity;
    }
    
    /**
     * 计算总价
     */
    public Money calculateTotalPrice(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return this.price.multiply(quantity);
    }
    
    /**
     * 更新价格
     */
    public void updatePrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (this.status != ProductStatus.ACTIVE) {
            throw new IllegalStateException("Cannot update price of inactive product");
        }
        
        this.price = newPrice;
    }
    
    /**
     * 激活商品
     */
    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }
    
    /**
     * 停用商品
     */
    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }
    
    /**
     * 下架商品
     */
    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }
}