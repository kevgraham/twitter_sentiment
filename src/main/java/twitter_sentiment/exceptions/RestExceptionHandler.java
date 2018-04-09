package twitter_sentiment.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import twitter_sentiment.model.internal.RootResponse;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Invalid API Key Exception
     * @param ex APIKeyException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=APIKeyException.class)
    protected @ResponseBody RootResponse invalidKey(APIKeyException ex) {
        return new RootResponse("Invalid API Key: " + ex.getApikey(), HttpStatus.UNAUTHORIZED, null);
    }

    /**
     * Rate Limited Exceeded Exception
     * @param ex RateLimitException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=RateLimitException.class)
    protected @ResponseBody RootResponse rateLimitExceeded(RateLimitException ex) {
        return new RootResponse("Rate Limit Exceeded", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, null);
    }

    /**
     * Twitter Exception
     * @param ex TwitterException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=TwitterException.class)
    protected @ResponseBody RootResponse twitterError(TwitterException ex) {
        return new RootResponse("Twitter API Error", HttpStatus.BAD_REQUEST, null);
    }

    /**
     * Watson Exception
     * @param ex WatsonException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=WatsonException.class)
    protected @ResponseBody RootResponse watsonError(WatsonException ex) {
        return new RootResponse("Watson API Error", HttpStatus.BAD_REQUEST, null);
    }

    /**
     * Database Exception
     * @param ex DatabaseException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=DatabaseException.class)
    protected @ResponseBody RootResponse databaseError(DatabaseException ex) {
        return new RootResponse("Database Error: " + ex.getMessage() , HttpStatus.BAD_REQUEST, null);
    }

    /**
     * Missing Parameter Exception
     * @param ex MissingServletRequestParameterException
     * @param headers
     * @param status
     * @param request
     * @return JSON Error Message Response
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RootResponse response = new RootResponse("Missing Parameter: " + ex.getParameterName(), HttpStatus.BAD_REQUEST, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Missing Endpoint Exception
     * @param ex NoHandlerFoundException
     * @param headers
     * @param status
     * @param request
     * @return JSON Error Message Response
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RootResponse response = new RootResponse("Endpoint Not Found: " + ex.getRequestURL(), HttpStatus.BAD_REQUEST, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
