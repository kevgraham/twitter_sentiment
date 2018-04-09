package twitter_sentiment.exceptions;

public class APIKeyException extends Exception {

    private String key;

    public APIKeyException(String key) {
        this.key = key;
    }

    public String getApikey() {
        return key;
    }

    // Ryan: you'll want to add a toString()

}
