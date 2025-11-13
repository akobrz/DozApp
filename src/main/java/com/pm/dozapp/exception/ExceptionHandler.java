package com.pm.dozapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = DozAppException.class)
    public ResponseEntity<ErrorInfo> handleException(DozAppException e) {
        return switch (e.getDozAppError()) {
            case USER_NOT_FOUND ->
                    ResponseEntity.status(NOT_FOUND).body(new ErrorInfo(e.getDozAppError().getMessage()));
            case TWEET_ALREADY_REGISTERED ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorInfo(e.getDozAppError().getMessage()));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorInfo(e.getDozAppError().getMessage()));
        };
    }
    
}
