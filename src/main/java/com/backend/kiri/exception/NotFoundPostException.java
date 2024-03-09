package com.backend.kiri.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "알 수 없는 오류가 발생했습니다.")
public class NotFoundPostException extends IllegalStateException{
    public NotFoundPostException(String message) {
        super(message);
    }
}
