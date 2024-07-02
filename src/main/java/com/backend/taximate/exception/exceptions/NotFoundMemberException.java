package com.backend.taximate.exception.exceptions;

public class NotFoundMemberException extends IllegalStateException{
    public NotFoundMemberException(String message) {
        super(message);
    }
}
