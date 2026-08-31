package com.luv2code.productservice.integration.repository;

import com.luv2code.productservice.integration.IntegrationTest;
import com.luv2code.productservice.model.Product;
import com.luv2code.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
    }

    @Test
    void findByCustomerId_existingCustomerId_returnsCart() {
        Product product = Product.builder()
                .name("Test Product")
                .priceEur(BigDecimal.valueOf(100.00))
                .priceUsd(BigDecimal.valueOf(120.00))
                .available(true)
                .build();

        productRepository.save(product);

        var result = productRepository.findByCode(product.getCode());

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Product::getName)
                .isEqualTo("Test Product");
    }

    @Test
    void findByCode_nonExistingCode_returnsEmpty() {
        var result = productRepository.findByCode("non-existing-code");

        assertThat(result)
                .isEmpty();
    }
}
