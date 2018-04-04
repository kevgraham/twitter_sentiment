package twitter_sentiment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import twitter_sentiment.exceptions.TwitterException;
import twitter_sentiment.model.twitter.Tweet;
import twitter_sentiment.utilities.AuthUtil;

@Service
public class TwitterService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AuthUtil authUtil;

    /**
     * Gets an array of recent tweets for the given username
     * @param username twitter handle
     * @return array of tweets
     */
    public Tweet[] recentTweets(String username, Integer count) throws TwitterException {

        // set default count
        if (count == null) {
            count = 10;
        }

        // build URL
        String baseURL = "https://api.twitter.com/1.1/statuses/user_timeline.json";
        String fullQuery = baseURL + "?tweet_mode=extended" + "&screen_name=" + username + "&count=" + count;

        // create new header with authorization String
        HttpHeaders headers = authUtil.createTwitterHeader(baseURL, username, count);

        // make API call
        try {
            ResponseEntity<Tweet[]> fullResponse = restTemplate.exchange(fullQuery, HttpMethod.GET, new HttpEntity(headers), Tweet[].class);
            Tweet[] response = fullResponse.getBody();
            return response;
        }
        // catch bad API call
        catch (HttpClientErrorException ex) {
            throw new TwitterException(ex.getMessage(), ex.getStatusCode());
        }

    }

}
