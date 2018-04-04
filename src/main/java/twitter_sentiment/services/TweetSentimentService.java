package twitter_sentiment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter_sentiment.mappers.TweetSentimentMapper;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.model.sentiment.ToneScore;
import twitter_sentiment.model.twitter.Tweet;
import twitter_sentiment.model.twitter.User;
import twitter_sentiment.utilities.AuthUtil;
import twitter_sentiment.utilities.TwitterHandleCSV;

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
     * @return an ArrayList of sentiment data on tweets
     */
    public ArrayList<TweetSentiment> analyzeTweets(String user, Integer count) {

        // build ArrayList of TweetSentiment
        ArrayList<TweetSentiment> output = new ArrayList<>();

        // pull recent tweets
        Tweet[] tweets = twitterService.recentTweets(user, count);

        // iterate through recent tweets
        for (int i = 0; i < tweets.length; i++) {

            // check if already in database
            TweetSentiment temp = findSpecificTweet(tweets[i].getFull_text());
            if (temp == null) {

                // get ToneScores for specific tweet
                System.out.print("getting from api");
                ToneScore[] tones = sentimentService.analyze(tweets[i].getFull_text()).getDocument_tone().getTones();

                // map TweetSentiment Object
                temp = mapTweetSentiment(tweets[i], tones);

                // add TweetSentiment to Tweet Database Table
                System.out.println("...adding to database");
                tweetSentimentMapper.insertTweet(temp);

                // add user to User Database Table
                if (findUserByScreenName(tweets[i].getUser().getScreen_name()) == null) {
                    tweetSentimentMapper.insertUser(tweets[i].getUser());
                }

                // add lookup UserTweet Database Table
                int user_id = findIdByUserName(tweets[i].getUser().getScreen_name());
                int tweet_id = findIdByTweet(tweets[i].getFull_text());
                insertUserTweet(user_id, tweet_id);


            } else {
                System.out.println("getting from database");
            }

            // add TweetSentiment to result ArrayList
            output.add(temp);

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

    /**
     * Analyzes the sentiment of Congress
     * @return an ArrayList of tweet sentiment data
     */
    public ArrayList<TweetSentiment> analyzeCongress() {

        // load twitter handles from csv
        ArrayList<String> twitterHandles = TwitterHandleCSV.loadTwitterHandles();

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
    public ArrayList<TweetSentiment> findTweetsByTone(String tone) {
        return tweetSentimentMapper.findTweets(tone);
    }

    /**
     * Pulls tweet sentiment data of a given user
     * @param user
     * @return
     */
    public ArrayList<TweetSentiment> findTweetsByUser(String user) {
        return tweetSentimentMapper.findTweetsByUser(user);
    }

    /**
     * Pulls tweet that matches text
     * @param text to query
     * @return a TweetSentiment Object
     */
    public TweetSentiment findSpecificTweet(String text) {
        ArrayList<TweetSentiment> temp = tweetSentimentMapper.findSpecificTweet(text);

        if (temp.size() > 0) {
            return temp.get(0);
        } else {
            return null;
        }

    }

    /**
     * Pulls user that matches a twitter handle
     * @param screen_name to query
     * @return a User Object
     */
    public User findUserByScreenName(String screen_name) {
        ArrayList<User> temp = tweetSentimentMapper.findUserByScreenName(screen_name);

        if (temp.size() > 0) {
            return temp.get(0);
        } else {
            return null;
        }
    }

    /**
     * Pulls the id that matches a twitter handle
     * @param screen_name to query
     * @return int id
     */
    public int findIdByUserName(String screen_name) {
        return tweetSentimentMapper.findIdByScreenName(screen_name);
    }

    /**
     * Pulls the id that matches a tweet
     * @param text to query
     * @return int id
     */
    public int findIdByTweet(String text) {
        return tweetSentimentMapper.findIdByTweet(text);
    }

    /**
     * Inserts a User Tweet entry in the Lookup Table
     * @param user_id
     * @param tweet_id
     * @return
     */
    public int insertUserTweet(int user_id, int tweet_id) {
        return tweetSentimentMapper.insertUserTweet(user_id, tweet_id);
    }
}
