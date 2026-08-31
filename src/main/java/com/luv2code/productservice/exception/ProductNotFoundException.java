package com.luv2code.productservice.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends ProductServiceException {

    public ProductNotFoundException(String message) {
        super(
                HttpStatus.NOT_FOUND,
                message,
                "product.not_found"
        );
    }
}
