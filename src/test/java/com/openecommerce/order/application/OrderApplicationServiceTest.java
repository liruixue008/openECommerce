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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for OrderApplicationService
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    private User mockUser;
    private Merchant mockMerchant;
    private Product mockProduct;
    private Order mockOrder;
    private CreateOrderRequest createOrderRequest;
    private OrderItemRequest orderItemRequest;

    @Before
    public void setUp() {
        // Setup mock user
        mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(1L);
        lenient().when(mockUser.hasEnoughBalance(any(Money.class))).thenReturn(true);

        // Setup mock merchant
        mockMerchant = mock(Merchant.class);
        lenient().when(mockMerchant.getId()).thenReturn(1L);

        // Setup mock product
        mockProduct = mock(Product.class);
        lenient().when(mockProduct.getSku()).thenReturn("SKU001");
        lenient().when(mockProduct.getName()).thenReturn("Test Product");
        lenient().when(mockProduct.getPrice()).thenReturn(new Money(new BigDecimal("100.00"), "CNY"));
        lenient().when(mockProduct.getMerchantId()).thenReturn(1L);
        lenient().when(mockProduct.hasEnoughStock(anyInt())).thenReturn(true);
        lenient().when(mockProduct.calculateTotalPrice(anyInt())).thenReturn(new Money(new BigDecimal("200.00"), "CNY"));

        // Setup mock order
        mockOrder = mock(Order.class);
        lenient().when(mockOrder.getTotalAmount()).thenReturn(new Money(new BigDecimal("200.00"), "CNY"));

        // Setup order item request
        orderItemRequest = new OrderItemRequest();
        orderItemRequest.setSku("SKU001");
        orderItemRequest.setQuantity(2);

        // Setup create order request
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(1L);
        createOrderRequest.setItems(Arrays.asList(orderItemRequest));
    }

    @Test
    public void testCreateAndProcessOrder_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findBySku("SKU001")).thenReturn(Optional.of(mockProduct));
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(mockMerchant));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        OrderResponse result = orderApplicationService.createAndProcessOrder(createOrderRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(productRepository, times(3)).findBySku("SKU001");
        verify(merchantRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(userRepository).save(mockUser);
        verify(merchantRepository).save(mockMerchant);
        verify(productRepository).save(mockProduct);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndProcessOrder_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        orderApplicationService.createAndProcessOrder(createOrderRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndProcessOrder_ProductNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findBySku("SKU001")).thenReturn(Optional.empty());

        // When
        orderApplicationService.createAndProcessOrder(createOrderRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndProcessOrder_MerchantNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findBySku("SKU001")).thenReturn(Optional.of(mockProduct));
        when(merchantRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        orderApplicationService.createAndProcessOrder(createOrderRequest);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndProcessOrder_InsufficientStock() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findBySku("SKU001")).thenReturn(Optional.of(mockProduct));
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(mockMerchant));
        when(mockProduct.hasEnoughStock(anyInt())).thenReturn(false);

        // When
        orderApplicationService.createAndProcessOrder(createOrderRequest);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAndProcessOrder_InsufficientBalance() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findBySku("SKU001")).thenReturn(Optional.of(mockProduct));
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(mockMerchant));
        when(mockUser.hasEnoughBalance(any(Money.class))).thenReturn(false);

        // When
        orderApplicationService.createAndProcessOrder(createOrderRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndProcessOrder_EmptyItems() {
        // Given
        createOrderRequest.setItems(Collections.emptyList());
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // When
        orderApplicationService.createAndProcessOrder(createOrderRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAndProcessOrder_DifferentMerchants() {
        // Given
        Product anotherProduct = mock(Product.class);
        when(anotherProduct.getMerchantId()).thenReturn(2L);
        
        OrderItemRequest anotherItem = new OrderItemRequest();
        anotherItem.setSku("SKU002");
        anotherItem.setQuantity(1);
        
        createOrderRequest.setItems(Arrays.asList(orderItemRequest, anotherItem));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findBySku("SKU001")).thenReturn(Optional.of(mockProduct));
        when(productRepository.findBySku("SKU002")).thenReturn(Optional.of(anotherProduct));

        // When
        orderApplicationService.createAndProcessOrder(createOrderRequest);
    }

    @Test
    public void testGetOrderByOrderNumber_Success() {
        // Given
        String orderNumber = "ORD20231201120000001";
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(mockOrder));

        // When
        Optional<OrderResponse> result = orderApplicationService.getOrderByOrderNumber(orderNumber);

        // Then
        assertTrue(result.isPresent());
        verify(orderRepository).findByOrderNumber(orderNumber);
    }

    @Test
    public void testGetOrderByOrderNumber_NotFound() {
        // Given
        String orderNumber = "ORD20231201120000001";
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

        // When
        Optional<OrderResponse> result = orderApplicationService.getOrderByOrderNumber(orderNumber);

        // Then
        assertFalse(result.isPresent());
        verify(orderRepository).findByOrderNumber(orderNumber);
    }

    @Test
    public void testGetOrdersByUserId_Success() {
        // Given
        Long userId = 1L;
        List<Order> orders = Arrays.asList(mockOrder);
        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        // When
        List<OrderResponse> result = orderApplicationService.getOrdersByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    public void testGetOrdersByMerchantId_Success() {
        // Given
        Long merchantId = 1L;
        List<Order> orders = Arrays.asList(mockOrder);
        when(orderRepository.findByMerchantId(merchantId)).thenReturn(orders);

        // When
        List<OrderResponse> result = orderApplicationService.getOrdersByMerchantId(merchantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByMerchantId(merchantId);
    }
}