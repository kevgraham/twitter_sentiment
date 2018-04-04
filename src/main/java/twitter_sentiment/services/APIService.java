package twitter_sentiment.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter_sentiment.mappers.APIMapper;
import twitter_sentiment.model.internal.APIKey;

@Service
public class APIService {

    @Autowired
    APIMapper apiMapper;

    /**
     * Creates an api key for the given owner and adds to database
     * @param owner String name
     * @return an APIKey Object of the generated key
     */
    public APIKey insertKey(String owner) {

        // check if owner taken
        if (apiMapper.findKeyByOwner(owner) != null) {
            // TODO make exception
            return null;
        }

        // generate key and make sure its unique
        String key = generateKey(owner);

        // create apikey Object
        APIKey temp = new APIKey();
        temp.setOwner(owner);
        temp.setKey(key);
        temp.setActive(1);

        // add apikey to database
        apiMapper.insertApiKey(temp);

        // return successfully generated key
        return temp;
    }

    /**
     * Determines if the given api key is valid and active
     * @param key to validate
     * @return true if valid, false if invalid
     */
    public boolean validateKey(String key) {
        if (apiMapper.findActiveKey(key) != null) {
            return true;
        }
        return false;
    }

    /**
     * Creates an encrypted API Key
     * @return String key
     */
    public String generateKey(String owner) {

        // get bytes of current time
        byte[] timestamp = String.valueOf(System.currentTimeMillis() / 1000).getBytes();

        // get bytes of owner
        byte[] user = String.valueOf(System.currentTimeMillis()).getBytes();

        // encode both into key
        String key = Base64.encodeBase64String(timestamp) +
                Base64.encodeBase64String(user);

        return key;
    }
}
