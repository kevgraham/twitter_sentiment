package twitter_sentiment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter_sentiment.model.watson.ToneResponse;
import twitter_sentiment.utilities.AuthUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class WatsonService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AuthUtil authUtil;

    /**
     * Gets the tones of the given query from Watson Tone Analyzer API
     * @param query text to be analyzed
     * @return tone of given text
     */
    public ToneResponse analyze(String query) {

        // clean hashtag issues
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // build url
        String fquery = "https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone?version=2017-09-21&text="+ query;

        // create new header with authorization String
        HttpHeaders headers = authUtil.createWatsonHeader();

        // make API call
        ResponseEntity<ToneResponse> fullResponse = restTemplate.exchange(fquery, HttpMethod.GET, new HttpEntity(headers), ToneResponse.class);

        // return response
        ToneResponse response = fullResponse.getBody();
        return response;
    }
}
