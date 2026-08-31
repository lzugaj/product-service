package com.luv2code.productservice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void constructor_validArguments_productCreated() {
        Product product = new Product(
                "Test product",
                new BigDecimal("12.34"),
                new BigDecimal("14.56"),
                true
        );

        assertThat(product.getName()).isEqualTo("Test product");
        assertThat(product.getPriceEur()).isEqualByComparingTo("12.34");
        assertThat(product.getPriceUsd()).isEqualByComparingTo("14.56");
        assertThat(product.isAvailable()).isTrue();
        assertThat(product.getCode()).hasSize(10);
    }

    @Test
    void constructor_productNotAvailable_availableIsFalse() {
        Product product = new Product(
                "Test product",
                new BigDecimal("12.34"),
                new BigDecimal("14.56"),
                false
        );

        assertThat(product.isAvailable()).isFalse();
    }

    @Test
    void constructor_emptyName_throwsException() {
        assertThatThrownBy(() -> new Product(
                "",
                new BigDecimal("12.34"),
                new BigDecimal("14.56"),
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be empty");
    }

    @Test
    void constructor_nullName_throwsException() {
        assertThatThrownBy(() -> new Product(
                null,
                new BigDecimal("12.34"),
                new BigDecimal("14.56"),
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be empty");
    }

    @Test
    void constructor_nullPriceEur_throwsException() {
        assertThatThrownBy(() -> new Product(
                "Test product",
                null,
                new BigDecimal("14.56"),
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product price in EUR cannot be null");
    }

    @Test
    void constructor_negativePriceEur_throwsException() {
        assertThatThrownBy(() -> new Product(
                "Test product",
                new BigDecimal("-1.00"),
                new BigDecimal("14.56"),
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product price in EUR cannot be negative");
    }

    @Test
    void constructor_nullPriceUsd_throwsException() {
        assertThatThrownBy(() -> new Product(
                "Test product",
                new BigDecimal("12.34"),
                null,
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product price in USD cannot be null");
    }

    @Test
    void constructor_negativePriceUsd_throwsException() {
        assertThatThrownBy(() -> new Product(
                "Test product",
                new BigDecimal("12.34"),
                new BigDecimal("-1.00"),
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product price in USD cannot be negative");
    }

    @Test
    void constructor_zeroPrices_productCreated() {
        Product product = new Product(
                "Test product",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                true
        );

        assertThat(product.getPriceEur()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(product.getPriceUsd()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void constructor_eachProduct_getsUniqueCode() {
        Product first = new Product(
                "Product 1",
                new BigDecimal("10.00"),
                new BigDecimal("11.00"),
                true
        );

        Product second = new Product(
                "Product 2",
                new BigDecimal("20.00"),
                new BigDecimal("22.00"),
                true
        );

        assertThat(first.getCode())
                .hasSize(10)
                .isNotEqualTo(second.getCode());

        assertThat(second.getCode()).hasSize(10);
    }
}