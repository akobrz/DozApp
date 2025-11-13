package com.pm.dozapp.service;

import com.pm.dozapp.dto.TweetDTO;
import com.pm.dozapp.dto.TweetsResponse;
import com.pm.dozapp.dto.UserDTO;
import com.pm.dozapp.dto.UserResponse;
import com.pm.dozapp.exception.ErrorsException;
import com.pm.dozapp.mapper.TweetMapper;
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

import static com.pm.dozapp.exception.ErrorType.TWEET_ALREADY_REGISTERED;
import static com.pm.dozapp.exception.ErrorType.USER_NOT_FOUND;
import static com.pm.dozapp.mapper.TweetMapper.getEntity;
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

    private Map<String, String> validUsers = new HashMap<>();

    public TweetService(RestTemplate restTemplate, TweetRepository tweetRepository) {
        this.restTemplate = restTemplate;
        this.tweetRepository = tweetRepository;
        this.validUsers = prepareValidUsersMap();
    }

    public UserDTO getUserByUsername(String username) {
        String userUrl = "https://api.twitter.com/2/users/by/username/" + username + "?user.fields=username,name";

        HttpEntity<String> entity = prepareHttpEntity();

        try {
            ResponseEntity<UserResponse> response = restTemplate.exchange(userUrl, GET, entity, UserResponse.class);
            log.info("Response for /user/{username}: status={}, body={}", response.getStatusCode(), response.getBody());
            if (response.getBody() != null && null != response.getBody().getData()) {
                return response.getBody().getData();
            }
            throw new ErrorsException(USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error for getting user: {}", e.getMessage());
            throw e;
        }
    }

    public List<TweetDTO> getUserTweets(String username, int count) {
        validateUsername(username, validUsers);
        return getTweetsByUserId(validUsers.get(username), count);
    }

    private List<TweetDTO> getTweetsByUserId(String userId, int count) {
        String tweetsUrl = "https://api.twitter.com/2/users/" + userId + "/tweets" +
                "?max_results=" + max(count, 5) +
                "&tweet.fields=created_at,author_id,text";

        HttpEntity<String> entity = prepareHttpEntity();

        try {
            ResponseEntity<TweetsResponse> response = restTemplate.exchange(tweetsUrl, GET, entity, TweetsResponse.class);
            log.info("Response for /tweets/{username}: status={}, body={}", response.getStatusCode(), response.getBody());
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

    private HttpEntity<String> prepareHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        return new HttpEntity<>(headers);
    }

    private void saveAllNotExistingTweets(ResponseEntity<TweetsResponse> response) {
        response.getBody().getData().forEach(this::saveNotExistingTweet);
    }

    private void saveNotExistingTweet(TweetDTO tweetDTO) {
        if (!tweetRepository.existsByTweetId(tweetDTO.getTweetId())) {
            tweetRepository.save(getEntity(tweetDTO));
        }
    }

    public List<TweetDTO> getTweetsByUsernameFromDb(String username) {
        validateUsername(username, validUsers);
        return tweetRepository.findAllByAuthorId(validUsers.get(username)).stream()
                .map(TweetMapper::getDTO).toList();
    }

    public TweetDTO createTweetInDb(TweetDTO tweetDTO) {
        validateUsersIds(tweetDTO, validUsers);
        validateDuplicate(tweetDTO);
        return TweetMapper.getDTO(tweetRepository.save(getEntity(tweetDTO)));
    }

    private void validateDuplicate(TweetDTO tweetDTO) {
        if (tweetRepository.existsByTweetId(tweetDTO.getTweetId())) {
            throw new ErrorsException(TWEET_ALREADY_REGISTERED);
        }
    }

    private static void validateUsersIds(TweetDTO tweetDTO, Map<String, String> users) {
        if (!users.containsValue(tweetDTO.getAuthorId())) {
            throw new ErrorsException(USER_NOT_FOUND);
        }
    }

    private static void validateUsername(String username, Map<String, String> users) {
        if (!users.containsKey(username.toLowerCase())) {
            throw new ErrorsException(USER_NOT_FOUND);
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