package com.pm.dozapp.service;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.dto.TweetsResponse;
import com.pm.dozapp.dto.UserDTO;
import com.pm.dozapp.dto.UserResponse;
import com.pm.dozapp.exception.DozAppException;
import com.pm.dozapp.mapper.TweetMapper;
import com.pm.dozapp.model.Tweet;
import com.pm.dozapp.repository.TweetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pm.dozapp.exception.DozAppError.TWEET_ALREADY_REGISTERED;
import static com.pm.dozapp.exception.DozAppError.USER_NOT_FOUND;
import static java.lang.Math.max;
import static java.util.List.of;
import static org.springframework.http.HttpMethod.GET;

@Service
public class TweetService {

    Logger log = LoggerFactory.getLogger(TweetService.class);

    @Value("${twitter.bearer-token}")
    private String bearerToken;

    private final RestTemplate restTemplate;
    private final TweetRepository tweetRepository;

    public TweetService(RestTemplate restTemplate, TweetRepository tweetRepository) {
        this.restTemplate = restTemplate;
        this.tweetRepository = tweetRepository;
    }

    public UserDTO getUserByUsername(String username) {
        String userUrl = "https://api.twitter.com/2/users/by/username/" + username + "?user.fields=username,name";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserResponse> response = restTemplate.exchange(userUrl, GET, entity, UserResponse.class);
            log.info("Response for /user/{username}: status={}, body={}", response.getStatusCode(), response.getBody());
            if (response.getBody() != null && null != response.getBody().getData()) {
                return response.getBody().getData();
            }
            throw new DozAppException(USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error during getting user: {}", e.getMessage());
            throw e;
        }
    }

    public List<TweetDTO> getUserTweets(String username, int count) {
        Map<String, String> users = prepareUsersMap();
        validateUsername(username, users);
        return getTweetsByUserId(users.getOrDefault(username, users.get("nasa")), count);
    }

    private List<TweetDTO> getTweetsByUserId(String userId, int count) {
        String tweetsUrl = "https://api.twitter.com/2/users/" + userId + "/tweets" +
                "?max_results=" + max(count, 5) +
                "&tweet.fields=created_at,author_id,text";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<TweetsResponse> response = restTemplate.exchange(tweetsUrl, GET, entity, TweetsResponse.class);
            log.info("Response /tweets: status={}, body={}", response.getStatusCode(), response.getBody());
            if (response.getBody() != null) {
                saveAllNotExistingTweets(response);
                return response.getBody().getData();
            }
            return of();
        } catch (Exception e) {
            log.error("Error for getting tweets: {}", e.getMessage());
            throw e;
        }
    }

    private void saveAllNotExistingTweets(ResponseEntity<TweetsResponse> response) {
        response.getBody().getData().forEach(this::saveNotExistingTweet);
    }

    private void saveNotExistingTweet(TweetDTO tweetDTO) {
        if (!tweetRepository.existsByTweetId(tweetDTO.getTweetId())) {
            tweetRepository.save(TweetMapper.getEntity(tweetDTO));
        }
    }

    public List<TweetDTO> getTweetsByUsernameFromDb(String username) {
        Map<String, String> users = prepareUsersMap();
        validateUsername(username, users);
        List<Tweet> tweets = tweetRepository.findAllByAuthorId(users.get(username));
        return tweets.stream().map(TweetMapper::getDTO).toList();
    }

    public TweetDTO createTweetInDb(TweetDTO tweetDTO) {
        Map<String, String> users = prepareUsersMap();
        validateUsersIds(tweetDTO, users);
        validateDuplicate(tweetDTO);
        Tweet newTweet = tweetRepository.save(TweetMapper.getEntity(tweetDTO));
        return TweetMapper.getDTO(newTweet);
    }

    private void validateDuplicate(TweetDTO tweetDTO) {
        if (tweetRepository.existsByTweetId(tweetDTO.getTweetId())) {
            throw new DozAppException(TWEET_ALREADY_REGISTERED);
        }
    }

    private static void validateUsersIds(TweetDTO tweetDTO, Map<String, String> users) {
        if (!users.containsValue(tweetDTO.getAuthorId())) {
            throw new DozAppException(USER_NOT_FOUND);
        }
    }

    private static void validateUsername(String username, Map<String, String> users) {
        if (!users.containsKey(username)) {
            throw new DozAppException(USER_NOT_FOUND);
        }
    }

    private static Map<String, String> prepareUsersMap() {
        Map<String, String> users = new HashMap<>();
        users.put("nasa", "11348282");
        users.put("spacex", "34743251");
        users.put("boeingspace", "3474004752");
        return users;
    }
}