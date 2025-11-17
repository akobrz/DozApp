package com.pm.dozapp.mapper;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.model.Tweet;

public class TweetMapper {

    public static TweetDTO getDTO(Tweet tweet) {
        return new TweetDTO(tweet.getTweetId(), tweet.getCreatedAt(), tweet.getAuthorId(), tweet.getText());
    }

    public static Tweet getEntity(TweetDTO tweetDTO) {
        return new Tweet(tweetDTO.getTweetId(), tweetDTO.getAuthorId(), tweetDTO.getCreatedAt(), tweetDTO.getText());
    }

}
