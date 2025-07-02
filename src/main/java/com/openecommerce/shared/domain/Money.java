package com.openecommerce.shared.domain;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Money Value Object
 * 金钱值对象
 */
@Embeddable
public class Money {
    
    private BigDecimal amount;
    private String currency;
    
    // JPA需要无参构造函数
    protected Money() {}
    
    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("货币类型不能为空");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }
    
    /**
     * 创建零金额
     */
    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
    
    /**
     * 创建金额
     */
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
    
    /**
     * 创建金额（从double）
     */
    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }
    
    /**
     * 加法
     */
    public Money add(Money other) {
        checkCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    /**
     * 减法
     */
    public Money subtract(Money other) {
        checkCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("余额不足");
        }
        return new Money(result, this.currency);
    }
    
    /**
     * 乘法
     */
    public Money multiply(BigDecimal multiplier) {
        if (multiplier == null) {
            throw new IllegalArgumentException("乘数不能为空");
        }
        if (multiplier.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("乘数不能为负数");
        }
        return new Money(this.amount.multiply(multiplier), this.currency);
    }
    
    /**
     * 乘法（整数）
     */
    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("乘数不能为负数");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }
    
    /**
     * 比较大小
     */
    public int compareTo(Money other) {
        checkCurrency(other);
        return this.amount.compareTo(other.amount);
    }
    
    /**
     * 是否大于
     */
    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }
    
    /**
     * 是否大于等于
     */
    public boolean isGreaterThanOrEqual(Money other) {
        return compareTo(other) >= 0;
    }
    
    /**
     * 是否小于
     */
    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }
    
    /**
     * 是否小于等于
     */
    public boolean isLessThanOrEqual(Money other) {
        return compareTo(other) <= 0;
    }
    
    /**
     * 是否为零
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * 是否为正数
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 检查货币类型是否相同
     */
    private void checkCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                String.format("货币类型不匹配: %s vs %s", this.currency, other.currency)
            );
        }
    }
    
    // Getters
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
               Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", amount.toString(), currency);
    }
}