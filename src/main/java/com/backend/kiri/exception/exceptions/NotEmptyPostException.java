package com.backend.kiri.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotEmptyPostException extends IllegalStateException {
    public NotEmptyPostException(String message) {
        super(message);
    }
}
