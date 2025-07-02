package com.openecommerce.merchant.infrastructure;

import com.openecommerce.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Merchant JPA Repository
 * 商家JPA仓储
 */
@Repository
public interface MerchantJpaRepository extends JpaRepository<Merchant, Long> {
    
    Optional<Merchant> findByEmail(String email);
    
    boolean existsByEmail(String email);
}