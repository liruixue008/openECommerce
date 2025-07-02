package com.openecommerce.user.domain;

import com.openecommerce.shared.domain.AggregateRoot;
import com.openecommerce.shared.domain.Money;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

/**
 * User Aggregate Root
 * 用户聚合根
 */
@Entity
@Table(name = "users")
@Getter
@ToString(callSuper = true)
public class User extends AggregateRoot {
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    })
    private Money balance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;
    
    // JPA required
    protected User() {
        super();
    }
    
    public User(String username, String email, String currency) {
        super();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        this.username = username.trim();
        this.email = email.trim().toLowerCase();
        this.balance = Money.zero(currency);
        this.status = UserStatus.ACTIVE;
    }
    
    /**
     * 充值到用户账户
     */
    public void recharge(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Recharge amount cannot be null");
        }
        if (amount.isZero()) {
            throw new IllegalArgumentException("Recharge amount must be greater than zero");
        }
        if (this.status != UserStatus.ACTIVE) {
            throw new IllegalStateException("Cannot recharge to inactive user account");
        }
        
        this.balance = this.balance.add(amount);
    }
    
    /**
     * 从用户账户扣款
     */
    public void deduct(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Deduct amount cannot be null");
        }
        if (amount.isZero()) {
            throw new IllegalArgumentException("Deduct amount must be greater than zero");
        }
        if (this.status != UserStatus.ACTIVE) {
            throw new IllegalStateException("Cannot deduct from inactive user account");
        }
        if (this.balance.isLessThan(amount)) {
            throw new IllegalStateException("Insufficient balance");
        }
        
        this.balance = this.balance.subtract(amount);
    }
    
    /**
     * 检查是否有足够余额
     */
    public boolean hasEnoughBalance(Money amount) {
        return this.balance.isGreaterThanOrEqual(amount);
    }
    
    /**
     * 激活用户
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }
    
    /**
     * 停用用户
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }
}