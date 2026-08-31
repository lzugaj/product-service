package com.luv2code.productservice.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HnbExchangeRateResponse(
        @JsonProperty("srednji_tecaj")
        String middleRate
) {
}