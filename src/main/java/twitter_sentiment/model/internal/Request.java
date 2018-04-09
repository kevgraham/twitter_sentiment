package twitter_sentiment.model.internal;

public class Request {

    private int id;
    private int apikey_id;
    private String timestamp;
    private String endpoint;

    public Request(int id, int apikey_id, String timestamp, String endpoint) {
        this.id = id;
        this.apikey_id = apikey_id;
        this.timestamp = timestamp;
        this.endpoint = endpoint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApikey_id() {
        return apikey_id;
    }

    public void setApikey_id(int apikey_id) {
        this.apikey_id = apikey_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
