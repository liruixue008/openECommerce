package com.openecommerce.product.application;

import com.openecommerce.merchant.domain.Merchant;
import com.openecommerce.merchant.domain.MerchantRepository;
import com.openecommerce.product.application.dto.AddStockRequest;
import com.openecommerce.product.application.dto.CreateProductRequest;
import com.openecommerce.product.application.dto.ProductResponse;
import com.openecommerce.product.domain.Product;
import com.openecommerce.product.domain.ProductRepository;
import com.openecommerce.product.domain.ProductStatus;
import com.openecommerce.shared.domain.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ProductApplicationService 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductApplicationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    private CreateProductRequest createProductRequest;
    private AddStockRequest addStockRequest;
    private Product mockProduct;
    private Merchant mockMerchant;

    @Before
    public void setUp() {
        createProductRequest = new CreateProductRequest();
        createProductRequest.setSku("TEST-SKU-001");
        createProductRequest.setName("Test Product");
        createProductRequest.setDescription("Test Description");
        createProductRequest.setPrice(new BigDecimal("99.99"));
        createProductRequest.setCurrency("CNY");
        createProductRequest.setStockQuantity(100);
        createProductRequest.setMerchantId(1L);

        addStockRequest = new AddStockRequest();
        addStockRequest.setQuantity(50);

        Money price = Money.of(new BigDecimal("99.99"), "CNY");
        mockProduct = new Product(
            "TEST-SKU-001",
            "Test Product",
            "Test Description",
            price,
            100,
            1L
        );

        Money initialBalance = new Money(BigDecimal.ZERO, "CNY");
        mockMerchant = new Merchant(
            "Test Merchant",
            "merchant@example.com",
            "1234567890",
            initialBalance
        );
    }

    @Test
    public void testCreateProduct_Success() {
        // Given
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(mockMerchant));
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // When
        ProductResponse result = productApplicationService.createProduct(createProductRequest);

        // Then
        assertNotNull(result);
        assertEquals("TEST-SKU-001", result.getSku());
        assertEquals("Test Product", result.getName());
        verify(merchantRepository).findById(1L);
        verify(productRepository).existsBySku("TEST-SKU-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProduct_MerchantNotFound() {
        // Given
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        productApplicationService.createProduct(createProductRequest);

        // Then - exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProduct_SkuExists() {
        // Given
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(mockMerchant));
        when(productRepository.existsBySku(anyString())).thenReturn(true);

        // When
        productApplicationService.createProduct(createProductRequest);

        // Then - exception expected
    }

    @Test
    public void testAddStock_Success() {
        // Given
        when(productRepository.findBySku(anyString())).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // When
        ProductResponse result = productApplicationService.addStock("TEST-SKU-001", addStockRequest);

        // Then
        assertNotNull(result);
        verify(productRepository).findBySku("TEST-SKU-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddStock_ProductNotFound() {
        // Given
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        // When
        productApplicationService.addStock("TEST-SKU-001", addStockRequest);

        // Then - exception expected
    }

    @Test
    public void testGetProductBySku_Success() {
        // Given
        when(productRepository.findBySku(anyString())).thenReturn(Optional.of(mockProduct));

        // When
        Optional<ProductResponse> result = productApplicationService.getProductBySku("TEST-SKU-001");

        // Then
        assertTrue(result.isPresent());
        assertEquals("TEST-SKU-001", result.get().getSku());
        verify(productRepository).findBySku("TEST-SKU-001");
    }

    @Test
    public void testGetProductBySku_NotFound() {
        // Given
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        // When
        Optional<ProductResponse> result = productApplicationService.getProductBySku("TEST-SKU-001");

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).findBySku("TEST-SKU-001");
    }

    @Test
    public void testGetProductsByMerchantId_Success() {
        // Given
        List<Product> products = Arrays.asList(mockProduct);
        when(productRepository.findByMerchantId(anyLong())).thenReturn(products);

        // When
        List<ProductResponse> result = productApplicationService.getProductsByMerchantId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST-SKU-001", result.get(0).getSku());
        verify(productRepository).findByMerchantId(1L);
    }

    @Test
    public void testGetActiveProducts_Success() {
        // Given
        List<Product> products = Arrays.asList(mockProduct);
        when(productRepository.findByStatus(any(ProductStatus.class))).thenReturn(products);

        // When
        List<ProductResponse> result = productApplicationService.getActiveProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST-SKU-001", result.get(0).getSku());
        verify(productRepository).findByStatus(ProductStatus.ACTIVE);
    }

    @Test
    public void testGetProductEntityBySku_Success() {
        // Given
        when(productRepository.findBySku(anyString())).thenReturn(Optional.of(mockProduct));

        // When
        Product result = productApplicationService.getProductEntityBySku("TEST-SKU-001");

        // Then
        assertNotNull(result);
        assertEquals("TEST-SKU-001", result.getSku());
        verify(productRepository).findBySku("TEST-SKU-001");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetProductEntityBySku_NotFound() {
        // Given
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        // When
        productApplicationService.getProductEntityBySku("TEST-SKU-001");

        // Then - exception expected
    }

    @Test
    public void testSaveProductEntity_Success() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // When
        Product result = productApplicationService.saveProductEntity(mockProduct);

        // Then
        assertNotNull(result);
        assertEquals("TEST-SKU-001", result.getSku());
        verify(productRepository).save(mockProduct);
    }
}