package com.backend.kiri.exception;

import com.backend.kiri.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResult exHandle(Exception e) {
        ErrorResult errorResult = new ErrorResult("EX", "알 수 없는 오류가 발생했습니다.");
        return errorResult;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        ErrorResult errorResult = new ErrorResult("BAD", "잘못된 요청입니다.");
        return errorResult;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResult handleNoHandlerFoundException(NoHandlerFoundException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND", "요청한 페이지를 찾을 수 없습니다.");
        return errorResult;
    }

    @ExceptionHandler(NotEmptyPostException.class)
    public ErrorResult notEmptyPostExHandle(NotEmptyPostException e) {
        ErrorResult errorResult = new ErrorResult("NOT EMPTY POST EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(ChatRoomFullException.class)
    public ErrorResult chatRoomFullExHandle(ChatRoomFullException e) {
        ErrorResult errorResult = new ErrorResult("CHATROOM FULL EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(NotFoundChatRoomException.class)
    public ErrorResult notFoundChatRoomExHandle(NotFoundChatRoomException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND CHATROOM EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ErrorResult notFoundMemberExHandle(NotFoundMemberException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND MEMBER EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(NotFoundPostException.class)
    public ErrorResult notFoundPostExHandle(NotFoundPostException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND POST EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(NotFoundRefreshTokenException.class)
    public ErrorResult notFoundRefreshTokenExHandle(NotFoundRefreshTokenException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND TOKEN EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(NotFoundUniversityException.class)
    public ErrorResult notFoundUniversityExHandle(NotFoundUniversityException e) {
        ErrorResult errorResult = new ErrorResult("NOT FOUND UNIVERSITY EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ErrorResult unauthorizedAccessExHandle(UnauthorizedAccessException e) {
        ErrorResult errorResult = new ErrorResult("UNAUTHORIZED ACCESS EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(AlreadyExistMemberException.class)
    public ErrorResult alreadyExistMemberExHandle(AlreadyExistMemberException e) {
        ErrorResult errorResult = new ErrorResult("ALREADY EXIST MEMBER EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(NotEnoughInfoException.class)
    public ErrorResult notEnoughInfoExHandle(NotEnoughInfoException e) {
        ErrorResult errorResult = new ErrorResult("NOT ENOUGH INFO EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(InvalidAuthNumberException.class)
    public ErrorResult invalidAuthNumberExHandle(InvalidAuthNumberException e) {
        ErrorResult errorResult = new ErrorResult("INVALID AUTH NUMBER EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ErrorResult incorrectPasswordExHandle(IncorrectPasswordException e) {
        ErrorResult errorResult = new ErrorResult("INCORRECT PASSWORD EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler(AlreadyBlockedException.class)
    public ErrorResult alreadyBlockedExHandle(AlreadyBlockedException e) {
        ErrorResult errorResult = new ErrorResult("ALREADY BLOCKED EX", e.getMessage());
        return errorResult;
    }
}
