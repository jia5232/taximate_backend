package com.backend.kiri.exception.exceptions;

public class UnauthorizedAccessException extends IllegalStateException{
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
