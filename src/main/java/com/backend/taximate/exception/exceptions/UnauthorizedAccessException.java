package com.backend.taximate.exception.exceptions;

public class UnauthorizedAccessException extends IllegalStateException{
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
