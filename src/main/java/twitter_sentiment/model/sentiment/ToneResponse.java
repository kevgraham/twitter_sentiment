package twitter_sentiment.model.sentiment;

public class ToneResponse {
    DocumentTone document_tone;
    SentenceTone[] sentences_tone;

    public DocumentTone getDocument_tone() {
        return document_tone;
    }

    public void setDocument_tone(DocumentTone document_tone) {
        this.document_tone = document_tone;
    }

    public SentenceTone[] getSentences_tone() {
        return sentences_tone;
    }

    public void setSentences_tone(SentenceTone[] sentences_tone) {
        this.sentences_tone = sentences_tone;
    }
}
