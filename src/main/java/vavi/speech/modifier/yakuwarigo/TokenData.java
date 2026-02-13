/*
 * https://github.com/ikawaha/kagome/blob/v2/tokenizer/token.go
 */

package vavi.speech.modifier.yakuwarigo;

import net.java.sen.Token;


/**
 * TokenData is a data format with all the contents of the token.
 */
public class TokenData {

    int id;
    int start;
    int end;
    public String surface;
    String[] pos;
    String base_form;
    String reading;
    String pronunciation;
    public Feature features;

    /** matchAnyTokenData returns true when data matches any of c. */
    public boolean matchAnyTokenData(ConvertCondition[] c) {
        for (ConvertCondition cond : c) {
            if (cond.equalsTokenData(this)) {
                return true;
            }
        }
        return false;
    }

    /** matchAllTokenData returns true when data matches all c. */
    public boolean matchAllTokenData(ConvertCondition[] c) {
        for (ConvertCondition cond : c) {
            if (!cond.equalsTokenData(this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * isPoliteWord determines whether the word is polite.
     * Words that start with the reading "オ" will also be true.
     */
    public boolean isPoliteWord() {
        return this.reading != null && this.reading.startsWith("オ");
    }

    public boolean isKuten() {
        return Feature.equalsFeatures(this.features, Feature.Pos.Kuten) && this.surface.equals("。");
    }

    public TokenData setSurface(String surface) {
        this.surface = surface;
        return this;
    }

    public TokenData setBaseForm(String base_form) {
        this.base_form = base_form;
        return this;
    }

    public TokenData setReading(String reading) {
        this.reading = reading;
        return this;
    }

    public TokenData setFeatures(Feature features) {
        this.features = features;
        return this;
    }

    /** for builder */
    public TokenData() {
    }

    /** new TokenData returns a data which has with all the contents of the token. */
    public TokenData(Token t) {
        id = t.hashCode();
        start = t.start();
        end = t.end();
        surface = t.getSurface();
        pos = t.getPos().split("-");
        features = t.getTermInfo() != null ? new Feature.Feat().setElements(t.getTermInfo().split(",")) : null;
        if (features == null) {
            features = Feature.Feat.NULL;
        }
        base_form = t.getBasicString();
        reading = t.getReading();
        pronunciation = t.getPronunciation();
    }
}
