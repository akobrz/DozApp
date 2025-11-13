package com.pm.dozapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TweetsResponse {

    @JsonProperty("data")
    private List<TweetDTO> data;

    public TweetsResponse() {
    }

    public TweetsResponse(List<TweetDTO> data) {
        this.data = data;
    }

    public List<TweetDTO> getData() {
        return data;
    }

    public void setData(List<TweetDTO> data) {
        this.data = data;
    }
}