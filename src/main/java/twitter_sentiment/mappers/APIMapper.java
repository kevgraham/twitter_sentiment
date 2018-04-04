package twitter_sentiment.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import twitter_sentiment.model.internal.APIKey;

@Mapper
public interface APIMapper {

    @Insert("INSERT INTO `TwitterSentiment`.`ApiKeys` (`key`, `owner`, `active`) VALUES (#{key}, #{owner}, #{active}); ")
    public int insertApiKey(APIKey apiKey);

    @Select("SELECT * FROM `TwitterSentiment`.`ApiKeys` WHERE `owner` = #{owner} ")
    public APIKey findKeyByOwner(String owner);

    @Select("SELECT * FROM `TwitterSentiment`.`ApiKeys` WHERE `key` = #{key} AND `active` = 1")
    public APIKey findActiveKey(String key);

}
