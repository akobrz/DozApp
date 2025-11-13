package com.pm.dozapp.exception;

public class DozAppException extends RuntimeException {

    private DozAppError dozAppError;

    public DozAppException(DozAppError dozAppError) {
        super(dozAppError.getMessage());
        this.dozAppError = dozAppError;
    }

    public DozAppError getDozAppError() {
        return dozAppError;
    }
}
