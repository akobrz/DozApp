package com.pm.dozapp.exception;

public enum AppErrorType {
    USER_NOT_FOUND("User does not exist"),
    TWEET_ALREADY_REGISTERED("Tweet already registered"),
    TOO_MANY_REQUESTS("Too many requests, wait 15 min and try again"),
    SERVER_ERROR("Internal server error");

    private String message;

    AppErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}