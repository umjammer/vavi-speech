/*
 * https://github.com/jiro4989/ojosama/blob/tokendata/util.go
 */

package vavi.speech.modifier.ojosama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Util {

    interface Feat {
        String[] elements();
        /**  。 */
        Feat Kuten = new Feature("記号", "句点");
        /** 、 */
        Feat Toten = new Feature("記号", "読点");
    }

    static class Feature implements Feat {
        String[] elements;
        Feature(String... elements) { this.elements = elements; }
        Feature(Feat f, int s, int e) { this.elements = Arrays.copyOfRange(f.elements(), s, e); }
        public String[] elements() { return elements;}
    }

    /**
     * 英語 文法 品詞
     * @see "https://ja.wikibooks.org/wiki/%E8%8B%B1%E8%AA%9E/%E6%96%87%E6%B3%95/%E5%93%81%E8%A9%9E"
     */
    enum Pos implements Feat {
        PronounGeneral("名詞", "代名詞", "一般"),
        NounsGeneral("名詞", "一般"),
        SpecificGeneral("名詞", "固有名詞", "一般"),
        NotIndependenceGeneral("名詞", "非自立", "一般"),
        AdnominalAdjective("連体詞"),
        AdjectivesSelfSupporting("形容詞", "自立"),
        Interjection("感動詞"),
        VerbIndependence("動詞", "自立"),
        VerbNotIndependence("動詞", "非自立"),
        SentenceEndingParticle("助詞", "終助詞"),
        SubPostpositionalParticle("助詞", "副助詞"),
        AssistantParallelParticle("助詞", "並立助詞"),
        SubParEndParticle("助詞", "副助詞／並立助詞／終助詞"),
        ConnAssistant("助詞", "接続助詞"),
        AuxiliaryVerb("助動詞"),
        NounsSaDynamic("名詞", "サ変接続");
        final String[] elements;
        Pos(String... elements) { this.elements = elements; }
        public String[] elements() { return elements;}
    }

    /**
     * EqualsFeatures は Features が等しいかどうかを判定する。
     *
     * featuresが空の時は * が設定されているため、 * が出現したら以降は無視する。
     *
     * 例えば {名詞,代名詞,一般,*,*} と {名詞,代名詞,一般} を比較したとき、単純にス
     * ライスを完全一致で比較すると false になるが、この関数に関しては * 以降を無視
     * するため true になる。
     */
    public static boolean equalsFeatures(Feat a, Feat b) {
        List<String> a2 = new ArrayList<>();
        for (String v : a.elements()) {
            if (v == null || v.equals("*")) {
                break;
            }
            a2.add(v);
        }

        a = new Feature(a2.toArray(new String[0]));
        if (a.elements().length != b.elements().length) {
            return false;
        }

        for (int i = 0; i < a.elements().length; i++) {
            String v = a.elements()[i];
            if (!v.equals(b.elements()[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * ContainsFeatures は a の中に b が含まれるかを判定する。
     *
     * features用。
     */
    public static boolean containsFeatures(Feat[] a, Feat b) {
        for (Feat a2 : a) {
            if (equalsFeatures(b, a2)) {
                return true;
            }
        }
        return false;
    }

    /** ContainsString は a の中に b が含まれるかを判定する。 */
    public static boolean containsString(String[] a, String b) {
        for (String a2 : a) {
            if (a2.equals(b)) {
                return true;
            }
        }
        return false;
    }
}