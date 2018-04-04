package twitter_sentiment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import twitter_sentiment.model.internal.ApiKey;
import twitter_sentiment.services.ApiService;

@RestController
public class ApiController {

    @Autowired
    ApiService apiService;

    /**
     * Requests a new API KEY to be generated
     * @param owner String name
     * @return an ApiKey Object with validated credentials
     */
    @PostMapping("/apikey")
    public ApiKey createKey(@RequestParam(value="owner") String owner) {
        return apiService.createKey(owner);
    }

}
