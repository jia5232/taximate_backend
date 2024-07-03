package com.backend.taximate.exception.exceptions;

public class NotEnoughInfoException extends IllegalStateException{
    public NotEnoughInfoException(String message) {
        super(message);
    }
}
