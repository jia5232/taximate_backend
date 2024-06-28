package com.backend.kiri.exception.exceptions;

public class AlreadyBlockedException extends IllegalStateException{
    public AlreadyBlockedException(String message) {
        super(message);
    }
}