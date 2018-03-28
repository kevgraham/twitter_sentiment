package twitter_sentiment.model.sentiment;

public class SentenceTone {

    int sentence_id;
    String text;
    ToneScore[] tones;

    public int getSentence_id() {
        return sentence_id;
    }

    public void setSentence_id(int sentence_id) {
        this.sentence_id = sentence_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ToneScore[] getTones() {
        return tones;
    }

    public void setTones(ToneScore[] tones) {
        this.tones = tones;
    }
}
