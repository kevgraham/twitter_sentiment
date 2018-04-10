package twitter_sentiment.exceptions;

public class APIKeyException extends Exception {

    private String key;
    private String message;

    public APIKeyException(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
