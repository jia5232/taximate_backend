package com.backend.taximate.exception.exceptions;

public class AlreadyBlockedException extends IllegalStateException{
    public AlreadyBlockedException(String message) {
        super(message);
    }
}