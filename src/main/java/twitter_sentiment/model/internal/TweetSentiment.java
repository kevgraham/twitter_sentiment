package twitter_sentiment.model.internal;

public class TweetSentiment {

    private String tweet = "";
    private double anger = 0;
    private double fear = 0;
    private double joy = 0;
    private double sadness = 0;
    private double analytical = 0;
    private double confident = 0;
    private double tentative = 0;

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public double getAnger() {
        return anger;
    }

    public void setAnger(double anger) {
        this.anger = anger;
    }

    public double getFear() {
        return fear;
    }

    public void setFear(double fear) {
        this.fear = fear;
    }

    public double getJoy() {
        return joy;
    }

    public void setJoy(double joy) {
        this.joy = joy;
    }

    public double getSadness() {
        return sadness;
    }

    public void setSadness(double sadness) {
        this.sadness = sadness;
    }

    public double getAnalytical() {
        return analytical;
    }

    public void setAnalytical(double analytical) {
        this.analytical = analytical;
    }

    public double getConfident() {
        return confident;
    }

    public void setConfident(double confident) {
        this.confident = confident;
    }

    public double getTentative() {
        return tentative;
    }

    public void setTentative(double tentative) {
        this.tentative = tentative;
    }
}
