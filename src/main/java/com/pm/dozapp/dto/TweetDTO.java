package com.pm.dozapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TweetDTO {

    @JsonProperty("id")
    private String tweetId;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("author_id")
    private String authorId;

    @JsonProperty("text")
    private String text;

    public TweetDTO() {
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}