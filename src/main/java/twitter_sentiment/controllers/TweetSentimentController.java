package twitter_sentiment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.services.TweetSentimentService;

import javax.websocket.server.PathParam;
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
    @RequestMapping(method = RequestMethod.GET, value = "/retrieve/tone/{tone}")
    public ArrayList<TweetSentiment> findTweetsByTone(@PathVariable String tone) {
        return tweetSentimentService.findTweetsByTone(tone);
    }

    /**
     * Pull tweets for the given user from the database
     * @param user to query
     * @return an ArrayList of sentiment data on tweets
     */
    @RequestMapping(method = RequestMethod.GET, value = "/retrieve/user/{user}")
    public ArrayList<TweetSentiment> findTweetsByUser(@PathVariable String user) {
        return tweetSentimentService.findTweetsByUser(user);
    }
}
