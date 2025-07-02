package com.openecommerce.merchant.application.dto;

import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Merchant Response DTO
 * 商家响应DTO
 */
public class MerchantResponse {
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    private BigDecimal balanceAmount;
    private String balanceCurrency;
    private MerchantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public MerchantResponse() {}
    
    public MerchantResponse(Long id, String name, String email, String phone,
                           BigDecimal balanceAmount, String balanceCurrency,
                           MerchantStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.balanceAmount = balanceAmount;
        this.balanceCurrency = balanceCurrency;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * 从Merchant实体创建MerchantResponse
     */
    public static MerchantResponse from(Merchant merchant) {
        return new MerchantResponse(
            merchant.getId(),
            merchant.getName(),
            merchant.getEmail(),
            merchant.getPhone(),
            merchant.getBalance().getAmount(),
            merchant.getBalance().getCurrency(),
            merchant.getStatus(),
            merchant.getCreatedAt(),
            merchant.getUpdatedAt()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }
    
    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
    
    public String getBalanceCurrency() {
        return balanceCurrency;
    }
    
    public void setBalanceCurrency(String balanceCurrency) {
        this.balanceCurrency = balanceCurrency;
    }
    
    public MerchantStatus getStatus() {
        return status;
    }
    
    public void setStatus(MerchantStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}