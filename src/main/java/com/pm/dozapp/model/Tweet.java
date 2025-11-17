package com.pm.dozapp.model;

import jakarta.persistence.*;

import java.util.UUID;

import static jakarta.persistence.GenerationType.AUTO;

@Entity(name = "tweets")
public class Tweet {

    @Id
    @GeneratedValue(strategy = AUTO)
    UUID id;

    @Column(name = "tweet_id", unique = true)
    String tweetId;

    @Column(name = "author_id")
    String authorId;

    @Column(name = "created")
    String createdAt;

    @Lob
    @Column(name = "text", columnDefinition = "MEDIUMTEXT")
    String text;

    public Tweet() {
    }

    public Tweet(String tweetId, String authorId, String createdAt, String text) {
        this.tweetId = tweetId;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.text = text;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
