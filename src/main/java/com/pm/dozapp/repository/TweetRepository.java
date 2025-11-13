package com.pm.dozapp.repository;

import com.pm.dozapp.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, String> {

    List<Tweet> findAllByAuthorIdOrderByCreatedAtDesc(String authorId);

    boolean existsByTweetId(String tweetId);

    @Query("SELECT t.tweetId FROM tweets t WHERE t.tweetId IN :tweetIds")
    List<String> findExistingTweetIds(@Param("tweetIds") List<String> tweetIds);

}
