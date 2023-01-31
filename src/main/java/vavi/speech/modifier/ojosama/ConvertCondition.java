/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_condition.go
 */

package vavi.speech.modifier.ojosama;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static vavi.speech.modifier.ojosama.Util.equalsFeatures;
import static vavi.speech.modifier.ojosama.Util.Feat;


/**
 * ConvertCondition は変換に使う条件。
 *
 * ConvertCondition[] は変換条件のスライス。
 * ANDで評価するかORで評価するかは、この型を使う側に依存する
 */
public class ConvertCondition {

    Feat features;
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
        if (this.features != null && 0 < this.features.elements().length && !equalsFeatures(data.features, this.features)) {
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

    public ConvertCondition setFeatures(Feat features) {
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

    @Override
    public String toString() {
        return '"' + surface + '"';
    }
}