package com.pm.dozapp.exception;

public class ErrorsException extends RuntimeException {

    private ErrorType errorType;

    public ErrorsException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ErrorType getDozAppError() {
        return errorType;
    }
}
