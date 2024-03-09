package com.backend.kiri.exception.exceptions;

public class NotFoundPostException extends IllegalStateException{
    public NotFoundPostException(String message) {
        super(message);
    }
}
