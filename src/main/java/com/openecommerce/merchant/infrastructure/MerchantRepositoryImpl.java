package com.openecommerce.merchant.infrastructure;

import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Merchant Repository Implementation
 * 商家仓储实现
 */
@Component
public class MerchantRepositoryImpl implements MerchantRepository {
    
    private final MerchantJpaRepository merchantJpaRepository;
    
    @Autowired
    public MerchantRepositoryImpl(MerchantJpaRepository merchantJpaRepository) {
        this.merchantJpaRepository = merchantJpaRepository;
    }
    
    @Override
    public Merchant save(Merchant merchant) {
        return merchantJpaRepository.save(merchant);
    }
    
    @Override
    public Optional<Merchant> findById(Long id) {
        return merchantJpaRepository.findById(id);
    }
    
    @Override
    public Optional<Merchant> findByEmail(String email) {
        return merchantJpaRepository.findByEmail(email);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return merchantJpaRepository.existsByEmail(email);
    }
    
    @Override
    public List<Merchant> findAll() {
        return merchantJpaRepository.findAll();
    }
}