package twitter_sentiment.services;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter_sentiment.exceptions.DatabaseException;
import twitter_sentiment.exceptions.TwitterException;
import twitter_sentiment.exceptions.WatsonException;
import twitter_sentiment.mappers.TweetSentimentMapper;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.model.watson.ToneResponse;
import twitter_sentiment.model.watson.ToneScore;
import twitter_sentiment.model.twitter.Tweet;
import twitter_sentiment.utilities.AuthUtil;
import twitter_sentiment.utilities.CSVUtil;

import java.util.ArrayList;
import java.util.concurrent.*;

@Service
public class TweetSentimentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    TweetSentimentMapper tweetSentimentMapper;

    @Autowired
    TwitterService twitterService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    CSVUtil csvUtil;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * Analyzes the most recent tweets of a given user
     * @param user twitter handle
     * @param count number of tweets
     * @return an ArrayList of sentiment data
     */
    public ArrayList<TweetSentiment> analyzeTweets(String user, Integer count) throws TwitterException, WatsonException {

        // ArrayList of TweetSentiments to return
        ArrayList<TweetSentiment> output = new ArrayList<>();

        // ArrayList of Futures to hold task results
        ArrayList<Future<Pair<Tweet, ToneResponse>>> futures = new ArrayList<>();

        // pull recent tweets
        Tweet[] tweets = twitterService.recentTweets(user, count);

        // iterate through recent tweets
        for (int i = 0; i < tweets.length; i++) {

            // check if already in database
            TweetSentiment temp = tweetSentimentMapper.findSpecificTweet(tweets[i].getFull_text());

            // start Watson API tasks for tweets not in database
            if (temp == null) {
                logger.info("getting from api with new thread...");

                // create api call task
                WatsonTask task = new WatsonTask(tweets[i], restTemplate, authUtil);

                // submit to threadpool
                Future<Pair<Tweet, ToneResponse>> future = threadPoolTaskExecutor.submit(task);

                // add task to future list
                futures.add(future);
             }
            // add TweetSentiment to result ArrayList from database (skip api call)
            else {
                logger.info("getting from database...");
                output.add(temp);
            }
        }

        // iterate through results of tasks
        for (int i = 0; i < futures.size(); i++) {
            try {
                // get task when complete
                Pair<Tweet, ToneResponse> response = futures.get(i).get();
                logger.info("retrieving thread result...");

                // parse response
                Tweet tweet = response.getKey();
                ToneResponse toneResponse = response.getValue();
                ToneScore[] tones = toneResponse.getDocument_tone().getTones();

                // map TweetSentiment Object
                TweetSentiment temp = mapTweetSentiment(tweet.getFull_text(), tones);

                // add to output ArrayList
                output.add(temp);

                // add TweetSentiment, User, UserTweet to relational database tables
                saveTweetSentiment(temp, tweet);
            }
            // catch threading exceptions
            catch (InterruptedException | ExecutionException ex) {
                throw new WatsonException("Watson API Error: ", HttpStatus.BAD_REQUEST);
            }
        }

        return output;
    }

    /**
     * Stores relational tweet sentiment and user data in database
     * @param tweetSentiment
     * @param tweet
     */
    public void saveTweetSentiment(TweetSentiment tweetSentiment, Tweet tweet) {
        // add TweetSentiment to Tweet table
        logger.info("adding to database...");
        tweetSentimentMapper.insertTweet(tweetSentiment);

        // add User to User table
        if (tweetSentimentMapper.findUserByScreenName(tweet.getUser().getScreen_name()) == null) {
            tweetSentimentMapper.insertUser(tweet.getUser());
        }

        // add lookup entry to UserTweet table
        int user_id = tweetSentimentMapper.findIdByScreenName(tweet.getUser().getScreen_name());
        int tweet_id = tweetSentimentMapper.findIdByTweet(tweet.getFull_text());
        tweetSentimentMapper.insertUserTweet(user_id, tweet_id);
    }

    /**
     * Analyzes the sentiment of Congress
     * @return an ArrayList of tweet sentiment data
     */
    public ArrayList<TweetSentiment> analyzeCongress() throws TwitterException, WatsonException {

        // get twitter handles from csv
        ArrayList<String> twitterHandles = csvUtil.getTwitterHandles();

        // analyze twitter for each twitter handle
        ArrayList<TweetSentiment> result = new ArrayList<>();

        for (String twitterHandle : twitterHandles) {
            ArrayList<TweetSentiment> tweetData = analyzeTweets(twitterHandle, 5);
            for (TweetSentiment data : tweetData) {
                result.add(data);
            }
        }

        return result;
    }

    /**
     * Pulls tweet sentiment data of a given tone
     * @param tone to query
     * @return an ArrayList of tweet sentiment data
     */
    @Cacheable(value="tweets")
    public ArrayList<TweetSentiment> findTweetsByTone(String tone) throws DatabaseException {
        try {
            // pull tweets from database
            logger.info("pulling tweets from database...");
            ArrayList<TweetSentiment> data = tweetSentimentMapper.findTweetsByTone(tone);

            // check if no data retrieved
            try {
                data.get(0);
            }
            // catch no tweets found
            catch (IndexOutOfBoundsException ex) {
                logger.error("no tweets found");
                throw new DatabaseException("No Tweets Found");
            }

            return data;
        }
        // catch bad sql query
        catch (BadSqlGrammarException ex) {
            logger.error("bad query");
            throw new DatabaseException("Bad Query");
        }
    }

    /**
     * Pulls tweet sentiment data of a given user
     * @param user
     * @return
     */
    @Cacheable(value="tweets")
    public ArrayList<TweetSentiment> findTweetsByUser(String user) throws DatabaseException {
        try {
            // pull tweets from database
            logger.info("pulling tweets from database");
            ArrayList<TweetSentiment> data = tweetSentimentMapper.findTweetsByUser(user);

            // check if no data retrieved
            try {
                data.get(0);
            }
            // catch no tweets found
            catch (IndexOutOfBoundsException ex) {
                logger.error("no tweets found");
                throw new DatabaseException("No Tweets Found");
            }

            return data;
        }
        // catch bad sql query
        catch (BadSqlGrammarException ex) {
            logger.error("bad query");
            throw new DatabaseException("Bad Query");
        }
    }

    /**
     * Maps a Tweet and its ToneScores to a TweetSentiment Object
     * @param tweet Tweet Object
     * @param tones Array of ToneScores
     * @return
     */
    public TweetSentiment mapTweetSentiment(String tweet, ToneScore[] tones) {
        TweetSentiment output = new TweetSentiment();

        output.setTweet(tweet);

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
