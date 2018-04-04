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

    @Insert("INSERT INTO `TwitterSentiment`.`Tweets` " +
            "(`tweet`, `anger`, `fear`, `joy`, `sadness`, `analytical`, `confident`, `tentative`) " +
            "VALUES (#{tweet}, #{anger}, #{fear}, #{joy}, #{sadness}, #{analytical}, #{confident}, #{tentative})")
    public int insertTweet(TweetSentiment tweetSentiment);

    @Select("SELECT * FROM `TwitterSentiment`.`Tweets` WHERE ${tone} > .75")
    public ArrayList<TweetSentiment> findTweetsByTone(@Param("tone") String tone);

    @Select("SELECT * FROM `TwitterSentiment`.`Tweets` WHERE tweet = #{text} LIMIT 1")
    public TweetSentiment findSpecificTweet(String text);

    @Select("SELECT `id` FROM `TwitterSentiment`.`Tweets` WHERE tweet = #{text}")
    public int findIdByTweet(String text);


    /**
     * User Table
     */

    @Insert("INSERT INTO `TwitterSentiment`.`Users` " +
            "(`screen_name`, `name`) " +
            "VALUES (#{screen_name}, #{name})")
    public int insertUser(User user);

    @Select("SELECT * FROM TwitterSentiment.Users WHERE `screen_name` = #{screen_name} LIMIT 1")
    public User findUserByScreenName(String screen_name);

    @Select("SELECT `id` FROM TwitterSentiment.Users WHERE `screen_name` = #{screen_name}")
    public int findIdByScreenName(String screen_name);


    /**
     * TweetsUser Table
     */

    @Insert("INSERT INTO `TwitterSentiment`.`TweetsUsers` (`user_id`,`tweet_id`) VALUES (${user_id},${tweet_id})")
    public int insertUserTweet(@Param("user_id") int user_id, @Param("tweet_id") int tweet_id);

    @Select("SELECT * FROM `TwitterSentiment`.`Users` u JOIN `TwitterSentiment`.`TweetsUsers` tu " +
            "ON u.id = tu.user_id JOIN `TwitterSentiment`.`Tweets` t ON tu.tweet_id = t.id " +
            "WHERE `screen_name` = \"${user}\"")
    ArrayList<TweetSentiment> findTweetsByUser(@Param("user") String user);

}
