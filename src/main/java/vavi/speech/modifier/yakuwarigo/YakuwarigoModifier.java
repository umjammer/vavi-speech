/*
 * https://github.com/jiro4989/ojosama/blob/main/ojosama.go
 */

package vavi.speech.modifier.yakuwarigo;


import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import net.java.sen.StringTagger;
import net.java.sen.Token;
import vavi.util.Debug;

import static vavi.speech.modifier.yakuwarigo.Feature.containsFeatures;
import static vavi.speech.modifier.yakuwarigo.Feature.containsString;
import static vavi.speech.modifier.yakuwarigo.Rule.ContinuousConditionsConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.ConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule.MeaningType;
import static vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule.getMeaningType;


/**
 * YakuwarigoModifier.
 * <p>
 *
 * @see "https://ja.wikipedia.org/wiki/%E5%BD%B9%E5%89%B2%E8%AA%9E"
 */
public class YakuwarigoModifier {

    /** Settings */
    public static class ConvertOption {
        /**
         * 句点を！に変換する機能をOFFにする。
         * 句点を！に変換してしまうと変換元の文章のニュアンスを破壊する可能性があるため、
         * オプションパラメータで無効にできるようにする。
         */
        public boolean disableKutenToExclamation;

        /** disable adding "〜" randomly */
        public boolean disableLongNote;

        /**
         * disable adding the prefix "お"
         * TODO out source
         */
        public boolean disablePrefix;

        /** rule name */
        public String name;
    }

    /** alphabet */
    private static final Pattern alnumRegexp = Pattern.compile("^[a-zA-Z0-9]+$");

    /** */
    public static class StringResult {
        public String str;
        public int pos;

        public StringResult(String s, int pos) {
            this.str = s;
            this.pos = pos;
        }
    }

    /** input source */
    public Token[] tokens;

    /** settings */
    public ConvertOption opt;

    /** */
    private Provider provider;

    /** rule database */
    private Rule rule;

    /** japanese tokenizer */
    private StringTagger tokenizer;

    /** w/o settings */
    public YakuwarigoModifier() throws IOException {
        this(null);
    }

    /**
     * @param opt は挙動を微調整するためのオプショナルなパラメータ。
     *            不要であれば null を渡せば良い。
     */
    public YakuwarigoModifier(ConvertOption opt) throws IOException {
        this.opt = opt;
        this.provider = Provider.getProvider(opt != null && opt.name != null ? opt.name : "salome");
        this.rule = provider.getRule();

        this.tokenizer = StringTagger.getInstance(); // TODO OmitBosEos
    }

    /**
     * Convert はテキストを壱百満天原サロメお嬢様風の口調に変換して返却する。
     * <p>
     * 簡単に説明すると「ハーブですわ！」を「おハーブですわ～～！！！」と変換する。
     * それ以外にもいくつかバリエーションがある。
     * <p>
     * 一部変換の途中でランダムに要素を選択するため、
     * 呼び出し側で乱数のシードの初期化を行うこと。
     */
    public String convert(String src) throws IOException {
        // tokenize
        this.tokens = tokenizer.analyze(src);
        provider.setContext(this);

        StringBuilder result = new StringBuilder();
        boolean nounKeep = false;
        for (int p = 0; p < tokens.length; p++) {
            TokenData data = new TokenData(tokens[p]);
            String buf = data.surface;

            // 英数字のみの単語の場合は何もしない
            if (alnumRegexp.matcher(buf).matches()) {
                result.append(buf);
                continue;
            }

            // 名詞＋動詞＋終助詞の組み合わせに対して変換する
            StringResult s = convertSentenceEndingParticle(p);
            if (s != null) {
                p = s.pos;
                result.append(s.str);
                continue;
            }

            // 連続する条件による変換を行う
            s = convertContinuousConditions(p);
            if (s != null) {
                p = s.pos;
                result.append(s.str);
                continue;
            }

            // 特定条件は優先して無視する
            if (matchExcludeRule(data)) {
                result.append(buf);
                continue;
            }

            // convert yakuwarigo
            ConversionResult cr = convert(data, p, buf, nounKeep);
            buf = cr.buf;
            nounKeep = cr.nounKeep;
            p = cr.pos;
            if (cr.extraRule != null) {
                s = provider.execExtraRule(cr.extraRule, p);
                if (s != null) {
                    buf += s.str;
                    p = s.pos;
                }
            }
Debug.printf(Level.FINER, "token[%d] result: %s", p, buf);
            result.append(buf);
        }
        return result.toString();
    }

