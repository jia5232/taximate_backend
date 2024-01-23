package com.backend.kiri.exception;

public class NotFoundRefreshTokenException extends RuntimeException{
    public NotFoundRefreshTokenException(String message) {
        super(message);
    }
}
