package com.backend.kiri.exception;

public class ChatRoomFullException extends RuntimeException{
    public ChatRoomFullException(String message) {
        super(message);
    }
}
