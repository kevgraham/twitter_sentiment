package twitter_sentiment.config;

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

    @Autowired
    private APIService apiService;

    @Autowired
    RequestsMapper requestsMapper;

    @Autowired
    APIMapper apiMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String key = request.getParameter("apikey");

        // ensure api key is valid
        if (!apiService.validateKey(key)) {
            throw new APIKeyException(key);
        }

        // ensure api key is under limit
        if (!apiService.checkThrottling(key)) {
            throw new RateLimitException("Rate Limit Exceeded", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
        }

        System.out.println("\nAuthenticated");
        System.out.println("API KEY: " + key);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {

        System.out.println("\nRequest Handled");

        // build request POJO
        int apikey_id = apiMapper.findIdByKey(request.getParameter("apikey")).getId();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String endpoint = request.getRequestURI();
        Request currentRequest = new Request(0, apikey_id, timestamp, endpoint);

        // insert into db
        requestsMapper.insertRequest(currentRequest);

        System.out.println("TIME: " + timestamp + "\n");

    }
}
