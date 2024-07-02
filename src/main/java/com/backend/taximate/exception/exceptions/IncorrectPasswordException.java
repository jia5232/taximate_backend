package com.backend.taximate.exception.exceptions;

public class IncorrectPasswordException extends IllegalStateException{
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
