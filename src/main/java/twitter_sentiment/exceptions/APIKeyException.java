package twitter_sentiment.exceptions;

public class ApiKeyException extends Exception {

    public ApiKeyException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
