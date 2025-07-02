package com.openecommerce.merchant.application;

import com.openecommerce.merchant.application.dto.CreateMerchantRequest;
import com.openecommerce.merchant.application.dto.MerchantResponse;
import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantRepository;
import com.openecommerce.shared.domain.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * MerchantApplicationService 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class MerchantApplicationServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private MerchantApplicationService merchantApplicationService;

    private CreateMerchantRequest createMerchantRequest;
    private Merchant mockMerchant;

    @Before
    public void setUp() {
        createMerchantRequest = new CreateMerchantRequest();
        createMerchantRequest.setName("Test Merchant");
        createMerchantRequest.setEmail("merchant@example.com");
        createMerchantRequest.setPhone("1234567890");
        createMerchantRequest.setCurrency("CNY");

        Money initialBalance = new Money(BigDecimal.ZERO, "CNY");
        mockMerchant = new Merchant(
            "Test Merchant",
            "merchant@example.com",
            "1234567890",
            initialBalance
        );
    }

    @Test
    public void testCreateMerchant_Success() {
        // Given
        when(merchantRepository.existsByEmail(anyString())).thenReturn(false);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(mockMerchant);

        // When
        MerchantResponse result = merchantApplicationService.createMerchant(createMerchantRequest);

        // Then
        assertNotNull(result);
        assertEquals("Test Merchant", result.getName());
        assertEquals("merchant@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());
        verify(merchantRepository).existsByEmail("merchant@example.com");
        verify(merchantRepository).save(any(Merchant.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMerchant_EmailExists() {
        // Given
        when(merchantRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        merchantApplicationService.createMerchant(createMerchantRequest);

        // Then - exception expected
    }

    @Test
    public void testGetMerchantById_Success() {
        // Given
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(mockMerchant));

        // When
        Optional<MerchantResponse> result = merchantApplicationService.getMerchantById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Merchant", result.get().getName());
        assertEquals("merchant@example.com", result.get().getEmail());
        verify(merchantRepository).findById(1L);
    }

    @Test
    public void testGetMerchantById_NotFound() {
        // Given
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<MerchantResponse> result = merchantApplicationService.getMerchantById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(merchantRepository).findById(1L);
    }

    @Test
    public void testGetMerchantByEmail_Success() {
        // Given
        when(merchantRepository.findByEmail(anyString())).thenReturn(Optional.of(mockMerchant));

        // When
        Optional<MerchantResponse> result = merchantApplicationService.getMerchantByEmail("merchant@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Merchant", result.get().getName());
        assertEquals("merchant@example.com", result.get().getEmail());
        verify(merchantRepository).findByEmail("merchant@example.com");
    }

    @Test
    public void testGetMerchantByEmail_NotFound() {
        // Given
        when(merchantRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        Optional<MerchantResponse> result = merchantApplicationService.getMerchantByEmail("merchant@example.com");

        // Then
        assertFalse(result.isPresent());
        verify(merchantRepository).findByEmail("merchant@example.com");
    }

    @Test
    public void testGetMerchantEntity_Success() {
        // Given
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(mockMerchant));

        // When
        Merchant result = merchantApplicationService.getMerchantEntity(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Merchant", result.getName());
        assertEquals("merchant@example.com", result.getEmail());
        verify(merchantRepository).findById(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMerchantEntity_NotFound() {
        // Given
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        merchantApplicationService.getMerchantEntity(1L);

        // Then - exception expected
    }

    @Test
    public void testSaveMerchantEntity_Success() {
        // Given
        when(merchantRepository.save(any(Merchant.class))).thenReturn(mockMerchant);

        // When
        Merchant result = merchantApplicationService.saveMerchantEntity(mockMerchant);

        // Then
        assertNotNull(result);
        assertEquals("Test Merchant", result.getName());
        assertEquals("merchant@example.com", result.getEmail());
        verify(merchantRepository).save(mockMerchant);
    }
}