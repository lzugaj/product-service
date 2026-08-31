package com.luv2code.productservice.exception;

import org.springframework.http.HttpStatus;

public class ExchangeRateServiceException extends ProductServiceException {

    public ExchangeRateServiceException(String message) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                message,
                "exchange-rate.unavailable"
        );
    }
}
