package com.openecommerce.order.interfaces.rest;

import com.openecommerce.order.application.OrderApplicationService;
import com.openecommerce.order.application.dto.CreateOrderRequest;
import com.openecommerce.order.application.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Order REST Controller
 * 订单REST控制器
 */
@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
    
    private final OrderApplicationService orderApplicationService;
    
    @Autowired
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }
    
    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse response = orderApplicationService.createAndProcessOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据订单号获取订单
     */
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        Optional<OrderResponse> order = orderApplicationService.getOrderByOrderNumber(orderNumber);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据用户ID获取订单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> orders = orderApplicationService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 根据商家ID获取订单列表
     */
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByMerchantId(@PathVariable Long merchantId) {
        List<OrderResponse> orders = orderApplicationService.getOrdersByMerchantId(merchantId);
        return ResponseEntity.ok(orders);
    }
}