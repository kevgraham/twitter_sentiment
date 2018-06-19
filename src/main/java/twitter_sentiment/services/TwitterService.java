package twitter_sentiment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthUtil authUtil;

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
        StringBuilder temp = new StringBuilder();
        temp.append(baseURL);
        temp.append("?tweet_mode=extended");
        temp.append("&screen_name=" + username);
        temp.append("&count=" + count);
        String fullQuery = temp.toString();

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
            logger.error("bad twitter api request");
            throw new TwitterException(ex.getMessage(), ex.getStatusCode());
        }

    }

}
