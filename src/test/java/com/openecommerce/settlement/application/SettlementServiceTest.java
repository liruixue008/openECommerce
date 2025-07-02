package com.openecommerce.settlement.application;

import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantRepository;
import com.openecommerce.order.domain.Order;
import com.openecommerce.order.domain.OrderRepository;
import com.openecommerce.order.domain.OrderStatus;
import com.openecommerce.shared.domain.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SettlementService
 */
@RunWith(MockitoJUnitRunner.class)
public class SettlementServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private SettlementService settlementService;

    private Merchant mockMerchant;
    private Order mockOrder1;
    private Order mockOrder2;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Before
    public void setUp() {
        // Setup mock merchant
        mockMerchant = mock(Merchant.class);
        when(mockMerchant.getId()).thenReturn(1L);
        when(mockMerchant.getName()).thenReturn("Test Merchant");
        when(mockMerchant.getBalance()).thenReturn(new Money(new BigDecimal("1000.00"), "CNY"));

        // Setup mock orders
        mockOrder1 = mock(Order.class);
        when(mockOrder1.getTotalAmount()).thenReturn(new Money(new BigDecimal("100.00"), "CNY"));

        mockOrder2 = mock(Order.class);
        when(mockOrder2.getTotalAmount()).thenReturn(new Money(new BigDecimal("200.00"), "CNY"));

        // Setup time range
        startTime = LocalDateTime.of(2023, 12, 1, 0, 0, 0);
        endTime = LocalDateTime.of(2023, 12, 2, 0, 0, 0);
    }

    @Test
    public void testPerformDailySettlement_Success() {
        // Given
        List<Merchant> merchants = Arrays.asList(mockMerchant);
        List<Order> orders = Arrays.asList(mockOrder1, mockOrder2);
        
        when(merchantRepository.findAll()).thenReturn(merchants);
        when(orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(orders);

        // When
        settlementService.performDailySettlement();

        // Then
        verify(merchantRepository).findAll();
        verify(orderRepository).findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testPerformDailySettlement_NoMerchants() {
        // Given
        when(merchantRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        settlementService.performDailySettlement();

        // Then
        verify(merchantRepository).findAll();
        verify(orderRepository, never()).findByMerchantIdAndStatusAndOrderTimeBetween(
            anyLong(), any(OrderStatus.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testPerformDailySettlement_NoCompletedOrders() {
        // Given
        List<Merchant> merchants = Arrays.asList(mockMerchant);
        
        when(merchantRepository.findAll()).thenReturn(merchants);
        when(orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        // When
        settlementService.performDailySettlement();

        // Then
        verify(merchantRepository).findAll();
        verify(orderRepository).findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testPerformDailySettlement_WithNullOrderAmount() {
        // Given
        Order orderWithNullAmount = mock(Order.class);
        when(orderWithNullAmount.getTotalAmount()).thenReturn(null);
        
        List<Merchant> merchants = Arrays.asList(mockMerchant);
        List<Order> orders = Arrays.asList(mockOrder1, orderWithNullAmount);
        
        when(merchantRepository.findAll()).thenReturn(merchants);
        when(orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(orders);

        // When
        settlementService.performDailySettlement();

        // Then
        verify(merchantRepository).findAll();
        verify(orderRepository).findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testPerformDailySettlement_WithZeroAmount() {
        // Given
        Order orderWithZeroAmount = mock(Order.class);
        when(orderWithZeroAmount.getTotalAmount()).thenReturn(new Money(BigDecimal.ZERO, "CNY"));
        
        List<Merchant> merchants = Arrays.asList(mockMerchant);
        List<Order> orders = Arrays.asList(orderWithZeroAmount);
        
        when(merchantRepository.findAll()).thenReturn(merchants);
        when(orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(orders);

        // When
        settlementService.performDailySettlement();

        // Then
        verify(merchantRepository).findAll();
        verify(orderRepository).findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testPerformDailySettlement_InsufficientBalance() {
        // Given
        when(mockMerchant.getBalance()).thenReturn(new Money(new BigDecimal("50.00"), "CNY"));
        
        List<Merchant> merchants = Arrays.asList(mockMerchant);
        List<Order> orders = Arrays.asList(mockOrder1, mockOrder2);
        
        when(merchantRepository.findAll()).thenReturn(merchants);
        when(orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(orders);

        // When
        settlementService.performDailySettlement();

        // Then
        verify(merchantRepository).findAll();
        verify(orderRepository).findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(1L), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test(expected = RuntimeException.class)
    public void testPerformDailySettlement_Exception() {
        // Given
        when(merchantRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        settlementService.performDailySettlement();
    }

    @Test
    public void testPerformManualSettlement_Success() {
        // Given
        Long merchantId = 1L;
        List<Order> orders = Arrays.asList(mockOrder1, mockOrder2);
        
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(mockMerchant));
        when(orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(merchantId), eq(OrderStatus.COMPLETED), eq(startTime), eq(endTime)))
            .thenReturn(orders);

        // When
        settlementService.performManualSettlement(merchantId, startTime, endTime);

        // Then
        verify(merchantRepository).findById(merchantId);
        verify(orderRepository).findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(merchantId), eq(OrderStatus.COMPLETED), eq(startTime), eq(endTime));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformManualSettlement_MerchantNotFound() {
        // Given
        Long merchantId = 1L;
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.empty());

        // When
        settlementService.performManualSettlement(merchantId, startTime, endTime);
    }

    @Test
    public void testPerformManualSettlement_NoOrders() {
        // Given
        Long merchantId = 1L;
        
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(mockMerchant));
        when(orderRepository.findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(merchantId), eq(OrderStatus.COMPLETED), eq(startTime), eq(endTime)))
            .thenReturn(Collections.emptyList());

        // When
        settlementService.performManualSettlement(merchantId, startTime, endTime);

        // Then
        verify(merchantRepository).findById(merchantId);
        verify(orderRepository).findByMerchantIdAndStatusAndOrderTimeBetween(
            eq(merchantId), eq(OrderStatus.COMPLETED), eq(startTime), eq(endTime));
    }
}