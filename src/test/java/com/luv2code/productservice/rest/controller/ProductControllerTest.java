package com.luv2code.productservice.rest.controller;

import com.luv2code.productservice.rest.dto.ProductRequest;
import com.luv2code.productservice.rest.dto.ProductResponse;
import com.luv2code.productservice.service.ProductService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void getProduct_existingProduct_returnsProduct() {
        String code = "ABC123xyz9";

        ProductResponse response = new ProductResponse(
                code,
                "Test",
                new BigDecimal("12.34"),
                new BigDecimal("14.38"),
                true
        );

        when(productService.findByCode(code))
                .thenReturn(response);

        ProductResponse result = productController.getProduct(code);

        assertThat(result).isEqualTo(response);

        verify(productService).findByCode(code);
    }

    @Test
    void getProducts_productsExist_returnsProducts() {
        ProductResponse firstProduct = new ProductResponse(
                "ABC123xyz9",
                "Test1",
                new BigDecimal("12.34"),
                new BigDecimal("14.38"),
                true
        );

        ProductResponse secondProduct = new ProductResponse(
                "XYZ987abc1",
                "Test2",
                new BigDecimal("20.00"),
                new BigDecimal("23.00"),
                false
        );

        List<ProductResponse> products = List.of(
                firstProduct,
                secondProduct
        );

        when(productService.findAll())
                .thenReturn(products);

        List<ProductResponse> result = productController.getProducts();

        assertThat(result).containsExactlyElementsOf(products);

        verify(productService).findAll();
    }

    @Test
    void getProducts_noProducts_returnsEmptyList() {
        when(productService.findAll())
                .thenReturn(List.of());

        List<ProductResponse> result = productController.getProducts();

        assertThat(result).isEmpty();

        verify(productService).findAll();
    }

    @Test
    void createProduct_negativePrice_requestIsInvalid() {
        ProductRequest request = new ProductRequest(
                "Test product",
                new BigDecimal("-1.00"),
                true
        );

        Set<ConstraintViolation<ProductRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getMessage().equals("Product price in EUR cannot be negative"));

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_emptyName_requestIsInvalid() {
        ProductRequest request = new ProductRequest(
                "",
                new BigDecimal("12.34"),
                true
        );

        Set<ConstraintViolation<ProductRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getMessage().equals("Product name cannot be blank"));

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_validRequest_returnsCreatedProduct() {
        ProductRequest request = new ProductRequest(
                "Test product",
                new BigDecimal("12.34"),
                true
        );

        ProductResponse response = new ProductResponse(
                "ABC123xyz9",
                "Test product",
                new BigDecimal("12.34"),
                new BigDecimal("14.38"),
                true
        );

        when(productService.createProduct(request))
                .thenReturn(response);

        ProductResponse result = productController.createProduct(request);

        assertThat(result).isEqualTo(response);

        verify(productService).createProduct(request);
    }
}