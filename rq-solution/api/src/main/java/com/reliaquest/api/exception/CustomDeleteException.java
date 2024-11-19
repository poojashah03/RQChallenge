package com.reliaquest.api.exception;

public class CustomDeleteException extends RuntimeException {
    public CustomDeleteException(String message) {
        super(message);
    }

    public CustomDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
