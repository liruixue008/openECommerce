package com.openecommerce.product.infrastructure;

import com.openecommerce.product.domain.Product;
import com.openecommerce.product.domain.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Product JPA Repository
 * 商品JPA仓储
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByMerchantId(Long merchantId);
    
    List<Product> findByMerchantIdAndStatus(Long merchantId, ProductStatus status);
    
    boolean existsBySku(String sku);
    
    List<Product> findByStatus(ProductStatus status);
}