package com.reliaquest.api.exception;

public class CustomCreateException extends RuntimeException {
    public CustomCreateException(String message) {
        super(message);
    }

    public CustomCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
