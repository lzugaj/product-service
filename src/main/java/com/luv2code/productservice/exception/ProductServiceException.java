package com.luv2code.productservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductServiceException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;
    private final String messageKey;
    private final Throwable cause;

    public ProductServiceException(HttpStatus httpStatus, String message, String messageKey) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.messageKey = messageKey;
        this.cause = null;
    }
}
