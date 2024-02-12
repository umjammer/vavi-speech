/*
 * https://github.com/jiro4989/ojosama/blob/chars/chars.go
 */

package vavi.speech.modifier.yakuwarigo.salome;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class EQMark {

    String value;
    StyleType style;
    MeaningType meaning;

    EQMark(String value, StyleType style, MeaningType meaning) {
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
        Excl, // !
        Ques, // ?
        EQ // !?
    }

    static final EQMark[] eqMarks = new EQMark[] {
            new EQMark("！", StyleType.FullWidth, MeaningType.Excl),
            new EQMark("!", StyleType.HalfWidth, MeaningType.Excl),
            new EQMark("❗", StyleType.Emoji, MeaningType.Excl),
            new EQMark("‼", StyleType.DoubleEmoji, MeaningType.Excl),
            new EQMark("？", StyleType.FullWidth, MeaningType.Ques),
            new EQMark("?", StyleType.HalfWidth, MeaningType.Ques),
            new EQMark("❓", StyleType.Emoji, MeaningType.Ques),
            new EQMark("!?", StyleType.HalfWidth, MeaningType.EQ),
            new EQMark("⁉", StyleType.Emoji, MeaningType.EQ),
    };

    /** */
    static EQMark isExclamationQuestionMark(String s) {
        for (EQMark v : eqMarks) {
            if (v.value.equals(s)) {
                return v;
            }
        }
        return null;
    }

    /** */
    static class Shuffler {
        /** */
        public static final Random random = new Random(System.currentTimeMillis());

        public EQMark getFirst(List<EQMark> s) {
            Collections.shuffle(s, random);
            return s.get(0);
        }
    }

    /** */
    private static final Shuffler shuffler = new Shuffler();

    /** */
    static EQMark sampleExclamationQuestionByValue(String v) {
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
    static EQMark findExclamationQuestionByStyleAndMeaning(StyleType s, MeaningType m) {
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