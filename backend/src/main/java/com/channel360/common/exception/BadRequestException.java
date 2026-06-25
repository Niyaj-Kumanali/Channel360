package com.channel360.common.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s is invalid with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
