package twitter_sentiment.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter_sentiment.mappers.ApiMapper;
import twitter_sentiment.model.internal.ApiKey;

@Service
public class ApiService {

    @Autowired
    ApiMapper apiMapper;

    /**
     * Creates an API KEY for the given owner and adds to database
     * @param owner String name
     * @return an ApiKey Object of the generated key
     */
    public ApiKey createKey(String owner) {

        // check if owner taken
        if (apiMapper.findKeyByOwner(owner) != null) {
            // TODO make exception
            return null;
        }

        // generate key and make sure its unique
        String key = generateKey(owner);

        // create apikey Object
        ApiKey temp = new ApiKey();
        temp.setOwner(owner);
        temp.setKey(key);
        temp.setActive(1);

        // add apikey to database
        apiMapper.insertApiKey(temp);

        // return successfully generated key
        return temp;
    }

    /**
     * Creates an apikey
     * @return String key
     */
    public String generateKey(String owner) {

        // get bytes of current time
        byte[] timestamp = String.valueOf(System.currentTimeMillis()).getBytes();

        // get bytes of owner
        byte[] user = String.valueOf(System.currentTimeMillis()).getBytes();

        // encode both into key
        String key = Base64.encodeBase64String(timestamp) +
                     Base64.encodeBase64String(user);

        return key;
    }

    /**
     * Determines if the given key is valid
     * @param key to validate
     * @return true if valid, false if invalid
     */
    public boolean validateKey(String key) {
        if (apiMapper.findKey(key) != null) {
            return true;
        }
        return false;
    }
}
