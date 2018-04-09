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

    // Ryan: I have lots of questions in here, but I know you basically copied it right? To get it to work?
    // Do you why know why you had to?

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
     * Creates a Header with oAuth1 Authorization for the Twitter API
     * @param baseURL
     * @param user
     * @return
     */
    public HttpHeaders createTwitterHeader(String baseURL, String user, int count) {

        this.method = "GET";
        this.baseURL = baseURL;

        this.oAuthNonce = String.valueOf(new SecureRandom().nextLong());
        this.oAuthSignatureMethod = "HMAC-SHA1";
        this.oAuthTimeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        this.oAuthVersion = "1.0";

        // build parameter String
        buildParams(user, count);

        this.oAuthSignature = getSignature();

        this.authorization = getAuthorization();

        // create header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void buildParams(String user, int count) {
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
    }

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