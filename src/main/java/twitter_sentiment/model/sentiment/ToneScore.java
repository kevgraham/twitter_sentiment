package twitter_sentiment.model.sentiment;

public class ToneScore {
    private double score;
    private String tone_id;
    private String tone_name;

    public double getScore() {
        return score;
    }

    public void setScore(double tone) {
        this.score = tone;
    }

    public String getTone_id() {
        return tone_id;
    }

    public void setTone_id(String tone_id) {
        this.tone_id = tone_id;
    }

    public String getTone_name() {
        return tone_name;
    }

    public void setTone_name(String tone_name) {
        this.tone_name = tone_name;
    }
}
