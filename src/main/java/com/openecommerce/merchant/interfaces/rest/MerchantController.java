package com.openecommerce.merchant.interfaces.rest;

import com.openecommerce.merchant.application.MerchantApplicationService;
import com.openecommerce.merchant.application.dto.CreateMerchantRequest;
import com.openecommerce.merchant.application.dto.MerchantResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Merchant REST Controller
 * 商家REST控制器
 */
@RestController
@RequestMapping("/merchants")
@Validated
public class MerchantController {
    
    private final MerchantApplicationService merchantApplicationService;
    
    @Autowired
    public MerchantController(MerchantApplicationService merchantApplicationService) {
        this.merchantApplicationService = merchantApplicationService;
    }
    
    /**
     * 创建商家
     */
    @PostMapping
    public ResponseEntity<MerchantResponse> createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        try {
            MerchantResponse response = merchantApplicationService.createMerchant(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据ID获取商家
     */
    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponse> getMerchantById(@PathVariable Long merchantId) {
        Optional<MerchantResponse> merchant = merchantApplicationService.getMerchantById(merchantId);
        return merchant.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据邮箱获取商家
     */
    @GetMapping
    public ResponseEntity<MerchantResponse> getMerchantByEmail(@RequestParam String email) {
        Optional<MerchantResponse> merchant = merchantApplicationService.getMerchantByEmail(email);
        return merchant.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
}