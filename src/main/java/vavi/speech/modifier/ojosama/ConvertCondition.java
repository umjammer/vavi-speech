/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_condition.go
 */

package vavi.speech.modifier.ojosama;

import java.util.regex.Pattern;

import static vavi.speech.modifier.ojosama.Feature.equalsFeatures;


/**
 * ConvertCondition は変換に使う条件。
 *
 * ConvertCondition[] は変換条件のスライス。
 * ANDで評価するかORで評価するかは、この型を使う側に依存する
 */
public class ConvertCondition {

    Feature feature;
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
        if (this.feature != null && 0 < this.feature.elements().length && !equalsFeatures(data.features, this.feature)) {
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

    @Override
    public String toString() {
        return "ConvertCondition{" +
                "feature=" + feature +
                ", reading='" + reading + '\'' +
                ", readingRe=" + readingRe +
                ", surface='" + surface + '\'' +
                ", surfaceRe=" + surfaceRe +
                ", baseForm='" + baseForm + '\'' +
                ", baseFormRe=" + baseFormRe +
                '}';
    }
}