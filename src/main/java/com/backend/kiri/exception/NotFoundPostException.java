package com.backend.kiri.exception;

public class NotFoundPostException extends RuntimeException{
    public NotFoundPostException(String message) {
        super(message);
    }
}
