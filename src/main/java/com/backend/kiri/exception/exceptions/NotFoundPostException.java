package com.backend.kiri.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotFoundPostException extends IllegalStateException{
    public NotFoundPostException(String message) {
        super(message);
    }
}
