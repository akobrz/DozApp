package com.pm.dozapp.controller;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.dto.UserDTO;
import com.pm.dozapp.service.TweetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    // Get info about user: nasa, spacex, boeingspace
    @GetMapping("/user/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ok().body(tweetService.getUserByUsername(username));
    }

    // Get 5 or more tweets of the user
    @GetMapping("/tweets/{username}")
    public ResponseEntity<List<TweetDTO>> getTweets(@PathVariable String username,
                                    @RequestParam(defaultValue = "5") int count) {
        return ok().body(tweetService.getUserTweets(username, count));
    }

    @GetMapping("/db/tweets/{username}")
    public ResponseEntity<List<TweetDTO>> getDbTweetsByUsername(@PathVariable String username) {
        return ok().body(tweetService.getTweetsByUsernameFromDb(username));
    }

    @PostMapping("/db/tweets")
    public ResponseEntity<TweetDTO> createTweetInDb(@RequestBody TweetDTO tweetDTO) {
        return ok().body(tweetService.createTweetInDb(tweetDTO));
    }

}