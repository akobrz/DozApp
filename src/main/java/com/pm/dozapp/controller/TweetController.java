package com.pm.dozapp.controller;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.dto.UserDTO;
import com.pm.dozapp.service.TweetService;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pm.dozapp.controller.ConsoleTemplate.HTML_TEMPLATE;
import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static java.lang.String.format;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/")
    public String consoleForm(Model model) {
        return HTML_TEMPLATE;
    }

    @PostMapping("/")
    public String consoleSubmit(@RequestParam("text") String text, @RequestParam(defaultValue = "5") int count, Model model) {
        processConsoleRequest(text, count);
        return HTML_TEMPLATE;
    }

    private void processConsoleRequest(String text, int count) {
        if (isNotEmpty(text)) {
            ok().body(tweetService.getUserTweets(text, count));
        }
    }

    // Get info about user: nasa, spacex, boeingspace
    @GetMapping("/user/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ok().body(tweetService.getUserByUsername(username));
    }

    // Get 5 or more last tweets of the user
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
    @PostMapping("/db/tweets")
    public ResponseEntity<TweetDTO> createTweetInDb(@RequestBody TweetDTO tweetDTO) {
        return ok().body(tweetService.createTweetInDb(tweetDTO));
    }

}