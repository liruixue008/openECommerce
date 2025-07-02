package com.openecommerce.order.application;

import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantRepository;
import com.openecommerce.order.application.dto.CreateOrderRequest;
import com.openecommerce.order.application.dto.OrderItemRequest;
import com.openecommerce.order.application.dto.OrderResponse;
import com.openecommerce.order.domain.Order;
import com.openecommerce.order.domain.OrderRepository;
import com.openecommerce.product.domain.Product;
import com.openecommerce.product.domain.ProductRepository;
import com.openecommerce.shared.domain.Money;
import com.openecommerce.user.domain.User;
import com.openecommerce.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Order Application Service
 * 订单应用服务
 */
@Service
@Transactional
public class OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    
    @Autowired
    public OrderApplicationService(OrderRepository orderRepository,
                                 UserRepository userRepository,
                                 ProductRepository productRepository,
                                 MerchantRepository merchantRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
    }
    
    /**
     * 创建并处理订单
     */
    public OrderResponse createAndProcessOrder(CreateOrderRequest request) {
        // 验证用户存在
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));
        
        // 验证所有商品都属于同一个商家
        Long merchantId = validateAndGetMerchantId(request.getItems());
        
        // 验证商家存在
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + merchantId));
        
        // 生成订单号
        String orderNumber = generateOrderNumber();
        
        // 创建订单
        Order order = new Order(orderNumber, user.getId(), merchantId);
        
        // 验证库存并添加订单项
        Money totalAmount = null;
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findBySku(itemRequest.getSku())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getSku()));
            
            // 验证库存
            if (!product.hasEnoughStock(itemRequest.getQuantity())) {
                throw new IllegalStateException("Insufficient stock for product: " + itemRequest.getSku());
            }
            
            // 添加订单项
            order.addOrderItem(
                product.getSku(),
                product.getName(),
                product.getPrice(),
                itemRequest.getQuantity()
            );
            
            // 计算总金额
            Money itemTotal = product.calculateTotalPrice(itemRequest.getQuantity());
            if (totalAmount == null) {
                totalAmount = itemTotal;
            } else {
                totalAmount = totalAmount.add(itemTotal);
            }
        }
        
        // 验证用户余额
        if (!user.hasEnoughBalance(totalAmount)) {
            throw new IllegalStateException("Insufficient user balance");
        }
        
        // 确认订单
        order.confirm();
        
        // 处理支付和库存
        processPaymentAndStock(user, merchant, order, request.getItems());
        
        // 完成订单
        order.complete();
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        return OrderResponse.from(savedOrder);
    }
    
    /**
     * 根据订单号获取订单
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .map(OrderResponse::from);
    }
    
    /**
     * 根据用户ID获取订单列表
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
            .stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据商家ID获取订单列表
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByMerchantId(Long merchantId) {
        return orderRepository.findByMerchantId(merchantId)
            .stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 验证所有商品属于同一商家并返回商家ID
     */
    private Long validateAndGetMerchantId(List<OrderItemRequest> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }
        
        Long merchantId = null;
        for (OrderItemRequest item : items) {
            Product product = productRepository.findBySku(item.getSku())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getSku()));
            
            if (merchantId == null) {
                merchantId = product.getMerchantId();
            } else if (!merchantId.equals(product.getMerchantId())) {
                throw new IllegalArgumentException("All products must belong to the same merchant");
            }
        }
        
        return merchantId;
    }
    
    /**
     * 处理支付和库存
     */
    private void processPaymentAndStock(User user, Merchant merchant, Order order, List<OrderItemRequest> items) {
        Money totalAmount = order.getTotalAmount();
        
        // 扣除用户余额
        user.deduct(totalAmount);
        userRepository.save(user);
        
        // 增加商家收入
        merchant.addRevenue(totalAmount);
        merchantRepository.save(merchant);
        
        // 减少商品库存
        for (OrderItemRequest item : items) {
            Product product = productRepository.findBySku(item.getSku())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getSku()));
            
            product.reduceStock(item.getQuantity());
            productRepository.save(product);
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.valueOf((int)(Math.random() * 1000));
        return "ORD" + timestamp + String.format("%03d", Integer.parseInt(randomSuffix));
    }
}