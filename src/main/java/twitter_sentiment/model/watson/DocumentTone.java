package twitter_sentiment.model.watson;

public class DocumentTone {
    private ToneScore[] tones;

    public ToneScore[] getTones() {
        return tones;
    }

    public void setTones(ToneScore[] tones) {
        this.tones = tones;
    }
}
