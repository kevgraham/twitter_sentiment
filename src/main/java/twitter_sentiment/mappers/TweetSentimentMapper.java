package twitter_sentiment.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import twitter_sentiment.model.internal.TweetSentiment;

import java.util.ArrayList;

@Mapper
public interface TweetSentimentMapper {
    final String INSERT_TWEET = "INSERT INTO `TwitterSentiment`.`TweetSentiment` " +
                                "(`tweet`, `anger`, `fear`, `joy`, `sadness`, `analytical`, `confident`, `tentative`) " +
                                "VALUES (#{tweet}, #{anger}, #{fear}, #{joy}, #{sadness}, #{analytical}, #{confident}, #{tentative});";

    final String FIND_TWEETS = "SELECT * FROM `TwitterSentiment`.`TweetSentiment` WHERE ${tone} > .75";

    final String DELETE_TWEET = "";


    @Insert(INSERT_TWEET)
    public int insertTweet(TweetSentiment tweetSentiment);

    @Select(FIND_TWEETS)
    public ArrayList<TweetSentiment> findTweets(@Param("tone") String tone);

}
