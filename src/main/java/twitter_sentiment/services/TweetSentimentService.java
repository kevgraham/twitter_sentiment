package twitter_sentiment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import twitter_sentiment.exceptions.DatabaseException;
import twitter_sentiment.exceptions.TwitterException;
import twitter_sentiment.exceptions.WatsonException;
import twitter_sentiment.mappers.TweetSentimentMapper;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.model.watson.ToneScore;
import twitter_sentiment.model.twitter.Tweet;
import twitter_sentiment.utilities.CSVUtil;

import java.util.ArrayList;

@Service
public class TweetSentimentService {

    @Autowired
    TweetSentimentMapper tweetSentimentMapper;

    @Autowired
    WatsonService sentimentService;

    @Autowired
    TwitterService twitterService;

    /**
     * Analyzes the most recent tweets of a given user
     * @param user twitter handle
     * @return an ArrayList of watson data on tweets
     */
    public ArrayList<TweetSentiment> analyzeTweets(String user, Integer count) throws TwitterException, WatsonException {

        // build ArrayList of TweetSentiment
        ArrayList<TweetSentiment> output = new ArrayList<>();

        // pull recent tweets
        Tweet[] tweets = twitterService.recentTweets(user, count);

        // iterate through recent tweets
        for (int i = 0; i < tweets.length; i++) {

            // check if already in database
            TweetSentiment temp = tweetSentimentMapper.findSpecificTweet(tweets[i].getFull_text());

            if (temp == null) {

                // get ToneScores for specific tweet
                System.out.print("getting from api");
                ToneScore[] tones = sentimentService.analyze(tweets[i].getFull_text()).getDocument_tone().getTones();

                // map TweetSentiment Object
                temp = mapTweetSentiment(tweets[i], tones);

                // add TweetSentiment to Tweet table
                System.out.println("...adding to database");
                tweetSentimentMapper.insertTweet(temp);

                // add User to User table
                if (tweetSentimentMapper.findUserByScreenName(tweets[i].getUser().getScreen_name()) == null) {
                    tweetSentimentMapper.insertUser(tweets[i].getUser());
                }

                // add lookup entry to UserTweet table
                int user_id = tweetSentimentMapper.findIdByScreenName(tweets[i].getUser().getScreen_name());
                int tweet_id = tweetSentimentMapper.findIdByTweet(tweets[i].getFull_text());
                tweetSentimentMapper.insertUserTweet(user_id, tweet_id);


            } else {
                System.out.println("getting from database");
            }

            // add TweetSentiment to result ArrayList
            output.add(temp);

        }

        return output;
    }

    /**
     * Analyzes the sentiment of Congress
     * @return an ArrayList of tweet sentiment data
     */
    public ArrayList<TweetSentiment> analyzeCongress() throws TwitterException, WatsonException {

        // load twitter handles from csv
        ArrayList<String> twitterHandles = CSVUtil.loadTwitterHandles();

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
    public ArrayList<TweetSentiment> findTweetsByTone(String tone) throws DatabaseException {
        try {
            // pull tweets from database
            ArrayList<TweetSentiment> data = tweetSentimentMapper.findTweetsByTone(tone);

            // check if no data retrieved
            try {
                data.get(0);
            }
            // catch if no tweets found
            catch (IndexOutOfBoundsException ex) {
                throw new DatabaseException("No Tweets Found");
            }

            return data;
        } catch (BadSqlGrammarException ex) {
            throw new DatabaseException("Bad Query");
        }
    }

    /**
     * Pulls tweet sentiment data of a given user
     * @param user
     * @return
     */
    public ArrayList<TweetSentiment> findTweetsByUser(String user) throws DatabaseException {
        try {
            // pull tweets from database
            ArrayList<TweetSentiment> data = tweetSentimentMapper.findTweetsByUser(user);

            // check if no data retrieved
            try {
                data.get(0);
            }
            // catch if no tweets found
            catch (IndexOutOfBoundsException ex) {
                throw new DatabaseException("No Tweets Found");
            }

            return data;
        }
        // catch bad query
        catch (BadSqlGrammarException ex) {
            throw new DatabaseException("Bad Query");
        }
    }

    /**
     * Maps a Tweet and its ToneScores to a TweetSentiment Object
     * @param tweet Tweet Object
     * @param tones Array of ToneScores
     * @return
     */
    public TweetSentiment mapTweetSentiment(Tweet tweet, ToneScore[] tones) {
        TweetSentiment output = new TweetSentiment();

        output.setTweet(tweet.getFull_text());

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
