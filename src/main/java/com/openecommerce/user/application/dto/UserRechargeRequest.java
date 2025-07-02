package com.openecommerce.user.application.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * User Recharge Request DTO
 * 用户充值请求DTO
 */
@Data
public class UserRechargeRequest {
    
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private String currency = "CNY";
}