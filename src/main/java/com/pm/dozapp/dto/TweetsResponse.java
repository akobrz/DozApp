package com.pm.dozapp.dto;

import java.util.List;

public class TweetsResponse {

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