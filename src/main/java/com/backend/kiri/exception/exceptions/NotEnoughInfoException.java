package com.backend.kiri.exception.exceptions;

public class NotEnoughInfoException extends IllegalStateException{
    public NotEnoughInfoException(String message) {
        super(message);
    }
}
