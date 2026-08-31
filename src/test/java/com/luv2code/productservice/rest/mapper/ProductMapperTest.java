package com.luv2code.productservice.rest.mapper;

import com.luv2code.productservice.model.Product;
import com.luv2code.productservice.rest.dto.ProductRequest;
import com.luv2code.productservice.rest.dto.ProductResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper();

    @Test
    void toEntity_validRequest_returnsProduct() {
        ProductRequest request = mock(ProductRequest.class);

        when(request.name()).thenReturn("Test product");
        when(request.priceEur()).thenReturn(new BigDecimal("12.34"));
        when(request.available()).thenReturn(true);

        BigDecimal priceUsd = new BigDecimal("14.56");

        Product product = productMapper.toEntity(request, priceUsd);

        assertThat(product.getName()).isEqualTo("Test product");
        assertThat(product.getPriceEur()).isEqualByComparingTo("12.34");
        assertThat(product.getPriceUsd()).isEqualByComparingTo("14.56");
        assertThat(product.isAvailable()).isTrue();
        assertThat(product.getCode()).hasSize(10);
    }

    @Test
    void toEntity_productNotAvailable_returnsUnavailableProduct() {
        ProductRequest request = mock(ProductRequest.class);

        when(request.name()).thenReturn("Test product");
        when(request.priceEur()).thenReturn(new BigDecimal("12.34"));
        when(request.available()).thenReturn(false);

        Product product = productMapper.toEntity(
                request,
                new BigDecimal("14.56")
        );

        assertThat(product.isAvailable()).isFalse();
    }

    @Test
    void toEntity_nullRequest_throwsException() {
        assertThatThrownBy(() -> productMapper.toEntity(
                null,
                new BigDecimal("14.56")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The product DTO cannot be null");
    }

    @Test
    void toResponse_validProduct_returnsResponse() {
        Product product = mock(Product.class);

        when(product.getCode()).thenReturn("ABC123xyz9");
        when(product.getPriceEur()).thenReturn(new BigDecimal("12.34"));
        when(product.getPriceUsd()).thenReturn(new BigDecimal("14.56"));
        when(product.isAvailable()).thenReturn(true);

        ProductResponse response = productMapper.toResponse(product);

        assertThat(response.code()).isEqualTo("ABC123xyz9");
        assertThat(response.priceInEur()).isEqualByComparingTo("12.34");
        assertThat(response.priceInUsd()).isEqualByComparingTo("14.56");
        assertThat(response.available()).isTrue();
    }

    @Test
    void toResponse_nullProduct_throwsException() {
        assertThatThrownBy(() -> productMapper.toResponse(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The product DTO cannot be null");
    }

    @Test
    void toResponse_productNotAvailable_returnsUnavailableResponse() {
        Product product = mock(Product.class);

        when(product.getCode()).thenReturn("ABC123xyz9");
        when(product.getPriceEur()).thenReturn(new BigDecimal("12.34"));
        when(product.getPriceUsd()).thenReturn(new BigDecimal("14.56"));
        when(product.isAvailable()).thenReturn(false);

        ProductResponse response = productMapper.toResponse(product);

        assertThat(response.available()).isFalse();
    }
}