package twitter_sentiment.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter_sentiment.mappers.TweetSentimentMapper;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.model.sentiment.ToneResponse;
import twitter_sentiment.model.sentiment.ToneScore;
import twitter_sentiment.model.twitter.Tweet;
import twitter_sentiment.utilities.OAuthUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;

@Service
public class TweetSentimentService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TweetSentimentMapper tweetSentimentMapper;

    @Value("${watson.username}")
    private String username;

    @Value("${watson.password}")
    private String password;

    @Value("${twitter.consumerKey}")
    private String consumerKey;

    @Value("${twitter.accessToken}")
    private String accessToken;

    @Value("${twitter.consumerSecret}")
    private String consumerSecret;

    @Value("${twitter.accessSecret}")
    String accessSecret;

    /**
     * Gets the tones of the given query from Watson Tone Analyzer API
     * @param query text to be analyzed
     * @return tone of given text
     */
    public ToneResponse sentimentAnalysis(String query) {

        // build url
        String fquery = "https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone?version=2017-09-21&text="+query;

        // make API call
        ResponseEntity<ToneResponse> fullResponse = restTemplate.exchange
                (fquery, HttpMethod.GET, new HttpEntity<String>(createBasicAuth()), ToneResponse.class);

        // return response
        ToneResponse response = fullResponse.getBody();
        return response;
    }

    /**
     * Creates a Header with Basic Authorization for the Watson Tone API
     * @return
     */
    HttpHeaders createBasicAuth() {

        // create new header with authorization String
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    /**
     * Gets an array of recent tweets for the given username
     * @param username twitter handle
     * @return array of tweets
     */
    public Tweet[] recentTweets(String username) {

        // build URL
        String baseURL = "https://api.twitter.com/1.1/statuses/user_timeline.json";
        String fullQuery = baseURL + "?screen_name=" + username + "&count=5";

        // create new header with authorization String
        HttpEntity<String> entity = new HttpEntity<>(OAuthUtil.getHeader(baseURL, username, consumerKey, accessToken, consumerSecret, accessSecret));

        // make API call
        ResponseEntity<Tweet[]> fullResponse = restTemplate.exchange
                (fullQuery, HttpMethod.GET, entity, Tweet[].class);

        // return response
        Tweet[] response = fullResponse.getBody();
        return response;
    }

    /**
     * Analyzes the most recent tweets of a given user
     * @param user twitter handle
     * @return an ArrayList of sentiment data on tweets
     */
    public ArrayList<TweetSentiment> analyzeTweets(String user) {

        // pull recent tweets
        Tweet[] tweets = recentTweets(user);

        // build ArrayList of TweetSentiment
        ArrayList<TweetSentiment> output = new ArrayList<>();
        for (int i = 0; i < tweets.length; i++) {
            // get ToneScores for specific tweet
            ToneScore[] tones = sentimentAnalysis(tweets[i].getText()).getDocument_tone().getTones();

            // map TweetSentiment Object
            TweetSentiment temp = mapTweetSentiment(tweets[i], tones);

            // add TweetSentiment to result ArrayList
            output.add(temp);

            // add TweetSentiment to TweetSentiment Database Table
            tweetSentimentMapper.insertTweet(temp);
        }

        return output;
    }

    /**
     * Maps a Tweet and its ToneScores to a TweetSentiment Object
     * @param tweet Tweet Object
     * @param tones Array of ToneScores
     * @return
     */
    public TweetSentiment mapTweetSentiment(Tweet tweet, ToneScore[] tones) {
        TweetSentiment output = new TweetSentiment();

        output.setTweet(tweet.getText());

        for (ToneScore tone : tones) {
            String tone_id = tone.getTone_id();
            double score = tone.getScore();

            if (tone_id != null && score != -1) {
                switch (tone_id) {
                    case "anger":
                        output.setAnger(score);
                        break;
                    case "fear":
                        output.setFear(score);
                        break;
                    case "joy":
                        output.setJoy(score);
                        break;
                    case "sadness":
                        output.setSadness(score);
                        break;
                    case "analytical":
                        output.setAnalytical(score);
                        break;
                    case "confident":
                        output.setConfident(score);
                        break;
                    case "tentative":
                        output.setTentative(score);
                        break;
                }
            }
        }

        return output;
    }
}