/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_condition.go
 */

package vavi.speech.modifier.yakuwarigo;

import java.util.regex.Pattern;


/**
 * ConvertCondition is the condition to use for conversion.
 *
 * ConvertCondition[] is a slice of conversion conditions.
 * Whether it is evaluated as AND or OR depends on the user of this type.
 */
public class ConvertCondition {

    Feature feature;
    String reading;
    /** Optional, only used if set */
    Pattern readingRe;
    String surface;
    /** Optional, only used if set */
    Pattern surfaceRe;
    String baseForm;
    /** Optional, only used if set */
    Pattern baseFormRe;

    private static boolean isNotEmptyStringAndDoesntEqualString(String a, String b) {
        return a != null && !a.isEmpty() && !a.equals(b);
    }

    private static boolean isNotNullAndDoesntMatchString(Pattern a, String b) {
        return a != null && !a.matcher(b).matches();
    }

    public boolean equalsTokenData(TokenData data) {
        if (this.feature != null && 0 < this.feature.elements().length && !Feature.equalsFeatures(data.features, this.feature)) {
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