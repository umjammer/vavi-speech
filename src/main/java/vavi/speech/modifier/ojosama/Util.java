/*
 * https://github.com/jiro4989/ojosama/blob/tokendata/util.go
 */

package vavi.speech.modifier.ojosama;

import java.util.ArrayList;
import java.util.List;


public class Util {

    /**  。 */
    public static final String[] Kuten = new String[] {"記号", "句点"};
    /** 、 */
    public static final String[] Toten = new String[] {"記号", "読点"};

    // 英語 文法 品詞
    // https://ja.wikibooks.org/wiki/%E8%8B%B1%E8%AA%9E/%E6%96%87%E6%B3%95/%E5%93%81%E8%A9%9E

    public static final String[] PronounGeneral = new String[] {"名詞", "代名詞", "一般"};
    public static final String[] NounsGeneral = new String[] {"名詞", "一般"};
    public static final String[] SpecificGeneral = new String[] {"名詞", "固有名詞", "一般"};
    public static final String[] NotIndependenceGeneral = new String[] {"名詞", "非自立", "一般"};
    public static final String[] AdnominalAdjective = new String[] {"連体詞"};
    public static final String[] AdjectivesSelfSupporting = new String[] {"形容詞", "自立"};
    public static final String[] Interjection = new String[] {"感動詞"};
    public static final String[] VerbIndependence = new String[] {"動詞", "自立"};
    public static final String[] VerbNotIndependence = new String[] {"動詞", "非自立"};
    public static final String[] SentenceEndingParticle = new String[] {"助詞", "終助詞"};
    public static final String[] SubPostpositionalParticle = new String[] {"助詞", "副助詞"};
    public static final String[] AssistantParallelParticle = new String[] {"助詞", "並立助詞"};
    public static final String[] SubParEndParticle = new String[] {"助詞", "副助詞／並立助詞／終助詞"};
    public static final String[] ConnAssistant = new String[] {"助詞", "接続助詞"};
    public static final String[] AuxiliaryVerb = new String[] {"助動詞"};
    public static final String[] NounsSaDynamic = new String[] {"名詞", "サ変接続"};

    /**
     * EqualsFeatures は Features が等しいかどうかを判定する。
     *
     * featuresが空の時は * が設定されているため、 * が出現したら以降は無視する。
     *
     * 例えば {名詞,代名詞,一般,*,*} と {名詞,代名詞,一般} を比較したとき、単純にス
     * ライスを完全一致で比較すると false になるが、この関数に関しては * 以降を無視
     * するため true になる。
     */
    public static boolean equalsFeatures(String[] a, String[] b) {
        List<String> a2 = new ArrayList<>();
        for (String v : a) {
            if (v == null || v.equals("*")) {
                break;
            }
            a2.add(v);
        }

        a = a2.toArray(new String[0]);
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            String v = a[i];
            if (!v.equals(b[i])) {
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
    public static boolean containsFeatures(String[][] a, String[] b) {
        for (String[] a2 : a) {
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