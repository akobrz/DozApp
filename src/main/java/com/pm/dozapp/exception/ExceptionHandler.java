package com.pm.dozapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = AppException.class)
    public ResponseEntity<ErrorInfo> handleException(AppException e) {
        ErrorInfo newErrorInfo = new ErrorInfo(e.getErrorType().getMessage());
        return switch (e.getErrorType()) {
            case USER_NOT_FOUND ->
                    ResponseEntity.status(NOT_FOUND).body(newErrorInfo);
            case TWEET_ALREADY_REGISTERED ->
                    ResponseEntity.status(CONFLICT).body(newErrorInfo);
            case INTERNAL_SERVER_ERROR ->
                    ResponseEntity.status(INTERNAL_SERVER_ERROR).body(newErrorInfo);
        };
    }
    
}
