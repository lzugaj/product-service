package com.luv2code.productservice.service;

import com.luv2code.productservice.exception.ProductNotFoundException;
import com.luv2code.productservice.model.Product;
import com.luv2code.productservice.repository.ProductRepository;
import com.luv2code.productservice.rest.dto.ProductRequest;
import com.luv2code.productservice.rest.dto.ProductResponse;
import com.luv2code.productservice.rest.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private HnbExchangeRateService hnbExchangeRateService;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_validRequest_returnsProductResponse() {
        ProductRequest request = new ProductRequest(
                "Test product",
                new BigDecimal("12.34"),
                true
        );

        BigDecimal priceUsd = new BigDecimal("14.38");

        Product product = new Product(
                "Test product",
                new BigDecimal("12.34"),
                priceUsd,
                true
        );

        ProductResponse expectedResponse = new ProductResponse(
                product.getCode(),
                new BigDecimal("12.34"),
                priceUsd,
                true
        );

        when(hnbExchangeRateService.convertEurToUsd(request.priceEur()))
                .thenReturn(priceUsd);

        when(productMapper.toEntity(request, priceUsd))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse result = productService.createProduct(request);

        assertThat(result).isEqualTo(expectedResponse);

        verify(hnbExchangeRateService).convertEurToUsd(request.priceEur());
        verify(productMapper).toEntity(request, priceUsd);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(product);
    }

    @Test
    void createProduct_nullRequest_throwsException() {
        assertThatThrownBy(() -> productService.createProduct(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The product request cannot be null");

        verifyNoInteractions(
                productMapper,
                productRepository,
                hnbExchangeRateService
        );
    }

    @Test
    void findByCode_existingProduct_returnsProductResponse() {
        String code = "ABC123xyz9";

        Product product = new Product(
                "Test product",
                new BigDecimal("12.34"),
                new BigDecimal("14.38"),
                true
        );

        ProductResponse expectedResponse = new ProductResponse(
                code,
                new BigDecimal("12.34"),
                new BigDecimal("14.38"),
                true
        );

        when(productRepository.findByCode(code))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse result = productService.findByCode(code);

        assertThat(result).isEqualTo(expectedResponse);

        verify(productRepository).findByCode(code);
        verify(productMapper).toResponse(product);
    }

    @Test
    void findByCode_productDoesNotExist_throwsException() {
        String code = "ABC123xyz9";

        when(productRepository.findByCode(code))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findByCode(code))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product with code '%s' was not found".formatted(code));

        verify(productRepository).findByCode(code);

        verifyNoInteractions(productMapper);
    }

    @Test
    void findByCode_emptyCode_throwsException() {
        assertThatThrownBy(() -> productService.findByCode(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product code cannot be empty");

        verifyNoInteractions(
                productRepository,
                productMapper
        );
    }

    @Test
    void findAll_productsExist_returnsProductResponses() {
        Product firstProduct = new Product(
                "Product 1",
                new BigDecimal("10.00"),
                new BigDecimal("11.00"),
                true
        );

        Product secondProduct = new Product(
                "Product 2",
                new BigDecimal("20.00"),
                new BigDecimal("22.00"),
                false
        );

        ProductResponse firstResponse = new ProductResponse(
                firstProduct.getCode(),
                new BigDecimal("10.00"),
                new BigDecimal("11.00"),
                true
        );

        ProductResponse secondResponse = new ProductResponse(
                secondProduct.getCode(),
                new BigDecimal("20.00"),
                new BigDecimal("22.00"),
                false
        );

        when(productRepository.findAll())
                .thenReturn(List.of(firstProduct, secondProduct));

        when(productMapper.toResponse(firstProduct))
                .thenReturn(firstResponse);

        when(productMapper.toResponse(secondProduct))
                .thenReturn(secondResponse);

        List<ProductResponse> result = productService.findAll();

        assertThat(result)
                .containsExactly(firstResponse, secondResponse);

        verify(productRepository).findAll();
        verify(productMapper).toResponse(firstProduct);
        verify(productMapper).toResponse(secondProduct);
    }

    @Test
    void findAll_noProducts_returnsEmptyList() {
        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> result = productService.findAll();

        assertThat(result).isEmpty();

        verify(productRepository).findAll();
        verifyNoInteractions(productMapper);
    }
}