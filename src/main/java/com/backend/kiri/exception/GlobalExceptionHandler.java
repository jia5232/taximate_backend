package com.backend.kiri.exception;

import com.backend.kiri.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> exHandle(Exception e) {
        ErrorResult errorResult = new ErrorResult("EX", "알 수 없는 오류가 발생했습니다.");
        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> illegalExHandle(IllegalArgumentException e) {
        ErrorResult errorResult = new ErrorResult("BAD", "잘못된 요청입니다.");
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResult> handleNoHandlerFoundException(NoHandlerFoundException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND", "요청한 페이지를 찾을 수 없습니다.");
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotEmptyPostException.class)
    public ResponseEntity<ErrorResult> notEmptyPostExHandle(NotEmptyPostException e) {
        ErrorResult errorResult = new ErrorResult("NOT EMPTY POST EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChatRoomFullException.class)
    public ResponseEntity<ErrorResult> chatRoomFullExHandle(ChatRoomFullException e) {
        ErrorResult errorResult = new ErrorResult("CHATROOM FULL EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundChatRoomException.class)
    public ResponseEntity<ErrorResult> notFoundChatRoomExHandle(NotFoundChatRoomException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND CHATROOM EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResult> notFoundMemberExHandle(NotFoundMemberException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND MEMBER EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<ErrorResult> notFoundPostExHandle(NotFoundPostException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND POST EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundRefreshTokenException.class)
    public ResponseEntity<ErrorResult> notFoundRefreshTokenExHandle(NotFoundRefreshTokenException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND TOKEN EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundUniversityException.class)
    public ResponseEntity<ErrorResult> notFoundUniversityExHandle(NotFoundUniversityException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND UNIVERSITY EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResult> unauthorizedAccessExHandle(UnauthorizedAccessException e) {
        ErrorResult errorResult = new ErrorResult("UNAUTHORIZED ACCESS EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyExistMemberException.class)
    public ResponseEntity<ErrorResult> alreadyExistMemberExHandle(AlreadyExistMemberException e) {
        ErrorResult errorResult = new ErrorResult("ALREADY EXIST MEMBER EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughInfoException.class)
    public ResponseEntity<ErrorResult> notEnoughInfoExHandle(NotEnoughInfoException e) {
        ErrorResult errorResult = new ErrorResult("NOT ENOUGH INFO EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAuthNumberException.class)
    public ResponseEntity<ErrorResult> invalidAuthNumberExHandle(InvalidAuthNumberException e) {
        ErrorResult errorResult = new ErrorResult("INVALID AUTH NUMBER EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResult> incorrectPasswordExHandle(IncorrectPasswordException e) {
        ErrorResult errorResult = new ErrorResult("INCORRECT PASSWORD EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyBlockedException.class)
    public ResponseEntity<ErrorResult> alreadyBlockedExHandle(AlreadyBlockedException e) {
        ErrorResult errorResult = new ErrorResult("ALREADY BLOCKED EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }
}
