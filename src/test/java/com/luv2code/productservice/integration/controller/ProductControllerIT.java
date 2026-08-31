package com.luv2code.productservice.integration.controller;

import com.luv2code.productservice.integration.IntegrationTest;
import com.luv2code.productservice.model.Product;
import com.luv2code.productservice.repository.ProductRepository;
import com.luv2code.productservice.rest.dto.ProductRequest;
import com.luv2code.productservice.rest.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ProductControllerIT {

    private static final String PRODUCTS_URL = "/api/v1/products";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void createProduct_validRequest_returnsCreatedProduct() {
        ProductRequest request = new ProductRequest(
                "Test product",
                new BigDecimal("12.34"),
                true
        );

        ProductResponse response = webTestClient.post()
                .uri(PRODUCTS_URL)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.code()).hasSize(10);
        assertThat(response.priceInEur()).isEqualByComparingTo("12.34");
        assertThat(response.priceInUsd()).isNotNull();
        assertThat(response.available()).isTrue();

        Product savedProduct = productRepository.findByCode(response.code())
                .orElseThrow();

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getCode()).isEqualTo(response.code());
        assertThat(savedProduct.getName()).isEqualTo("Test product");
        assertThat(savedProduct.getPriceEur()).isEqualByComparingTo("12.34");
        assertThat(savedProduct.getPriceUsd())
                .isEqualByComparingTo(response.priceInUsd());
        assertThat(savedProduct.isAvailable()).isTrue();
    }

    @Test
    @Sql(scripts = "/sql/products.sql",  executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getProduct_existingProduct_returnsProduct() {
        ProductResponse response = webTestClient.get()
                .uri(PRODUCTS_URL + "/" + "ABC123xyz9")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("ABC123xyz9");
        assertThat(response.priceInEur()).isEqualByComparingTo("12.34");
        assertThat(response.priceInUsd()).isNotNull();
        assertThat(response.available()).isTrue();
    }

    @Test
    @Sql(scripts = "/sql/products.sql",  executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getProducts_productsExist_returnsProducts() {
        List<ProductResponse> response = webTestClient.get()
                .uri(PRODUCTS_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull()
                .extracting(ProductResponse::code)
                .contains("ABC123xyz9", "XYZ987abc1");
    }
}
