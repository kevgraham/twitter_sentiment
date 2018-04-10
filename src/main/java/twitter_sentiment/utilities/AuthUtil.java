package twitter_sentiment.utilities;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AuthUtil {

    private ArrayList<Parameter> parameters = new ArrayList<>();

    private String method;
    private String baseURL;

    private String oAuthNonce;
    private String oAuthSignatureMethod;
    private String oAuthTimeStamp;
    private String oAuthVersion;

    private String oAuthSignature;
    private String authorization;

    @Value("${watson.username}")
    private String username;

    @Value("${watson.password}")
    private String password;

    @Value("${twitter.consumerKey}")
    private String oAuthConsumerKey;

    @Value("${twitter.accessToken}")
    private String oAuthToken;

    @Value("${twitter.consumerSecret}")
    private String oAuthConsumerSecret;

    @Value("${twitter.accessSecret}")
    private String oAuthTokenSecret;

    /**
     * Creates a Header with Basic Authorization for the Watson Tone API
     * @return
     */
    public HttpHeaders createWatsonHeader() {

        HttpHeaders headers = new HttpHeaders();

        String auth = username + ":" + password;

        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")) );

        String authHeader = "Basic " + new String( encodedAuth );

        // create headers
        headers.set( "Authorization", authHeader );
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Creates a Header with OAuth1 Authorization for the Twitter API
     * @param baseURL
     * @param user
     * @return
     */
    public HttpHeaders createTwitterHeader(String baseURL, String user, int count) {

        // set default values
        this.method = "GET";
        this.baseURL = baseURL;
        this.oAuthNonce = String.valueOf(new SecureRandom().nextLong());
        this.oAuthSignatureMethod = "HMAC-SHA1";
        this.oAuthTimeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        this.oAuthVersion = "1.0";

        // build parameter list
        this.parameters = buildParams(user, count);

        // build signature String
        this.oAuthSignature = getSignature();

        // build authorization String
        this.authorization = getAuthorization();

        // create header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Builds an ArrayList of Parameters based on OAuth Specification
     * @param user query parameter
     * @param count query parameter
     * @return ArrayList of encoded, sorted parameters
     */
    private ArrayList<Parameter> buildParams(String user, int count) {
        // add encoded parameters
        parameters = new ArrayList<>();
        parameters.add(new Parameter("tweet_mode", encode("extended")));
        parameters.add(new Parameter("screen_name", encode(user)));
        parameters.add(new Parameter("count", count));
        parameters.add(new Parameter("oauth_consumer_key", encode(oAuthConsumerKey)));
        parameters.add(new Parameter("oauth_nonce", encode(oAuthNonce)));
        parameters.add(new Parameter("oauth_signature_method", encode(oAuthSignatureMethod)));
        parameters.add(new Parameter("oauth_timestamp", encode(oAuthTimeStamp)));
        parameters.add(new Parameter("oauth_token", encode(oAuthToken)));
        parameters.add(new Parameter("oauth_version", encode(oAuthVersion)));

        // sort alphabetically
        Collections.sort(parameters, new Comparator<Parameter>() {
            public int compare(Parameter p1, Parameter p2) {
                return p1.getKey().compareTo(p2.getKey());
            }
        });

        return parameters;
    }

    /**
     * URL encodes the given String
     * @param str to encode
     * @return encoded String
     */
    private String encode(String str) {
        String encoded = str;

        try {
            encoded = URLEncoder.encode(str, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return encoded;
    }

    /**
     * Builds Signature Base String based on OAuth Specification
     * @return Signature Base String
     */
    private String getSignatureBase() {

        StringBuilder paramsString = new StringBuilder();

        for (Parameter param : parameters) {
            if (paramsString.length() > 0) {
                paramsString.append("&");
            }
            paramsString.append(param.getKey());
            paramsString.append("=");
            paramsString.append(param.getValue());
        }

        StringBuilder baseString = new StringBuilder();

        baseString.append(encode(method.toString().toUpperCase()));
        baseString.append("&");
        baseString.append(encode(baseURL));
        baseString.append("&");
        baseString.append(encode(paramsString.toString()));
        return baseString.toString();
    }

    /**
     * Builds a Signature String based on OAuth Specification
     * @return Signature String
     */
    private String getSignature() {
        String baseString = getSignatureBase();

        String algorithm = "HmacSHA1";
        String key = oAuthConsumerSecret + "&" + oAuthTokenSecret;

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), algorithm);

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] rawValue = mac.doFinal(baseString.getBytes());
            return Base64.encodeBase64String(rawValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     * Builds Authorization String based on OAuth Specification
     * @return Authorization String
     */
    private String getAuthorization() {
        StringBuilder buf = new StringBuilder();
        buf.append("OAuth ");

        buf.append("oauth_token=\"");
        buf.append(encode(oAuthToken));
        buf.append("\",");

        buf.append("oauth_consumer_key=\"");
        buf.append(encode(oAuthConsumerKey));
        buf.append("\",");

        buf.append("oauth_signature_method=\"");
        buf.append(encode(oAuthSignatureMethod));
        buf.append("\",");

        buf.append("oauth_signature=\"");
        buf.append(encode(oAuthSignature));
        buf.append("\",");

        buf.append("oauth_timestamp=\"");
        buf.append(encode(oAuthTimeStamp));
        buf.append("\",");

        buf.append("oauth_nonce=\"");
        buf.append(encode(oAuthNonce));
        buf.append("\",");

        buf.append("oauth_version=\"");
        buf.append(encode(oAuthVersion));
        buf.append("\"");

        return buf.toString();
    }

    /**
     * A simple key value class to hold parameters
     */
    private class Parameter {

        private final String key;
        private final Object value;

        public Parameter(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

    }

}