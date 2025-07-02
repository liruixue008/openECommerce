package com.openecommerce.product.infrastructure;

import com.openecommerce.product.domain.Product;
import com.openecommerce.product.domain.ProductRepository;
import com.openecommerce.product.domain.ProductStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Product Repository Implementation
 * 商品仓储实现
 */
@Component
public class ProductRepositoryImpl implements ProductRepository {
    
    private final ProductJpaRepository productJpaRepository;
    
    @Autowired
    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }
    
    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }
    
    @Override
    public Optional<Product> findBySku(String sku) {
        return productJpaRepository.findBySku(sku);
    }
    
    @Override
    public List<Product> findByMerchantId(Long merchantId) {
        return productJpaRepository.findByMerchantId(merchantId);
    }
    
    @Override
    public List<Product> findByMerchantIdAndStatus(Long merchantId, ProductStatus status) {
        return productJpaRepository.findByMerchantIdAndStatus(merchantId, status);
    }
    
    @Override
    public boolean existsBySku(String sku) {
        return productJpaRepository.existsBySku(sku);
    }
    
    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return productJpaRepository.findByStatus(status);
    }
}