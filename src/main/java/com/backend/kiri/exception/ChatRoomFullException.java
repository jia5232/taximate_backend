package com.backend.kiri.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "인원 초과입니다.")
public class ChatRoomFullException extends IllegalStateException{
    public ChatRoomFullException(String message) {
        super(message);
    }
}
