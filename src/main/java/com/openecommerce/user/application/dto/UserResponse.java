package com.openecommerce.user.application.dto;

import com.openecommerce.user.domain.User;
import com.openecommerce.user.domain.UserStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User Response DTO
 * 用户响应DTO
 */
@Data
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private BigDecimal balanceAmount;
    private String balanceCurrency;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setBalanceAmount(user.getBalance().getAmount());
        response.setBalanceCurrency(user.getBalance().getCurrency());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}