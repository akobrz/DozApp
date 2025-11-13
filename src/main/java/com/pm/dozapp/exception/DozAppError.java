package com.pm.dozapp.exception;

public enum DozAppError {
    USER_NOT_FOUND("User does not exist"),
    TWEET_ALREADY_REGISTERED("Tweet already registered");

    private String message;

    DozAppError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}