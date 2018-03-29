package twitter_sentiment.utilities;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AuthUtil {

    private static ArrayList<Parameter> parameters = new ArrayList<>();

    private static String method;
    private static String baseURL;

    private static String oAuthConsumerKey;
    private static String oAuthNonce;
    private static String oAuthSignatureMethod;
    private static String oAuthTimeStamp;
    private static String oAuthToken;
    private static String oAuthVersion;

    private static String oAuthConsumerSecret;
    private static String oAuthTokenSecret;

    private static String oAuthSignature;
    private static String authorization;

    /**
     * Creates a Header with Basic Authorization for the Watson Tone API
     * @return
     */
    public static HttpHeaders createWatsonHeader(String username, String password) {

        HttpHeaders headers = new HttpHeaders();

        String auth = username + ":" + password;

        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")) );

        String authHeader = "Basic " + new String( encodedAuth );

        headers.set( "Authorization", authHeader );

        return headers;
    }

    /**
     * Creates a Header with oAuth1 Authorization for the Twitter API
     * @param baseURL
     * @param user
     * @param oAuthConsumerKey
     * @param oAuthToken
     * @param oAuthConsumerSecret
     * @param oAuthTokenSecret
     * @return
     */
    public static HttpHeaders createTwitterHeader(String baseURL, String user, int count, String oAuthConsumerKey, String oAuthToken,
                                        String oAuthConsumerSecret, String oAuthTokenSecret) {

        AuthUtil.method = "GET";
        AuthUtil.baseURL = baseURL;

        AuthUtil.oAuthConsumerKey = oAuthConsumerKey;
        AuthUtil.oAuthNonce = String.valueOf(new SecureRandom().nextLong());
        AuthUtil.oAuthSignatureMethod = "HMAC-SHA1";
        AuthUtil.oAuthTimeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        AuthUtil.oAuthToken = oAuthToken;
        AuthUtil.oAuthVersion = "1.0";

        AuthUtil.oAuthConsumerSecret = oAuthConsumerSecret;
        AuthUtil.oAuthTokenSecret = oAuthTokenSecret;

        // build parameter String
        buildParams(user, count);

        AuthUtil.oAuthSignature = getSignature();

        AuthUtil.authorization = getAuthorization();

        // create header
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", authorization);
        return header;
    }

    private static void buildParams(String user, int count) {
        // add encoded parameters
        parameters = new ArrayList<>();
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

    private static String encode(String str) {
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

    private static String getSignatureBase() {

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

    private static String getSignature() {
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

    private static String getAuthorization() {
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

    private static class Parameter {

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