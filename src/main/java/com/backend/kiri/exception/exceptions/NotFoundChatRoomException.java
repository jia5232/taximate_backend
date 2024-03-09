package com.backend.kiri.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotFoundChatRoomException extends IllegalStateException{
    public NotFoundChatRoomException(String message) {
        super(message);
    }
}
