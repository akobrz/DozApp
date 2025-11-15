package com.pm.dozapp.exception;

public class AppException extends RuntimeException {

    private final ErrorType errorType;

    public AppException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
