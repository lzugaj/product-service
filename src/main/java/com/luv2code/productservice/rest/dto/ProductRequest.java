package com.luv2code.productservice.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name cannot be blank")
        String name,

        @NotNull(message = "Product price in EUR cannot be null")
        @DecimalMin(value = "0.0", message = "Product price in EUR cannot be negative")
        BigDecimal priceEur,

        boolean available
) {}
