package twitter_sentiment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.model.sentiment.ToneResponse;
import twitter_sentiment.model.twitter.Tweet;
import twitter_sentiment.services.TweetSentimentService;

import java.util.ArrayList;

@RestController
public class TweetSentimentController {

    @Autowired
    TweetSentimentService tweetSentimentService;

    /**
     * Gets the sentiment of each sentence in a given text
     * @param query text
     * @return tone of the given text
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sentiment")
    public ToneResponse sentimentAnalysis(@RequestParam(value="q") String query) {
        return tweetSentimentService.sentimentAnalysis(query);
    }

    /**
     * Gets the most recent tweets of a given user
     * @param user twitter handle
     * @return an array of tweets
     */
    @RequestMapping(method = RequestMethod.GET, value = "/tweets")
    public Tweet[] getTweets(@RequestParam(value="user") String user) {
        return tweetSentimentService.recentTweets(user);
    }

    /**
     * Analyzes the most recent tweets of a given user
     * @param user twitter handle
     * @return an arraylist of sentiment data on tweets
     */
    @RequestMapping(method = RequestMethod.GET, value = "/analyze")
    public ArrayList<TweetSentiment> analyzeTweets(@RequestParam(value="user") String user) {
        return tweetSentimentService.analyzeTweets(user);
    }
}
