package com.openecommerce.merchant.application;

import com.openecommerce.merchant.application.dto.CreateMerchantRequest;
import com.openecommerce.merchant.application.dto.MerchantResponse;
import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantRepository;
import com.openecommerce.shared.domain.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Merchant Application Service
 * 商家应用服务
 */
@Service
@Transactional
public class MerchantApplicationService {
    
    private final MerchantRepository merchantRepository;
    
    @Autowired
    public MerchantApplicationService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }
    
    /**
     * 创建商家
     */
    public MerchantResponse createMerchant(CreateMerchantRequest request) {
        // 检查邮箱是否已存在
        if (merchantRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在: " + request.getEmail());
        }
        
        // 创建商家实体
        Money initialBalance = new Money(BigDecimal.ZERO, request.getCurrency());
        Merchant merchant = new Merchant(
            request.getName(),
            request.getEmail(),
            request.getPhone(),
            initialBalance
        );
        
        // 保存商家
        Merchant savedMerchant = merchantRepository.save(merchant);
        
        return MerchantResponse.from(savedMerchant);
    }
    
    /**
     * 根据ID获取商家
     */
    @Transactional(readOnly = true)
    public Optional<MerchantResponse> getMerchantById(Long merchantId) {
        return merchantRepository.findById(merchantId)
                .map(MerchantResponse::from);
    }
    
    /**
     * 根据邮箱获取商家
     */
    @Transactional(readOnly = true)
    public Optional<MerchantResponse> getMerchantByEmail(String email) {
        return merchantRepository.findByEmail(email)
                .map(MerchantResponse::from);
    }
    
    /**
     * 获取商家实体（内部使用）
     */
    public Merchant getMerchantEntity(Long merchantId) {
        return merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("商家不存在: " + merchantId));
    }
    
    /**
     * 保存商家实体（内部使用）
     */
    public Merchant saveMerchantEntity(Merchant merchant) {
        return merchantRepository.save(merchant);
    }
}