package com.luv2code.productservice.rest.handler;

public record ValidationErrorResponse(String object, String field, Object rejectedValue, String message) {

}
