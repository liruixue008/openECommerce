package com.openecommerce.product.domain;

import java.util.List;
import java.util.Optional;

/**
 * Product Repository Interface
 * 商品仓储接口
 */
public interface ProductRepository {
    
    /**
     * 保存商品
     */
    Product save(Product product);
    
    /**
     * 根据ID查找商品
     */
    Optional<Product> findById(Long id);
    
    /**
     * 根据SKU查找商品
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * 根据商家ID查找商品列表
     */
    List<Product> findByMerchantId(Long merchantId);
    
    /**
     * 根据商家ID和状态查找商品列表
     */
    List<Product> findByMerchantIdAndStatus(Long merchantId, ProductStatus status);
    
    /**
     * 检查SKU是否存在
     */
    boolean existsBySku(String sku);
    
    /**
     * 查找所有活跃商品
     */
    List<Product> findByStatus(ProductStatus status);
}