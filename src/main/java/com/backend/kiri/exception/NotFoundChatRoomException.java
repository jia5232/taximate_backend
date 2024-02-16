package com.backend.kiri.exception;

public class NotFoundChatRoomException extends RuntimeException{
    public NotFoundChatRoomException(String message) {
        super(message);
    }
}