    /**
     * convertSentenceEndingParticle は名詞＋動詞（＋助動詞）＋終助詞の組み合わせすべてを満たす場合に変換する。
     * <p>
     * 終助詞は文の終わりに、文を完結させつつ、文に「希望」「禁止」「詠嘆」「強意」等の意味を添える効果がある。
     * <p>
     * 例えば「野球しようぜ」の場合、
     * 「名詞：野球」「動詞：しよ」「助動詞：う」「終助詞：ぜ」という分解がされる。
     * <p>
     * 終助詞の「ぜ」としては「希望」の意味合いが含まれるため、希望する意味合いのお嬢様言葉に変換する。
     * 例：お野球をいたしませんこと
     * <p>
     * その他にも「野球するな」だと「お野球をしてはいけませんわ」になる。
     */
    private StringResult convertSentenceEndingParticle(int tokenPos) {
        for (SentenceEndingParticleConvertRule r : rule.sentenceEndingParticleConvertRules) {
            StringBuilder result = new StringBuilder();
            int p = tokenPos;
            TokenData data = new TokenData(tokens[p]);

            // 先頭が一致するならば次の単語に進む
            if (!data.matchAnyTokenData(r.conditions1)) {
                continue;
            }
            if (tokens.length <= p + 1) {
                continue;
            }
            String s = provider.convert(data); // TODO location
            result.append(s);
            p++;
            data = new TokenData(tokens[p]);

            // NOTE:
            // 2つ目以降は value の値で置き換えるため
            // result.append(data.Surface) を実行しない。

            // 2つ目は動詞のいずれかとマッチする。マッチしなければふりだしに戻る
            if (!data.matchAnyTokenData(r.conditions2)) {
                continue;
            }
            if (tokens.length <= p + 1) {
                continue;
            }
            p++;
            data = new TokenData(tokens[p]);

            // 助動詞があった場合は無視してトークンを進める。
            // 別に無くても良い。
            if (data.matchAllTokenData(r.auxiliaryVerb)) {
                if (tokens.length <= p + 1) {
                    continue;
                }
                p++;
                data = new TokenData(tokens[p]);
            }

            // 最後、終助詞がどの意味分類に該当するかを取得
            MeaningType mt = getMeaningType(r.sentenceEndingParticle, data);
            if (mt == MeaningType.Unknown) {
                continue;
            }

            // 意味分類に該当する変換候補の文字列を返す
            // TODO 現状1個だけなので決め打ちで最初の1つ目を返す。
            result.append(r.value.get(mt)[0]);
            return new StringResult(result.toString(), p);
        }
        return null;
    }

    /**
     * 連続する条件による変換ルールにマッチした変換結果を返す。
     * <p>
     * 例えば「壱百満天原サロメ」や「横断歩道」のように、複数のTokenがこの順序で連続
     * して初めて1つの意味になるような条件をすべて満たした時に結果を返す。
     * <p>
     * 連続する条件にマッチした場合は tokenPos をその分だけ進める必要があるため、進
     * めた後の tokenPos を返却する。
     * <p>
     * 第三引数は変換ルールにマッチしたかどうかを返す。
     */
    StringResult convertContinuousConditions(int tokenPos) {
        for (ContinuousConditionsConvertRule mc : rule.continuousConditionsConvertRules) {
            if (!matchContinuousConditions(tokenPos, mc.conditions)) {
                continue;
            }

            return provider.convert(mc, tokenPos);
        }
        return null;
    }

    /**
     * tokens の tokenPos の位置からのトークンが、連続する条件にすべてマッチするかを判定する。
     * <p>
     * 次のトークンが存在しなかったり、1つでも条件が不一致になった場合 false を返す。
     */
    private boolean matchContinuousConditions(int tokenPos, ConvertCondition[] ccs) {
        int p = tokenPos;
        for (ConvertCondition conds : ccs) {
            if (tokens.length <= p) {
                return false;
            }
            TokenData data = new TokenData(tokens[p]);
            if (!conds.equalsTokenData(data)) {
                return false;
            }
            p++;
        }
        return true;
    }

    /** 除外ルールと一致するものが存在するかを判定する。 */
    private boolean matchExcludeRule(TokenData data) {
        for (ConvertRule c : rule.excludeRules) {
            if (!data.matchAllTokenData(c.conditions)) {
                continue;
            }
            return true;
        }
        return false;
    }

    /** */
    public static class ConversionResult {
        String buf;
        boolean nounKeep;
        int pos;
        String extraRule;

        public ConversionResult(String buf, boolean nounKeep, int pos, String extraRule) {
            this.buf = buf;
            this.nounKeep = nounKeep;
            this.pos = pos;
            this.extraRule = extraRule;
        }
    }

    /** 基本的な変換を行う。 */
    private ConversionResult convert(TokenData data, int p, String surface, boolean nounKeep) {
        ConvertRule c = matchConvertRule(data, p);
        return provider.convert(c, data, p, surface, nounKeep);
    }

    /** @return nullable */
    private ConvertRule matchConvertRule(TokenData data, int p) {
        TokenData beforeToken = null;
        boolean beforeTokenOK = false;
        if (0 < p) {
            beforeToken = new TokenData(tokens[p - 1]);
            beforeTokenOK = true;
        }

        TokenData afterToken = null;
        boolean afterTokenOK = false;
        if (p + 1 < tokens.length) {
            afterToken = new TokenData(tokens[p + 1]);
            afterTokenOK = true;
        }

        for (ConvertRule c : rule.convertRules) {
            if (!data.matchAllTokenData(c.conditions)) {
Debug.println(Level.FINER, "skipped: " + c.value);
                continue;
            }

            // 前に続く単語をみて変換を無視する
            if (beforeTokenOK && c.beforeIgnoreConditions != null && beforeToken.matchAnyTokenData(c.beforeIgnoreConditions)) {
Debug.println(Level.FINE, "break cause before token");
                break;
            }

            // 次に続く単語をみて変換を無視する
            if (afterTokenOK && c.afterIgnoreConditions != null && afterToken.matchAnyTokenData(c.afterIgnoreConditions)) {
Debug.println(Level.FINE, "break cause after token");
                break;
            }

            // 文の区切りか、文の終わりの時だけ有効にする。
            // 次のトークンが存在して、且つ次のトークンが文を区切るトークンでない時
            // は変換しない。
            if (c.enableWhenSentenceSeparation && afterTokenOK && !isSentenceSeparation(afterToken)) {
Debug.println(Level.FINE, "break cause sentence termination");
                break;
            }

Debug.println(Level.FINE, "select: " + c.value + " for surface: " + tokens[p].getSurface());
            return c;
        }
        return null;
    }

    /** isSentenceSeparation は data が文の区切りに使われる token かどうかを判定する。 */
    private static boolean isSentenceSeparation(TokenData data) {
        return containsFeatures(new Feature[] {Feature.Kuten, Feature.Toten}, data.features) ||
                containsString(new String[] {"！", "!", "？", "?"}, data.surface);
    }
}
