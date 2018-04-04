package twitter_sentiment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter_sentiment.model.internal.APIKey;
import twitter_sentiment.services.APIService;

@RestController
public class APIController {

    @Autowired
    APIService apiService;

    /**
     * Requests a new API KEY to be generated
     * @param owner String name
     * @return an APIKey Object with validated credentials
     */
    @PostMapping("/apikey")
    public APIKey createKey(@RequestParam(value="owner") String owner) {
        return apiService.insertKey(owner);
    }

}
