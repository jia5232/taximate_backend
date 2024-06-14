package com.backend.kiri.exception.exceptions;

public class IncorrectPasswordException extends IllegalStateException{
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
