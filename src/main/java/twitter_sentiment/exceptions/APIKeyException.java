package twitter_sentiment.exceptions;

public class APIKeyException extends Exception {

    public APIKeyException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
