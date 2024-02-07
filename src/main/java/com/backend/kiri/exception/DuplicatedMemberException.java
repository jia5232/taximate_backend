package com.backend.kiri.exception;

public class DuplicatedMemberException extends RuntimeException{
    public DuplicatedMemberException(String message) {
        super(message);
    }
}
