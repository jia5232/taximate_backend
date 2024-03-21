package com.backend.kiri.exception;

import com.backend.kiri.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        return new ErrorResult("EX","알 수 없는 오류가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        return new ErrorResult("BAD", "잘못된 요청입니다.");
    }

    @ExceptionHandler(NotEmptyPostException.class)
    public ResponseEntity<ErrorResult> notEmptyPostExHandle(NotEmptyPostException e){
        ErrorResult errorResult = new ErrorResult("NOT EMPTY POST EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChatRoomFullException.class)
    public ResponseEntity<ErrorResult> chatRoomFullExHandle(ChatRoomFullException e){
        ErrorResult errorResult = new ErrorResult("CHATROOM FULL EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundChatRoomException.class)
    public ResponseEntity<ErrorResult> notFoundChatRoomExHandle(NotFoundChatRoomException e){
        ErrorResult errorResult = new ErrorResult("NOT FOUND CHATROOM EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResult> notFoundMemberExHandle(NotFoundMemberException e){
        ErrorResult errorResult = new ErrorResult("NOT FOUND MEMBER EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<ErrorResult> notFoundPostExHandle(NotFoundPostException e){
        ErrorResult errorResult = new ErrorResult("NOT FOUND POST EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundRefreshTokenException.class)
    public ResponseEntity<ErrorResult> notFoundRefreshTokenExHandle(NotFoundRefreshTokenException e){
        ErrorResult errorResult = new ErrorResult("NOT FOUND TOKEN EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundUniversityException.class)
    public ResponseEntity<ErrorResult> notFoundUniversityExHandle(NotFoundUniversityException e){
        ErrorResult errorResult = new ErrorResult("NOT FOUND TOKEN EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResult> unauthorizedAccessExHandle(UnauthorizedAccessException e){
        ErrorResult errorResult = new ErrorResult("UNAUTHORIZED ACCESS EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistMemberException.class)
    public ResponseEntity<ErrorResult> alreadyExistMemberExHandle(AlreadyExistMemberException e){
        ErrorResult errorResult = new ErrorResult("ALREADY EXIST MEMBER EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughInfoException.class)
    public ResponseEntity<ErrorResult> notEnoughInfoExHandle(NotEnoughInfoException e){
        ErrorResult errorResult = new ErrorResult("NOT ENOUGH INFO EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }
}
