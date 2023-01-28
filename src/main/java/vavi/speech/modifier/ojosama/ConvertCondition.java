/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_condition.go
 */

package vavi.speech.modifier.ojosama;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static vavi.speech.modifier.ojosama.Util.equalsFeatures;


/** ConvertCondition は変換に使う条件。 */
public class ConvertCondition {

    String[] features;
    String reading;
    /** オプション。設定されてる時だけ使う */
    Pattern readingRe;
    String surface;
    /** オプション。設定されてる時だけ使う */
    Pattern surfaceRe;
    String baseForm;
    /** オプション。設定されてる時だけ使う */
    Pattern baseFormRe;

    private static boolean isNotEmptyStringAndDoesntEqualString(String a, String b) {
        return a != null && !a.isEmpty() && !a.equals(b);
    }

    private static boolean isNotNullAndDoesntMatchString(Pattern a, String b) {
        return a != null && !a.matcher(b).matches();
    }

    public boolean equalsTokenData(TokenData data) {
        if (this.features != null && 0 < this.features.length && !equalsFeatures(data.features, this.features)) {
            return false;
        }
        if (isNotEmptyStringAndDoesntEqualString(this.surface, data.surface)) {
            return false;
        }
        if (isNotEmptyStringAndDoesntEqualString(this.reading, data.reading)) {
            return false;
        }
        if (isNotEmptyStringAndDoesntEqualString(this.baseForm, data.base_form)) {
            return false;
        }
        if (isNotNullAndDoesntMatchString(this.surfaceRe, data.surface)) {
            return false;
        }
        if (isNotNullAndDoesntMatchString(this.readingRe, data.reading)) {
            return false;
        }
        if (isNotNullAndDoesntMatchString(this.baseFormRe, data.base_form)) {
            return false;
        }

        return true;
    }

    // builders

    public ConvertCondition setFeatures(String[] features) {
        this.features = features;
        return this;
    }

    public ConvertCondition setReading(String reading) {
        this.reading = reading;
        return this;
    }

    public ConvertCondition setReadingRe(Pattern readingRe) {
        this.readingRe = readingRe;
        return this;
    }

    public ConvertCondition setSurface(String surface) {
        this.surface = surface;
        return this;
    }

    public ConvertCondition setSurfaceRe(Pattern surfaceRe) {
        this.surfaceRe = surfaceRe;
        return this;
    }

    public ConvertCondition setBaseForm(String baseForm) {
        this.baseForm = baseForm;
        return this;
    }

    public ConvertCondition setBaseFormRe(Pattern baseFormRe) {
        this.baseFormRe = baseFormRe;
        return this;
    }

    // convenient instanciators

    public static ConvertCondition newCond(String[] features, String surface) {
        return new ConvertCondition()
                .setFeatures(features)
                .setSurface(surface);
    }

    public static ConvertCondition newCondRe(String[] features, Pattern surfaceRe) {
        return new ConvertCondition()
                .setFeatures(features)
                .setSurfaceRe(surfaceRe);
    }

    public static ConvertCondition newCondSentenceEndingParticle(String surface) {
        return new ConvertCondition()
                .setFeatures(Util.SentenceEndingParticle)
                .setSurface(surface);
    }

    public static ConvertCondition newCondAuxiliaryVerb(String surface) {
        return new ConvertCondition()
                .setFeatures(Util.AuxiliaryVerb)
                .setSurface(surface);
    }

    /**
     * ConvertCondition[] は変換条件のスライス。
     *
     * ANDで評価するかORで評価するかは、この型を使う側に依存する
     */
    public static ConvertCondition[] newConds(String[] surfaces) {
        List<ConvertCondition> c = new ArrayList<>();
        for (String s : surfaces) {
            ConvertCondition cc = new ConvertCondition().setSurface(s);
            c.add(cc);
        }
        return c.toArray(new ConvertCondition[0]);
    }
}