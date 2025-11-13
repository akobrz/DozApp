package com.pm.dozapp.exception;

public class AppException extends RuntimeException {

    private final AppErrorType appErrorType;

    public AppException(AppErrorType appErrorType) {
        super(appErrorType.getMessage());
        this.appErrorType = appErrorType;
    }

    public AppErrorType getErrorType() {
        return appErrorType;
    }
}
