package twitter_sentiment.services;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import twitter_sentiment.exceptions.WatsonException;
import twitter_sentiment.model.twitter.Tweet;
import twitter_sentiment.model.watson.ToneResponse;
import twitter_sentiment.utilities.AuthUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Callable;


public class WatsonTask implements Callable<Pair<Tweet, ToneResponse>> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private RestTemplate restTemplate;
    private AuthUtil authUtil;
    private Tweet tweet;

    public WatsonTask(Tweet tweet, RestTemplate restTemplate, AuthUtil authUtil) {
        this.tweet = tweet;
        this.restTemplate = restTemplate;
        this.authUtil = authUtil;
    }

    @Override
    public Pair<Tweet, ToneResponse> call() throws WatsonException {
        // clean hashtag issues
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(tweet.getFull_text(), "UTF-8");
        }
        // throw exception
        catch (UnsupportedEncodingException ex) {
            logger.error("could not encode watson query");
            throw new WatsonException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // build url
        String fquery = "https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone?version=2017-09-21&text="+ encodedQuery;

        // create new header with authorization String
        HttpHeaders headers = authUtil.createWatsonHeader();

        // make API call
        try {
            ResponseEntity<ToneResponse> fullResponse = restTemplate.exchange(fquery, HttpMethod.GET, new HttpEntity(headers), ToneResponse.class);
            ToneResponse response = fullResponse.getBody();
            Pair<Tweet, ToneResponse> result = new Pair<>(tweet, response);
            return result;
        }
        // catch bad API call
        catch (HttpClientErrorException ex) {
            logger.error("bad watson api request");
            throw new WatsonException(ex.getMessage(),ex.getStatusCode());
        }
    }
}
