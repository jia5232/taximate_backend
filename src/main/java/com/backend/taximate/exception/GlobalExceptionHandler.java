package com.backend.taximate.exception;

import com.backend.taximate.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> exHandle(Exception e) {
        ErrorResult errorResult = new ErrorResult("EX", "알 수 없는 오류가 발생했습니다.");
        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

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
        ErrorResult errorResult = new ErrorResult("NOT_EMPTY_POST_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChatRoomFullException.class)
    public ResponseEntity<ErrorResult> chatRoomFullExHandle(ChatRoomFullException e) {
        ErrorResult errorResult = new ErrorResult("CHATROOM_FULL_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundChatRoomException.class)
    public ResponseEntity<ErrorResult> notFoundChatRoomExHandle(NotFoundChatRoomException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND_CHATROOM_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResult> notFoundMemberExHandle(NotFoundMemberException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND_MEMBER_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<ErrorResult> notFoundPostExHandle(NotFoundPostException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND_POST_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundRefreshTokenException.class)
    public ResponseEntity<ErrorResult> notFoundRefreshTokenExHandle(NotFoundRefreshTokenException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND_TOKEN_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundUniversityException.class)
    public ResponseEntity<ErrorResult> notFoundUniversityExHandle(NotFoundUniversityException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND_UNIVERSITY_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResult> unauthorizedAccessExHandle(UnauthorizedAccessException e) {
        ErrorResult errorResult = new ErrorResult("UNAUTHORIZED_ACCESS_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyExistMemberException.class)
    public ResponseEntity<ErrorResult> alreadyExistMemberExHandle(AlreadyExistMemberException e) {
        ErrorResult errorResult = new ErrorResult("ALREADY_EXIST_MEMBER_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotEnoughInfoException.class)
    public ResponseEntity<ErrorResult> notEnoughInfoExHandle(NotEnoughInfoException e) {
        ErrorResult errorResult = new ErrorResult("NOT_ENOUGH_INFO_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAuthNumberException.class)
    public ResponseEntity<ErrorResult> invalidAuthNumberExHandle(InvalidAuthNumberException e) {
        ErrorResult errorResult = new ErrorResult("INVALID_AUTH_NUMBER_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResult> incorrectPasswordExHandle(IncorrectPasswordException e) {
        ErrorResult errorResult = new ErrorResult("INCORRECT_PASSWORD_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyBlockedException.class)
    public ResponseEntity<ErrorResult> alreadyBlockedExHandle(AlreadyBlockedException e) {
        ErrorResult errorResult = new ErrorResult("ALREADY_BLOCKED_EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }
}
