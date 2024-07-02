package com.backend.taximate.exception.exceptions;

public class NotFoundChatRoomException extends IllegalStateException{
    public NotFoundChatRoomException(String message) {
        super(message);
    }
}
