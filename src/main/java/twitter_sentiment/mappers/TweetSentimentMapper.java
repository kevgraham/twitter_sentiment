package twitter_sentiment.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import twitter_sentiment.model.internal.TweetSentiment;

@Mapper
public interface TweetSentimentMapper {
    final String INSERT_TWEET = "INSERT INTO `TweetSentiment`.`TweetSentiment` " +
                                "(`tweet`, `anger`, `fear`, `joy`, `sadness`, `analytical`, `confident`) " +
                                "VALUES #{tweet}, #{anger}, #{fear}, #{joy}, #{sadness}, #{analytical}, #{confident});";

    final String DELETE_TWEET = "";

    @Insert(INSERT_TWEET)
    public int insertTweet(TweetSentiment tweetSentiment);

}
