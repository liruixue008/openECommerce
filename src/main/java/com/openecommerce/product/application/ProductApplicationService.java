package com.openecommerce.product.application;

import com.openecommerce.merchant.domain.MerchantRepository;
import com.openecommerce.product.application.dto.AddStockRequest;
import com.openecommerce.product.application.dto.CreateProductRequest;
import com.openecommerce.product.application.dto.ProductResponse;
import com.openecommerce.product.domain.Product;
import com.openecommerce.product.domain.ProductRepository;
import com.openecommerce.product.domain.ProductStatus;
import com.openecommerce.shared.domain.valueobject.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Product Application Service
 * 商品应用服务
 */
@Service
@Transactional
public class ProductApplicationService {
    
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    
    @Autowired
    public ProductApplicationService(ProductRepository productRepository, 
                                   MerchantRepository merchantRepository) {
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
    }
    
    /**
     * 创建商品
     */
    public ProductResponse createProduct(CreateProductRequest request) {
        // 验证商家是否存在
        merchantRepository.findById(request.getMerchantId())
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + request.getMerchantId()));
        
        // 检查SKU是否已存在
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }
        
        Money price = Money.of(request.getPrice(), request.getCurrency());
        Product product = new Product(
            request.getSku(),
            request.getName(),
            request.getDescription(),
            price,
            request.getStockQuantity(),
            request.getMerchantId()
        );
        
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }
    
    /**
     * 添加库存
     */
    public ProductResponse addStock(String sku, AddStockRequest request) {
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + sku));
        
        product.addStock(request.getQuantity());
        Product savedProduct = productRepository.save(product);
        
        return ProductResponse.from(savedProduct);
    }
    
    /**
     * 根据SKU获取商品
     */
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductBySku(String sku) {
        return productRepository.findBySku(sku)
            .map(ProductResponse::from);
    }
    
    /**
     * 根据商家ID获取商品列表
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByMerchantId(Long merchantId) {
        return productRepository.findByMerchantId(merchantId)
            .stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取所有活跃商品
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE)
            .stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 内部方法：根据SKU获取商品实体（用于其他服务调用）
     */
    @Transactional(readOnly = true)
    public Product getProductEntityBySku(String sku) {
        return productRepository.findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + sku));
    }
    
    /**
     * 内部方法：保存商品实体（用于其他服务调用）
     */
    public Product saveProductEntity(Product product) {
        return productRepository.save(product);
    }
}