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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pm.dozapp.exception.AppErrorType.*;
import static com.pm.dozapp.mapper.TweetMapper.getEntity;
import static com.pm.dozapp.templates.TwitterUrlTemplates.getTweetsUrl;
import static com.pm.dozapp.templates.TwitterUrlTemplates.getUserUrl;
import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static org.springframework.http.HttpMethod.GET;

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
        return performGetUser(getUserUrl(username), getHttpEntity());
    }

    private UserDTO performGetUser(String userUrl, HttpEntity<String> entity) {
        try {
            return getUserFromUrl(userUrl, entity);
        } catch (Exception e) {
            log.error("Error during getting user: {}", e.getMessage());
            throw e;
        }
    }

    private UserDTO getUserFromUrl(String userUrl, HttpEntity<String> entity) {
        try {
            ResponseEntity<UserResponse> response = restTemplate.exchange(userUrl, GET, entity, UserResponse.class);
            log.info("Response for /user/{username}: status={}, body={}", response.getStatusCode(), response.getBody());
            return response.getBody().getData();
        } catch (HttpClientErrorException e) {
            if (isTooManyRequests(e.getStatusCode())) throw new AppException(TOO_MANY_REQUESTS);
            if (isNotFound(e.getStatusCode())) throw new AppException(USER_NOT_FOUND);
            throw new AppException(SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException(SERVER_ERROR);
        }
    }

    public List<TweetDTO> getUserTweets(String username, int count) {
        validateUsername(username, validUsers);
        return getTweetsByUserId(validUsers.get(username), count);
    }

    private List<TweetDTO> getTweetsByUserId(String userId, int count) {
        return performGetTweets(getTweetsUrl(userId, count), getHttpEntity());
    }

    private List<TweetDTO> performGetTweets(String tweetsUrl, HttpEntity<String> entity) {
        try {
            return getTweetsFromUrl(tweetsUrl, entity);
        } catch (Exception e) {
            log.error("Error during getting tweets: {}", e.getMessage());
            throw e;
        }
    }

    private List<TweetDTO> getTweetsFromUrl(String tweetsUrl, HttpEntity<String> entity) {
        try {
            ResponseEntity<TweetsResponse> response = restTemplate.exchange(tweetsUrl, GET, entity, TweetsResponse.class);
            log.info("Response for /tweets/{username}: status={}, body={}", response.getStatusCode(), response.getBody());
            return saveAllNotExistingTweets(response);
        } catch (HttpClientErrorException e) {
            if (isTooManyRequests(e.getStatusCode())) throw new AppException(TOO_MANY_REQUESTS);
            if (isNotFound(e.getStatusCode())) throw new AppException(USER_NOT_FOUND);
            throw new AppException(SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException(SERVER_ERROR);
        }
    }

    private boolean isTooManyRequests(HttpStatusCode code) {
        return HttpStatus.TOO_MANY_REQUESTS.equals(code);
    }

    private boolean isNotFound(HttpStatusCode code) {
        return HttpStatus.BAD_REQUEST.equals(code);
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
        List<String> newTweetIds = response.getBody().getData().stream().map(TweetDTO::getTweetId).toList();
        List<String> existingTweetIds = tweetRepository.findExistingTweetIds(newTweetIds);

        log.info("Get tweets in response: " + newTweetIds.size());
        response.getBody().getData().forEach(tweet -> log.info(tweet.toString()));

        return response.getBody().getData().stream()
                .filter(tweet -> isTweetNew(tweet, existingTweetIds))
                .map(this::saveNotExistingTweet)
                .map(TweetMapper::getDTO)
                .toList();
    }

    private boolean isTweetNew(TweetDTO tweet, List<String> existingTweetIds) {
        return !existingTweetIds.contains(tweet.getTweetId());
    }

    private Tweet saveNotExistingTweet(TweetDTO tweetDTO) {
        return tweetRepository.save(getEntity(tweetDTO));
    }

    public List<TweetDTO> getTweetsByUsernameFromDb(String username) {
        validateUsername(username, validUsers);
        return tweetRepository.findAllByAuthorIdOrderByCreatedAtDesc(validUsers.get(username)).stream()
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