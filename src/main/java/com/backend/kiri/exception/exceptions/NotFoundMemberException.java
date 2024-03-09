package com.backend.kiri.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotFoundMemberException extends IllegalStateException{
    public NotFoundMemberException(String message) {
        super(message);
    }
}
