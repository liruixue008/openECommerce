package com.openecommerce.merchant.domain;

import com.openecommerce.shared.domain.AggregateRoot;
import com.openecommerce.shared.domain.Money;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Merchant Aggregate Root
 * 商家聚合根
 */
@Entity
@Table(name = "merchants")
@Getter
@ToString(callSuper = true)
public class Merchant extends AggregateRoot {
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    })
    private Money balance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MerchantStatus status;
    
    // JPA required
    protected Merchant() {
        super();
    }
    
    public Merchant(String name, String email, String phone, Money initialBalance) {
        super();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Merchant name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance cannot be null");
        }
        
        this.name = name.trim();
        this.email = email.trim().toLowerCase();
        this.phone = phone != null ? phone.trim() : null;
        this.balance = initialBalance;
        this.status = MerchantStatus.ACTIVE;
    }
    
    /**
     * 增加商家收入
     */
    public void addRevenue(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Revenue amount cannot be null");
        }
        if (amount.isZero()) {
            throw new IllegalArgumentException("Revenue amount must be greater than zero");
        }
        if (this.status != MerchantStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add revenue to inactive merchant account");
        }
        
        this.balance = this.balance.add(amount);
    }
    
    /**
     * 从商家账户扣款（如退款等）
     */
    public void deduct(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Deduct amount cannot be null");
        }
        if (amount.isZero()) {
            throw new IllegalArgumentException("Deduct amount must be greater than zero");
        }
        if (this.status != MerchantStatus.ACTIVE) {
            throw new IllegalStateException("Cannot deduct from inactive merchant account");
        }
        if (this.balance.isLessThan(amount)) {
            throw new IllegalStateException("Insufficient merchant balance");
        }
        
        this.balance = this.balance.subtract(amount);
    }
    
    /**
     * 激活商家
     */
    public void activate() {
        this.status = MerchantStatus.ACTIVE;
    }
    
    /**
     * 停用商家
     */
    public void deactivate() {
        this.status = MerchantStatus.INACTIVE;
    }
    
    /**
     * 暂停商家
     */
    public void suspend() {
        this.status = MerchantStatus.SUSPENDED;
    }
}