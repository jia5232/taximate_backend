package com.backend.kiri.exception;

public class DuplicatedNicknameException extends RuntimeException{
    public DuplicatedNicknameException(String message) {
        super(message);
    }
}
