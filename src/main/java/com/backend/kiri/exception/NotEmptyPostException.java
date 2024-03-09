package com.backend.kiri.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "참여자가 있어 삭제할 수 없습니다.")
public class NotEmptyPostException extends IllegalStateException {
    public NotEmptyPostException(String message) {
        super(message);
    }
}
