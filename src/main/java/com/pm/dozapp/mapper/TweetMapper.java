package com.pm.dozapp.mapper;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.model.Tweet;

public class TweetMapper {

    public static TweetDTO getDTO(Tweet tweet) {
        TweetDTO dto = new TweetDTO();
        dto.setTweetId(tweet.getTweetId());
        dto.setAuthorId(tweet.getAuthorId());
        dto.setCreatedAt(tweet.getCreatedAt());
        dto.setText(tweet.getText());
        return dto;
    }

    public static Tweet getEntity(TweetDTO tweetDTO) {
        Tweet entity = new Tweet();
        entity.setTweetId(tweetDTO.getTweetId());
        entity.setAuthorId(tweetDTO.getAuthorId());
        entity.setCreatedAt(tweetDTO.getCreatedAt());
        entity.setText(tweetDTO.getText());
        return entity;
    }

}
