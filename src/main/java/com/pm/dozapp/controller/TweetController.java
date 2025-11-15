package com.pm.dozapp.controller;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.dto.UserDTO;
import com.pm.dozapp.service.TweetService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pm.dozapp.templates.ConsoleTemplate.HTML_TEMPLATE;
import static java.lang.String.format;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    // Get console
    @GetMapping("/")
    public String getConsoleForm(Model model) {
        return format(HTML_TEMPLATE, "Please enter one of the twitter user to get new tweets");
    }

    @PostMapping("/")
    public String performConsoleSubmit(@RequestParam("text") String text, Model model) {
        return format(HTML_TEMPLATE, tweetService.getResultForConsoleInput(text));
    }

    // Get info about user: nasa, spacex, boeingspace
    @GetMapping("/user/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ok().body(tweetService.getUserByUsername(username));
    }

    // Get 5 or more new tweets of the user
    @GetMapping("/tweets/{username}")
    public ResponseEntity<List<TweetDTO>> getTweets(@PathVariable String username,
                                    @RequestParam(defaultValue = "5") int count) {
        return ok().body(tweetService.getUserTweets(username, count));
    }

    // Get tweets from database for specific user
    @GetMapping("/db/tweets/{username}")
    public ResponseEntity<List<TweetDTO>> getDbTweetsByUsername(@PathVariable String username) {
        return ok().body(tweetService.getTweetsByUsernameFromDb(username));
    }

    //Create new tweet via post
    @PostMapping("/db/tweet")
    public ResponseEntity<TweetDTO> createTweetInDb(@RequestBody TweetDTO tweetDTO) {
        return ok().body(tweetService.createTweetInDb(tweetDTO));
    }

}