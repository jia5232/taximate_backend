package com.backend.kiri.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "권한이 없습니다.")
public class UnauthorizedAccessException extends IllegalStateException{
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
