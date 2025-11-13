package com.pm.dozapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class AppExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = AppException.class)
    public ResponseEntity<ErrorInfo> handleException(AppException e) {
        ErrorInfo newErrorInfo = new ErrorInfo(e.getErrorType().getMessage());
        return switch (e.getErrorType()) {
            case USER_NOT_FOUND -> status(NOT_FOUND).body(newErrorInfo);
            case TWEET_ALREADY_REGISTERED -> status(CONFLICT).body(newErrorInfo);
            case TOO_MANY_REQUESTS -> status(TOO_MANY_REQUESTS).body(newErrorInfo);
            case SERVER_ERROR -> status(INTERNAL_SERVER_ERROR).body(newErrorInfo);
        };
    }
    
}
