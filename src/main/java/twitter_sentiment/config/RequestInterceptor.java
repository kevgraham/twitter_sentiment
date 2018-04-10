package twitter_sentiment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import twitter_sentiment.exceptions.APIKeyException;
import twitter_sentiment.exceptions.RateLimitException;
import twitter_sentiment.mappers.APIMapper;
import twitter_sentiment.mappers.RequestsMapper;
import twitter_sentiment.model.internal.Request;
import twitter_sentiment.services.APIService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private APIService apiService;

    @Autowired
    RequestsMapper requestsMapper;

    @Autowired
    APIMapper apiMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // skip api key validation if requesting new key
        if (request.getRequestURI().equals("/apikey")) {
            return true;
        }

        String key = request.getParameter("apikey");

        // ensure api key is valid
        if (!apiService.validateKey(key)) {
            logger.error("invalid api key");
            throw new APIKeyException(key, "Invalid API Key");
        }

        // ensure api key is under limit
        if (!apiService.checkThrottling(key)) {
            logger.error("rate limit exceeded");
            throw new RateLimitException("Rate Limit Exceeded", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
        }

        logger.info("authenticated api key - " + key);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {

        // build request POJO
        int apikey_id = apiMapper.findIdByKey(request.getParameter("apikey")).getId();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String endpoint = request.getRequestURI();
        Request currentRequest = new Request(0, apikey_id, timestamp, endpoint);

        // insert into db
        requestsMapper.insertRequest(currentRequest);

        logger.info("request handled - " + endpoint);

    }
}
