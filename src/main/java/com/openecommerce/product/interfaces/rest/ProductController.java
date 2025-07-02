package com.openecommerce.product.interfaces.rest;

import com.openecommerce.product.application.ProductApplicationService;
import com.openecommerce.product.application.dto.AddStockRequest;
import com.openecommerce.product.application.dto.CreateProductRequest;
import com.openecommerce.product.application.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Product REST Controller
 * 商品REST控制器
 */
@RestController
@RequestMapping("/products")
@Validated
public class ProductController {
    
    private final ProductApplicationService productApplicationService;
    
    @Autowired
    public ProductController(ProductApplicationService productApplicationService) {
        this.productApplicationService = productApplicationService;
    }
    
    /**
     * 创建商品
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        try {
            ProductResponse response = productApplicationService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据SKU获取商品
     */
    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        Optional<ProductResponse> product = productApplicationService.getProductBySku(sku);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取所有活跃商品
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        List<ProductResponse> products = productApplicationService.getActiveProducts();
        return ResponseEntity.ok(products);
    }
    
    /**
     * 根据商家ID获取商品列表
     */
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<ProductResponse>> getProductsByMerchantId(@PathVariable Long merchantId) {
        List<ProductResponse> products = productApplicationService.getProductsByMerchantId(merchantId);
        return ResponseEntity.ok(products);
    }
    
    /**
     * 添加库存
     */
    @PostMapping("/{sku}/stock")
    public ResponseEntity<ProductResponse> addStock(
            @PathVariable String sku,
            @Valid @RequestBody AddStockRequest request) {
        try {
            ProductResponse response = productApplicationService.addStock(sku, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}