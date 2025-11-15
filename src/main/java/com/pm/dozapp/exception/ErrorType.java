package com.pm.dozapp.exception;

public enum ErrorType {
    USER_NOT_FOUND("User does not exist"),
    TWEET_ALREADY_REGISTERED("Tweet already registered"),
    INTERNAL_SERVER_ERROR("Internal server error");

    private String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}