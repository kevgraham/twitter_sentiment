package twitter_sentiment.utilities;

import org.springframework.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OAuthUtil {

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

    public static HttpHeaders getHeader(String baseURL, String user, String oAuthConsumerKey, String oAuthToken,
                                        String oAuthConsumerSecret, String oAuthTokenSecret) {

        OAuthUtil.method = "GET";
        OAuthUtil.baseURL = baseURL;

        OAuthUtil.oAuthConsumerKey = oAuthConsumerKey;
        OAuthUtil.oAuthNonce = String.valueOf(new SecureRandom().nextLong());
        OAuthUtil.oAuthSignatureMethod = "HMAC-SHA1";
        OAuthUtil.oAuthTimeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        OAuthUtil.oAuthToken = oAuthToken;
        OAuthUtil.oAuthVersion = "1.0";

        OAuthUtil.oAuthConsumerSecret = oAuthConsumerSecret;
        OAuthUtil.oAuthTokenSecret = oAuthTokenSecret;

        // build parameter String
        buildParams(user);

        OAuthUtil.oAuthSignature = getSignature();

        OAuthUtil.authorization = getAuthorization();

        // create header
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", authorization);
        System.out.println("\nHeader: " + header + "\n");
        return header;
    }

    private static void buildParams(String user) {
        // add encoded parameters
        parameters = new ArrayList<>();
        parameters.add(new Parameter("screen_name", encode(user)));
        parameters.add(new Parameter("count", "5"));
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
            return Base64.encode(rawValue);
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

    private static class Base64 {

        private final static char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

        private static int[] charToInt = new int[128];

        static {
            for (int i = 0; i < chars.length; i++) {
                charToInt[chars[i]] = i;
            }
        }

        public static String encode(byte[] bytes) {
            int size = bytes.length;
            char[] ar = new char[((size + 2) / 3) * 4];
            int a = 0;
            int i = 0;
            while (i < size) {
                byte b0 = bytes[i++];
                byte b1 = (i < size) ? bytes[i++] : 0;
                byte b2 = (i < size) ? bytes[i++] : 0;

                int mask = 0x3F;
                ar[a++] = chars[(b0 >> 2) & mask];
                ar[a++] = chars[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
                ar[a++] = chars[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
                ar[a++] = chars[b2 & mask];
            }
            switch (size % 3) {
                case 1:
                    ar[--a] = '=';
                case 2:
                    ar[--a] = '=';
            }
            return new String(ar);
        }

        public static byte[] decode(String str) {
            int delta = str.endsWith("==") ? 2 : str.endsWith("=") ? 1 : 0;
            byte[] bytes = new byte[str.length() * 3 / 4 - delta];
            int mask = 0xFF;
            int index = 0;
            for (int i = 0; i < str.length(); i += 4) {
                int c0 = charToInt[str.charAt(i)];
                int c1 = charToInt[str.charAt(i + 1)];
                bytes[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
                if (index >= bytes.length) {
                    return bytes;
                }
                int c2 = charToInt[str.charAt(i + 2)];
                bytes[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
                if (index >= bytes.length) {
                    return bytes;
                }
                int c3 = charToInt[str.charAt(i + 3)];
                bytes[index++] = (byte) (((c2 << 6) | c3) & mask);
            }
            return bytes;
        }

    }
}
