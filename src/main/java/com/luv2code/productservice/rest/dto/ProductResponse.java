package com.luv2code.productservice.rest.dto;

import java.math.BigDecimal;

public record ProductResponse(
        String code,
        String name,
        BigDecimal priceInEur,
        BigDecimal priceInUsd,
        boolean available
) {}