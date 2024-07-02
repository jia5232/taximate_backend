package com.backend.taximate.exception.exceptions;

public class InvalidAuthNumberException extends IllegalStateException{
    public InvalidAuthNumberException(String message) {
        super(message);
    }
}
