package com.openecommerce.merchant.domain;

import java.util.List;
import java.util.Optional;

/**
 * Merchant Repository Interface
 * 商家仓储接口
 */
public interface MerchantRepository {
    
    /**
     * 保存商家
     */
    Merchant save(Merchant merchant);
    
    /**
     * 根据ID查找商家
     */
    Optional<Merchant> findById(Long id);
    
    /**
     * 根据邮箱查找商家
     */
    Optional<Merchant> findByEmail(String email);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 查找所有商家
     */
    List<Merchant> findAll();
}