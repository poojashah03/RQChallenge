package com.reliaquest.api.exception;

public class CustomNoDataFoundException extends RuntimeException{
    public CustomNoDataFoundException(String message) {
        super(message);
    }

    public CustomNoDataFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
