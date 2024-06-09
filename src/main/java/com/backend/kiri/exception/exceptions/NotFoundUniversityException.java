package com.backend.kiri.exception.exceptions;

public class NotFoundUniversityException extends IllegalStateException{
    public NotFoundUniversityException(String message) {
        super(message);
    }
}