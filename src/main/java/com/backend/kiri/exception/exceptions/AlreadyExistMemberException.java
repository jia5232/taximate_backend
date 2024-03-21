package com.backend.kiri.exception.exceptions;

public class AlreadyExistMemberException extends IllegalStateException{
    public AlreadyExistMemberException(String message) {
        super(message);
    }
}