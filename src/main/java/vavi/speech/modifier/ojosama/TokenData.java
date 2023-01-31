/*
 * https://github.com/ikawaha/kagome/blob/v2/tokenizer/token.go
 */

package vavi.speech.modifier.ojosama;

import net.java.sen.Token;
import static vavi.speech.modifier.ojosama.Util.Feature;
import static vavi.speech.modifier.ojosama.Util.Feat;
import static vavi.speech.modifier.ojosama.Util.Pos;


/**
 * TokenData is a data format with all the contents of the token.
 */
public class TokenData {

    int id;
    int start;
    int end;
    String surface;
    String[] pos;
    String base_form;
    String reading;
    String pronunciation;
    Feat features;

    /** matchAnyTokenData は  data がいずれかの c と一致した時に true を返す。*/
    public boolean matchAnyTokenData(ConvertCondition[] c) {
        for (ConvertCondition cond : c) {
            if (cond.equalsTokenData(this)) {
                return true;
            }
        }
        return false;
    }

    /** matchAllTokenData は data がすべての c と一致した時に true を返す。 */
    public boolean matchAllTokenData(ConvertCondition[] c) {
        for (ConvertCondition cond : c) {
            if (!cond.equalsTokenData(this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * isPoliteWord は丁寧語かどうかを判定する。
     * 読みがオで始まる言葉も true になる。
     */
    public boolean isPoliteWord() {
        return this.reading != null && this.reading.startsWith("オ");
    }

    public boolean isKuten() {
        return Util.equalsFeatures(this.features, Pos.Kuten) && this.surface.equals("。");
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

    public TokenData setFeatures(Feat features) {
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
        features = t.getTermInfo() != null ? new Feature(t.getTermInfo().split(",")) : null;
        if (features == null) {
            features = new Feature();
        }
        base_form = t.getBasicString();
        reading = t.getReading();
        pronunciation = t.getPronunciation();
    }
}
