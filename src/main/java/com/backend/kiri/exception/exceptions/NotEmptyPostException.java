package com.backend.kiri.exception.exceptions;

public class NotEmptyPostException extends IllegalStateException {
    public NotEmptyPostException(String message) {
        super(message);
    }
}
