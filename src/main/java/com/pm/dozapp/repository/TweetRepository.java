package com.pm.dozapp.repository;

import com.pm.dozapp.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, String> {

    List<Tweet> findAllByAuthorId(String authorId);

    boolean existsByTweetId(String tweetId);

}
