package twitter_sentiment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import twitter_sentiment.exceptions.APIKeyException;
import twitter_sentiment.services.APIService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    APIService apiService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // ensure api key is valid
        String key = request.getParameter("apikey");

        if (!apiService.validateKey(key)) {
            throw new APIKeyException(key);
        }

        // TODO check throttling

        System.out.println("\nAuthenticated");
        System.out.println("API KEY: " + key);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
        System.out.println("\nRequest Handled");

        String timestamp = String.valueOf(System.currentTimeMillis());

        System.out.println("TIME: " + timestamp);

        // TODO log timestamp in DB
    }
}
