package twitter_sentiment.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Invalid API Key Exception
     * @param ex APIKeyException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=APIKeyException.class)
    protected ResponseEntity<Object> invalidKey(APIKeyException ex) {
        String responseBody = "Invalid API Key: " + ex.getApikey();
        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }

    /**
     * Twitter Exception
     * @param ex TwitterException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=TwitterException.class)
    protected ResponseEntity<Object> twitterError(TwitterException ex) {
        String responseBody = "Twitter API Error: " + ex.getStatus();
        return new ResponseEntity<>(responseBody, ex.getStatus());
    }

    /**
     * Watson Exception
     * @param ex WatsonException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=WatsonException.class)
    protected ResponseEntity<Object> watsonError(WatsonException ex) {
        String responseBody = "Watson API Error: " + ex.getStatus();
        return new ResponseEntity<>(responseBody, ex.getStatus());
    }

    /**
     * Database Exception
     * @param ex DatabaseException
     * @return JSON Error Message Response
     */
    @ExceptionHandler(value=DatabaseException.class)
    protected ResponseEntity<Object> databaseError(DatabaseException ex) {
        String responseBody = "Database Error: " + ex.getMessage();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
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
        String responseBody = "Missing Parameter: " + ex.getParameterName();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
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
        String responseBody = "Endpoint not found: " + ex.getRequestURL();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

}
