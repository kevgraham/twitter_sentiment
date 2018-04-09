package twitter_sentiment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter_sentiment.exceptions.DatabaseException;
import twitter_sentiment.exceptions.TwitterException;
import twitter_sentiment.exceptions.WatsonException;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.services.TweetSentimentService;

import java.util.ArrayList;

@RestController
@CrossOrigin
public class TweetSentimentController {

    @Autowired
    TweetSentimentService tweetSentimentService;

    /**
     * Analyzes the most recent tweets of a given user
     * @param user twitter handle
     * @return an ArrayList of sentiment data on tweets
     */
    @GetMapping("/tweets")
    public ArrayList<TweetSentiment> analyzeTweets(@RequestParam(value="apikey") String key,
                                                   @RequestParam(value="user") String user,
                                                   @RequestParam(value="count", required=false) Integer count)
            throws TwitterException, WatsonException {

        return tweetSentimentService.analyzeTweets(user, count);
    }

    /**
     * Analyzes the overall sentiment of Congress
     * @return an ArrayList of sentiment data on all available Congress Member Tweets
     */
    @GetMapping("/congress")
    public ArrayList<TweetSentiment> analyzeCongress(@RequestParam(value="apikey") String key)
            throws TwitterException, WatsonException {

        return tweetSentimentService.analyzeCongress();
    }

    /**
     * Pull tweets with the given tone from the database
     * @param tone to query
     * @return an ArrayList of sentiment data on tweets
     */
    @RequestMapping(method = RequestMethod.GET, value = "/retrieve/tone/{tone}")
    public ArrayList<TweetSentiment> findTweetsByTone(@PathVariable String tone,
                                                      @RequestParam(value="apikey") String key) throws DatabaseException {

        return tweetSentimentService.findTweetsByTone(tone);
    }

    /**
     * Pull tweets for the given user from the database
     * @param user to query
     * @return an ArrayList of sentiment data on tweets
     */
    @RequestMapping(method = RequestMethod.GET, value = "/retrieve/user/{user}")
    public ArrayList<TweetSentiment> findTweetsByUser(@PathVariable String user,
                                                      @RequestParam(value="apikey") String key) throws DatabaseException {

        return tweetSentimentService.findTweetsByUser(user);
    }

}
