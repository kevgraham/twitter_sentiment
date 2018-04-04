package twitter_sentiment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import twitter_sentiment.exceptions.WatsonException;
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
    public ToneResponse analyze(String query) throws WatsonException {

        // clean hashtag issues
        try {
            query = URLEncoder.encode(query, "UTF-8");
        }
        // throw exception
        catch (UnsupportedEncodingException ex) {
            throw new WatsonException(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }

        // build url
        String fquery = "https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone?version=2017-09-21&text="+ query;

        // create new header with authorization String
        HttpHeaders headers = authUtil.createWatsonHeader();

        // make API call
        try {
            ResponseEntity<ToneResponse> fullResponse = restTemplate.exchange(fquery, HttpMethod.GET, new HttpEntity(headers), ToneResponse.class);
            ToneResponse response = fullResponse.getBody();
            return response;
        }
        // catch bad API call
        catch (HttpClientErrorException ex) {
            throw new WatsonException(ex.getMessage(),ex.getStatusCode());
        }


    }
}
