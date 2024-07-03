package com.backend.taximate.exception.exceptions;

public class NotFoundRefreshTokenException extends IllegalStateException{
    public NotFoundRefreshTokenException(String message) {
        super(message);
    }
}
