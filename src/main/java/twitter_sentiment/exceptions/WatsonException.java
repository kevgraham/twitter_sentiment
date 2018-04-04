package twitter_sentiment.exceptions;

import org.springframework.http.HttpStatus;

public class WatsonException extends Exception {

    private HttpStatus status;

    public WatsonException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
