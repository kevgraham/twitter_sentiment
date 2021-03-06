package twitter_sentiment.exceptions;

import org.springframework.http.HttpStatus;

public class TwitterException extends Exception {

    private HttpStatus status;

    public TwitterException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
