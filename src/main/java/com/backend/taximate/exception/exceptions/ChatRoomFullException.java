package com.backend.taximate.exception.exceptions;

public class ChatRoomFullException extends IllegalStateException{
    public ChatRoomFullException(String message) {
        super(message);
    }
}
