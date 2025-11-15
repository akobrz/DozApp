package com.pm.dozapp.service;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.dto.TweetsResponse;
import com.pm.dozapp.dto.UserDTO;
import com.pm.dozapp.dto.UserResponse;
import com.pm.dozapp.exception.AppException;
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
import java.util.Optional;

import static com.pm.dozapp.exception.ErrorType.*;
import static com.pm.dozapp.mapper.TweetMapper.getEntity;
import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static java.lang.Math.max;
import static java.util.List.of;
import static java.util.Optional.empty;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

@Service
public class TweetService {

    Logger log = LoggerFactory.getLogger(TweetService.class);

    @Value("${twitter.bearer-token}")
    private String bearerToken;

    private final RestTemplate restTemplate;
    private final TweetRepository tweetRepository;

    private final Map<String, String> validUsers;

    public TweetService(RestTemplate restTemplate, TweetRepository tweetRepository) {
        this.restTemplate = restTemplate;
        this.tweetRepository = tweetRepository;
        this.validUsers = prepareValidUsersMap();
    }

    public UserDTO getUserByUsername(String username) {
        String userUrl = "https://api.twitter.com/2/users/by/username/" + username + "?user.fields=username,name";
        return performGetUser(userUrl, getHttpEntity());
    }

    private UserDTO performGetUser(String userUrl, HttpEntity<String> entity) {
        try {
            return getUser(userUrl, entity);
        } catch (Exception e) {
            log.error("Error during getting user: {}", e.getMessage());
            throw new AppException(INTERNAL_SERVER_ERROR);
        }
    }

    private UserDTO getUser(String userUrl, HttpEntity<String> entity) {
        ResponseEntity<UserResponse> response = restTemplate.exchange(userUrl, GET, entity, UserResponse.class);
        log.info("Response for /user/{username}: status={}, body={}", response.getStatusCode(), response.getBody());
        if (isGetUserResponseSuccessful(response)) return response.getBody().getData();
        throw new AppException(USER_NOT_FOUND);
    }

    private static boolean isGetUserResponseSuccessful(ResponseEntity<UserResponse> response) {
        return OK.equals(response.getStatusCode());
    }

    public List<TweetDTO> getUserTweets(String username, int count) {
        validateUsername(username, validUsers);
        return getTweetsByUserId(validUsers.get(username), count);
    }

    private List<TweetDTO> getTweetsByUserId(String userId, int count) {
        String tweetsUrl = "https://api.twitter.com/2/users/" + userId + "/tweets" +
                "?max_results=" + max(count, 5) + "&tweet.fields=created_at,author_id,text";
        return performGetTweets(tweetsUrl, getHttpEntity());
    }

    private List<TweetDTO> performGetTweets(String tweetsUrl, HttpEntity<String> entity) {
        try {
            return getNewTweets(tweetsUrl, entity);
        } catch (Exception e) {
            log.error("Error during getting tweets: {}", e.getMessage());
            throw new AppException(INTERNAL_SERVER_ERROR);
        }
    }

    private List<TweetDTO> getNewTweets(String tweetsUrl, HttpEntity<String> entity) {
        ResponseEntity<TweetsResponse> response = restTemplate.exchange(tweetsUrl, GET, entity, TweetsResponse.class);
        log.info("Response for /tweets/{username}: status={}, body={}", response.getStatusCode(), response.getBody());
        if (isGetTweetsResponseSuccessful(response)) return saveAllNotExistingTweets(response);
        return of();
    }

    private boolean isGetTweetsResponseSuccessful(ResponseEntity<TweetsResponse> response) {
        return OK.equals(response.getStatusCode());
    }

    public String getResultForConsoleInput(String text) {
        long tweetsFound = processConsoleInput(text);
        return tweetsFound > 0 ? "New tweets found: " + tweetsFound : "No new tweets found";
    }

    private long processConsoleInput(String text) {
        return isNotEmpty(text) ? (long) getUserTweets(text, 5).size() : 0;
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        return new HttpEntity<>(headers);
    }

    private List<TweetDTO> saveAllNotExistingTweets(ResponseEntity<TweetsResponse> response) {
        return response.getBody().getData().stream()
                .map(this::saveNotExistingTweet)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TweetMapper::getDTO)
                .toList();
    }

    private Optional<Tweet> saveNotExistingTweet(TweetDTO tweetDTO) {
        return !tweetRepository.existsByTweetId(tweetDTO.getTweetId())
                ? Optional.of(tweetRepository.save(getEntity(tweetDTO))) : empty();
    }

    public List<TweetDTO> getTweetsByUsernameFromDb(String username) {
        validateUsername(username, validUsers);
        return tweetRepository
                .findAllByAuthorId(validUsers.get(username)).stream()
                .map(TweetMapper::getDTO)
                .toList();
    }

    public TweetDTO createTweetInDb(TweetDTO tweetDTO) {
        validateUsersIds(tweetDTO, validUsers);
        validateDuplicate(tweetDTO);
        return TweetMapper.getDTO(tweetRepository.save(getEntity(tweetDTO)));
    }

    private void validateDuplicate(TweetDTO tweetDTO) {
        if (tweetRepository.existsByTweetId(tweetDTO.getTweetId())) {
            throw new AppException(TWEET_ALREADY_REGISTERED);
        }
    }

    private static void validateUsersIds(TweetDTO tweetDTO, Map<String, String> users) {
        if (!users.containsValue(tweetDTO.getAuthorId())) {
            throw new AppException(USER_NOT_FOUND);
        }
    }

    private static void validateUsername(String username, Map<String, String> users) {
        if (!users.containsKey(username.toLowerCase())) {
            throw new AppException(USER_NOT_FOUND);
        }
    }

    private static Map<String, String> prepareValidUsersMap() {
        Map<String, String> users = new HashMap<>();
        users.put("nasa", "11348282");
        users.put("spacex", "34743251");
        users.put("boeingspace", "3474004752");
        return users;
    }

}