package twitter_sentiment.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import twitter_sentiment.model.internal.Request;

import java.util.ArrayList;

@Mapper
public interface RequestsMapper {

    @Insert("INSERT INTO `TwitterSentiment`.`Requests` (`apikey_id`, `timestamp`, `endpoint`) VALUES (#{apikey_id}, #{timestamp}, #{endpoint});")
    public int insertRequest(Request request);

    @Select("SELECT * FROM `TwitterSentiment`.`Requests` WHERE `timestamp` > ${timestamp} AND `apikey_id` = ${apikey_id}")
    public ArrayList<Request> findRecentRequests(@Param("timestamp") String timestamp, @Param("apikey_id") int apikey_id);

}
