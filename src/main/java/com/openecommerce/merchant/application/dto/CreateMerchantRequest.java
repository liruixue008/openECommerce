package com.openecommerce.merchant.application.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Create Merchant Request DTO
 * 创建商家请求DTO
 */
public class CreateMerchantRequest {
    
    @NotBlank(message = "商家名称不能为空")
    @Size(min = 2, max = 100, message = "商家名称长度必须在2-100个字符之间")
    private String name;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "电话不能为空")
    @Size(min = 10, max = 20, message = "电话长度必须在10-20个字符之间")
    private String phone;
    
    private String currency = "CNY"; // 默认货币
    
    // 构造函数
    public CreateMerchantRequest() {}
    
    public CreateMerchantRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    public CreateMerchantRequest(String name, String email, String phone, String currency) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.currency = currency;
    }
    
    // Getters and Setters
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
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}