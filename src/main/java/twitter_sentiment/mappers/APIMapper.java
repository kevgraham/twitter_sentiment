package twitter_sentiment.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import twitter_sentiment.model.internal.ApiKey;

@Mapper
public interface ApiMapper {

    final String INSERT_APIKEY = "INSERT INTO `TwitterSentiment`.`ApiKeys` " +
            "(`key`, `owner`, `active`) " +
            "VALUES (#{key}, #{owner}, #{active}); ";
    @Insert(INSERT_APIKEY)
    public int insertApiKey(ApiKey apiKey);


    final String FIND_KEY_BY_OWNER = "SELECT * FROM `TwitterSentiment`.`ApiKeys` WHERE `owner` = #{owner} ";
    @Select(FIND_KEY_BY_OWNER)
    public ApiKey findKeyByOwner(String owner);

    final String FIND_KEY = "SELECT * FROM `TwitterSentiment`.`ApiKeys` WHERE `key` = #{key}";
    @Select(FIND_KEY)
    public ApiKey findKey(String key);
}
