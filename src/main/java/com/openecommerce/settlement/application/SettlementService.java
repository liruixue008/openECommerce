package com.openecommerce.settlement.application;

import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantRepository;
import com.openecommerce.order.domain.Order;
import com.openecommerce.order.domain.OrderRepository;
import com.openecommerce.order.domain.OrderStatus;
import com.openecommerce.shared.domain.valueobject.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Settlement Service
 * 结算服务
 */
@Service
@Transactional
public class SettlementService {
    
    private static final Logger logger = LoggerFactory.getLogger(SettlementService.class);
    
    private final OrderRepository orderRepository;
    private final MerchantRepository merchantRepository;
    
    @Autowired
    public SettlementService(OrderRepository orderRepository, MerchantRepository merchantRepository) {
        this.orderRepository = orderRepository;
        this.merchantRepository = merchantRepository;
    }
    
    /**
     * 每日结算任务
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "${business.settlement.cron:0 0 2 * * ?}")
    public void performDailySettlement() {
        logger.info("Starting daily settlement process...");
        
        LocalDateTime endTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusDays(1);
        
        logger.info("Settlement period: {} to {}", startTime, endTime);
        
        try {
            // 获取所有商家
            List<Merchant> merchants = merchantRepository.findAll();
            
            for (Merchant merchant : merchants) {
                performMerchantSettlement(merchant, startTime, endTime);
            }
            
            logger.info("Daily settlement completed successfully");
        } catch (Exception e) {
            logger.error("Error during daily settlement", e);
            throw e;
        }
    }
    
    /**
     * 执行单个商家的结算
     */
    private void performMerchantSettlement(Merchant merchant, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Processing settlement for merchant: {} (ID: {})", merchant.getName(), merchant.getId());
        
        // 获取该商家在指定时间范围内的已完成订单
        List<Order> completedOrders = orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            merchant.getId(), OrderStatus.COMPLETED, startTime, endTime);
        
        if (completedOrders.isEmpty()) {
            logger.info("No completed orders found for merchant: {}", merchant.getName());
            return;
        }
        
        // 计算总销售额
        Money totalSalesAmount = null;
        for (Order order : completedOrders) {
            if (order.getTotalAmount() != null) {
                if (totalSalesAmount == null) {
                    totalSalesAmount = order.getTotalAmount();
                } else {
                    totalSalesAmount = totalSalesAmount.add(order.getTotalAmount());
                }
            }
        }
        
        if (totalSalesAmount == null || totalSalesAmount.isZero()) {
            logger.info("No sales amount to settle for merchant: {}", merchant.getName());
            return;
        }
        
        // 验证商家账户余额与销售额是否匹配
        Money merchantBalance = merchant.getBalance();
        
        logger.info("Merchant: {}, Sales Amount: {}, Current Balance: {}", 
            merchant.getName(), totalSalesAmount, merchantBalance);
        
        // 检查余额是否足够覆盖销售额
        if (merchantBalance.isGreaterThanOrEqual(totalSalesAmount)) {
            logger.info("Settlement verification passed for merchant: {}", merchant.getName());
        } else {
            logger.warn("Settlement verification failed for merchant: {}. Balance insufficient to cover sales.", 
                merchant.getName());
        }
        
        // 记录结算信息
        logger.info("Settlement summary for merchant {}: {} orders processed, total amount: {}", 
            merchant.getName(), completedOrders.size(), totalSalesAmount);
    }
    
    /**
     * 手动触发结算（用于测试或特殊情况）
     */
    public void performManualSettlement(Long merchantId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Starting manual settlement for merchant: {}", merchantId);
        
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + merchantId));
        
        performMerchantSettlement(merchant, startTime, endTime);
        
        logger.info("Manual settlement completed for merchant: {}", merchantId);
    }
}