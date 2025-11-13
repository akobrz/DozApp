package com.pm.dozapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ErrorsException.class)
    public ResponseEntity<ErrorInfo> handleException(ErrorsException e) {
        ErrorInfo newErrorInfo = new ErrorInfo(e.getDozAppError().getMessage());
        return switch (e.getDozAppError()) {
            case USER_NOT_FOUND ->
                    ResponseEntity.status(NOT_FOUND).body(newErrorInfo);
            case TWEET_ALREADY_REGISTERED ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(newErrorInfo);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(newErrorInfo);
        };
    }
    
}
