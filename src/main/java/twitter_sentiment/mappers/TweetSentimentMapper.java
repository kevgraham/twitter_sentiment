package twitter_sentiment.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.model.twitter.User;

import java.util.ArrayList;

@Mapper
public interface TweetSentimentMapper {

    /**
     * Tweet Table
     */

    final String INSERT_TWEET = "INSERT INTO `TwitterSentiment`.`Tweets` " +
                                "(`tweet`, `anger`, `fear`, `joy`, `sadness`, `analytical`, `confident`, `tentative`) " +
                                "VALUES (#{tweet}, #{anger}, #{fear}, #{joy}, #{sadness}, #{analytical}, #{confident}, #{tentative}); ";
    @Insert(INSERT_TWEET)
    public int insertTweet(TweetSentiment tweetSentiment);


    final String FIND_TWEETS_BY_TONE = "SELECT * FROM `TwitterSentiment`.`Tweets` WHERE ${tone} > .75; ";
    @Select(FIND_TWEETS_BY_TONE)
    public ArrayList<TweetSentiment> findTweets(@Param("tone") String tone);


    final String FIND_SPECIFIC_TWEET = "SELECT * FROM `TwitterSentiment`.`Tweets` WHERE tweet = #{text}; ";
    @Select(FIND_SPECIFIC_TWEET)
    public ArrayList<TweetSentiment> findSpecificTweet(String text);


    final String FIND_ID_BY_TWEET = "SELECT `id` FROM `TwitterSentiment`.`Tweets` WHERE tweet = #{text}; ";
    @Select(FIND_ID_BY_TWEET)
    public int findIdByTweet(String text);


    /**
     * User Table
     */

    final String INSERT_USER = "INSERT INTO `TwitterSentiment`.`Users` " +
                               "(`screen_name`, `name`) " +
                               "VALUES (#{screen_name}, #{name});";
    @Insert(INSERT_USER)
    public int insertUser(User user);


    final String FIND_USER_BY_SCREENNAME = "SELECT * FROM TwitterSentiment.Users WHERE `screen_name` = #{screen_name}";
    @Select(FIND_USER_BY_SCREENNAME)
    public ArrayList<User> findUserByScreenName(String screen_name);


    final String FIND_ID_BY_SCREENNAME = "SELECT `id` FROM TwitterSentiment.Users WHERE `screen_name` = #{screen_name}";
    @Select(FIND_ID_BY_SCREENNAME)
    public int findIdByScreenName(String screen_name);


    /**
     * TweetsUser Table
     */

    final String INSERT_USER_TWEET = "INSERT INTO `TwitterSentiment`.`TweetsUsers` (`user_id`,`tweet_id`) VALUES (${user_id},${tweet_id});";
    @Insert(INSERT_USER_TWEET)
    public int insertUserTweet(@Param("user_id") int user_id, @Param("tweet_id") int tweet_id);


    final String FIND_TWEETS_BY_USER = "SELECT * FROM `TwitterSentiment`.`Users` u JOIN `TwitterSentiment`.`TweetsUsers` tu " +
                                       "ON u.id = tu.user_id JOIN `TwitterSentiment`.`Tweets` t ON tu.tweet_id = t.id " +
                                       "WHERE `screen_name` = \"${user}\";";
    @Select(FIND_TWEETS_BY_USER)
    ArrayList<TweetSentiment> findTweetsByUser(@Param("user") String user);

}
