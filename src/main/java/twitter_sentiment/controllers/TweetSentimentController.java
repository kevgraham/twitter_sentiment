package twitter_sentiment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.services.TweetSentimentService;

import java.util.ArrayList;

@RestController
public class TweetSentimentController {

    @Autowired
    TweetSentimentService tweetSentimentService;

    /**
     * Analyzes the most recent tweets of a given user
     * @param user twitter handle
     * @return an ArrayList of sentiment data on tweets
     */
    @RequestMapping(method = RequestMethod.GET, value = "/analyze")
    public ArrayList<TweetSentiment> analyzeTweets(@RequestParam(value="user") String user,
                                                   @RequestParam(value="count", required=false) Integer count) {

        return tweetSentimentService.analyzeTweets(user, count);
    }

    /**
     * Pull tweets with the given tone from the database
     * @param tone to query
     * @return an ArrayList of sentiment data on tweets
     */
    @RequestMapping(method = RequestMethod.GET, value = "/retrieve")
    public ArrayList<TweetSentiment> findTweetsByTone(@RequestParam(value="tone") String tone) {
        return tweetSentimentService.findTweetsByTone(tone);
    }
}
