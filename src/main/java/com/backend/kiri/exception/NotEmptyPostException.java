package com.backend.kiri.exception;

public class NotEmptyPostException extends RuntimeException {
    public NotEmptyPostException(String message) {
        super(message);
    }
}
