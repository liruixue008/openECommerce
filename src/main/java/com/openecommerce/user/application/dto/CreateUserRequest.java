package com.openecommerce.user.application.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Create User Request DTO
 * 创建用户请求DTO
 */
@Data
public class CreateUserRequest {
    
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email format is invalid")
    private String email;
    
    private String currency = "CNY";
}