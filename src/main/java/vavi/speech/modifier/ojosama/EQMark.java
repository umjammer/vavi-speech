/*
 * https://github.com/jiro4989/ojosama/blob/chars/chars.go
 */

package vavi.speech.modifier.ojosama;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class EQMark {

    String value;
    StyleType style;
    MeaningType meaning;

    public EQMark(String value, StyleType style, MeaningType meaning) {
        this.value = value;
        this.style = style;
        this.meaning = meaning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EQMark that = (EQMark) o;
        return value.equals(that.value) && style == that.style && meaning == that.meaning;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, style, meaning);
    }

    @Override
    public String toString() {
        return "EQMark{" +
                "value='" + value + '\'' +
                ", style=" + style +
                ", meaning=" + meaning +
                '}';
    }

    enum StyleType {
        Unknown,
        FullWidth,
        HalfWidth,
        Emoji,
        DoubleEmoji // !!
    }

    enum MeaningType {
        Unknown,
        Excl,    // !
        Ques,    // ?
        EQ      // !?
    }

    static final EQMark[] eqMarks = new EQMark[] {
            newExcl("！", StyleType.FullWidth),
            newExcl("!", StyleType.HalfWidth),
            newExcl("❗", StyleType.Emoji),
            newExcl("‼", StyleType.DoubleEmoji),
            newQues("？", StyleType.FullWidth),
            newQues("?", StyleType.HalfWidth),
            newQues("❓", StyleType.Emoji),
            newEQ("!?", StyleType.HalfWidth),
            newEQ("⁉", StyleType.Emoji),
    };

    static EQMark newExcl(String v, StyleType t) {
        return new EQMark(v, t, MeaningType.Excl);
    }

    static EQMark newQues(String v, StyleType t) {
        return new EQMark(v, t, MeaningType.Ques);
    }

    static EQMark newEQ(String v, StyleType t) {
        return new EQMark(v, t, MeaningType.EQ);
    }

    /** */
    public static EQMark isExclamationQuestionMark(String s) {
        for (EQMark v : eqMarks) {
            if (v.value.equals(s)) {
                return v;
            }
        }
        return null;
    }

    /** */
    public static class Shuffler {
        EQMark getFirst(List<EQMark> s) {
            Collections.shuffle(s, YakuwarigoModifier.random);
            return s.get(0);
        }
    }

    /** */
    private static final Shuffler shuffler = new Shuffler();

    /** */
    public static EQMark sampleExclamationQuestionByValue(String v) {
        EQMark got = isExclamationQuestionMark(v);
        if (got == null) {
            return null;
        }

        List<EQMark> s = new ArrayList<>();
        for (EQMark mark : eqMarks) {
            if (mark.meaning == got.meaning) {
                s.add(mark);
            }
        }
        // 到達しないはずだけれど一応いれてる;
        if (s.size() < 1) {
            return null;
        }

        return shuffler.getFirst(s);
    }

    /** */
    public static EQMark findExclamationQuestionByStyleAndMeaning(StyleType s, MeaningType m) {
        List<EQMark> eq = new ArrayList<>();
        for (EQMark mark : eqMarks) {
            if (mark.style == s) {
                eq.add(mark);
            }
        }
        if (eq.size() < 1) {
            return null;
        }

        for (EQMark mark : eq) {
            if (mark.meaning == m) {
                return mark;
            }
        }

        return null;
    }
}