/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo.salome;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import net.java.sen.Token;
import vavi.speech.modifier.yakuwarigo.Feature;
import vavi.speech.modifier.yakuwarigo.Provider;
import vavi.speech.modifier.yakuwarigo.TokenData;
import vavi.speech.modifier.yakuwarigo.YakuwarigoModifier;
import vavi.util.Debug;

import static vavi.speech.modifier.yakuwarigo.salome.EQMark.Shuffler.random;
import static vavi.speech.modifier.yakuwarigo.salome.EQMark.findExclamationQuestionByStyleAndMeaning;
import static vavi.speech.modifier.yakuwarigo.salome.EQMark.isExclamationQuestionMark;
import static vavi.speech.modifier.yakuwarigo.salome.EQMark.sampleExclamationQuestionByValue;
import static vavi.speech.modifier.yakuwarigo.Feature.equalsFeatures;
import static vavi.speech.modifier.yakuwarigo.Feature.slice;
import static vavi.speech.modifier.yakuwarigo.Rule.ContinuousConditionsConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.ConvertRule;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConversionResult;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.StringResult;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConvertOption;


/**
 * SalomeProvider.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-04-23 nsano initial version <br>
 */
public class SalomeProvider implements Provider {

    /** */
    private Token[] tokens;

    /** */
    private ConvertOption opt;

    @Override
    public String getName() {
        return "salome";
    }

    @Override
    public void setContext(YakuwarigoModifier context) {
        this.tokens = context.tokens;
        this.opt = context.opt;
    }

    @Override
    public StringResult convert(ContinuousConditionsConvertRule mc, int tokenPos) {
        int p = tokenPos + mc.conditions.length - 1;
        String result = mc.value;

        // FIXME 書き方が汚い
        TokenData data = new TokenData(this.tokens[tokenPos]);
        String surface = data.surface;
        if ((this.opt == null || !this.opt.disablePrefix) && isPrefixAppendable(data)) {
            surface = "お" + surface;
        }
        result = result.replaceAll("@1", surface);

        // 句点と～が同時に発生することは無いので早期リターンで良い
        StringResult sr = randomKutenToExclamation(p);
        if (sr != null) {
            result += sr.str;
            p = sr.pos;
            return new StringResult(result, p);
        }

        if (mc.extraRule3 != null) {
            sr = this.execExtraRule(mc.extraRule3, p);
            if (sr != null) {
                result += sr.str;
                p = sr.pos;
            }
        }

        return new StringResult(result, p);
    }

    @Override
    public ConversionResult convert(ConvertRule c, TokenData data, int p, String surface, boolean nounKeep) {
        if (c == null) {
            AppendResult ar = appendPrefix(data, p, surface, nounKeep);
            return new ConversionResult(ar.result, ar.nounKeep, p, null);
        }

        String result = c.value;
        int pos = p;
        result = result.replaceAll("@1", data.surface);

        // 波線伸ばしをランダムに追加する
        if (this.opt == null || !this.opt.disableLongNote && c.extraRule3 != null) {
            StringResult sr = this.execExtraRule(c.extraRule3, pos);
            if (sr != null) {
                result += sr.str;
                pos = sr.pos;
            }
        }

        // 手前に「お」を付ける
        if (this.opt == null || !this.opt.disablePrefix && c.extraRule2 == null) {
            AppendResult ar = appendPrefix(data, pos, result, nounKeep);
            result = ar.result;
            nounKeep = ar.nounKeep;
        }

        return new ConversionResult(result, nounKeep, pos, c.extraRule);
    }

    @Override
    public String convert(TokenData data) {
        String s = data.surface;
        // TODO ベタ書きしててよくない
        if ((this.opt == null || !this.opt.disablePrefix) && (equalsFeatures(data.features, Feature.Pos.NounsGeneral) ||
                equalsFeatures(slice(data.features, 0, 2), Feature.Pos.NounsSaDynamic))) {
            s = "お" + s;
        }
        return s;
    }

    //----

    /** */
    private static final String[] shuffleElementsKutenToExclamation = {"。", "。", "！", "❗"};

    /** */
    static class Randomizer {
        public int wavyLineCount() {
            return random.nextInt(3);
        }
        public int exclamationMarkCount() {
            return random.nextInt(3);
        }
    }

    /**
     * 乱数が絡むと単体テストがやりづらくなるので、
     * set mock sub class here for fixed fixture.
     */
    private static final Randomizer randomizer = new Randomizer();

    /**
     * newLongNote は次の token が感嘆符か疑問符の場合に波線、感嘆符、疑問符をランダムに生成する。
     *
     * @param p tokens index
     * @return nullable
     */
    public StringResult newLongNote(int p) {
        String s = creatableLongNote(p);
        if (s == null) {
            return null;
        }

        int w = randomizer.wavyLineCount();
        int e = randomizer.exclamationMarkCount();

        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < w; i++) {
            suffix.append("～");
        }

        // ！or？をどれかからランダムに選択する
        EQMark feq = sampleExclamationQuestionByValue(s);

        // 次の token は必ず感嘆符か疑問符のどちらかであることが確定しているため
        // -1 して数を調整している。
        for (int i = 0; i < e - 1; i++) {
            suffix.append(feq.value);
        }

        // 後ろに！や？が連続する場合、それらをすべて feq と同じ種類（半角、全角、
        // 絵文字）の！や？に置き換えて返却する。
        StringResult sr = getContinuousExclamationMark(p, feq);
        suffix.append(sr.str);
        return new StringResult(suffix.toString(), sr.pos);
    }

    /**
     * @param p tokens index
     * @return nullable
     */
    private String creatableLongNote(int p) {
        if (this.tokens.length <= p + 1) {
            return null;
        }

        Token data = this.tokens[p + 1];
        for (String s : new String[] {"！", "？", "!", "?"}) {
            if (!data.getSurface().equals(s)) {
                continue;
            }
            return s;
        }
        return null;
    }

    /**
     * @param p tokens index
     */
    private StringResult getContinuousExclamationMark(int p, EQMark feq) {
        StringBuilder result = new StringBuilder();
        int pos = p;

        for (int i = p + 1; i < this.tokens.length; i++) {
            TokenData token = new TokenData(this.tokens[i]);
            for (char r : token.surface.toCharArray()) {
                EQMark eq = isExclamationQuestionMark(String.valueOf(r));
                if (eq == null) {
                    return new StringResult(result.toString(), pos);
                } else {
                    // e は！か？のどちらかなので、同じスタイルの文字を取得して追加
                    EQMark got = findExclamationQuestionByStyleAndMeaning(feq.style, eq.meaning);
                    if (got != null) {
                        result.append(got.value);
                    }
                }
            }
            // トークンの位置を制御する変数なので、forループ内では変更しない
            pos = i;
        }

        return new StringResult(result.toString(), pos);
    }

    /**
     * randomKutenToExclamation はランダムで句点を！に変換する。
     *
     * @return nullable
     */
    public StringResult randomKutenToExclamation(int tokenPos) {
        if (this.opt != null && this.opt.disableKutenToExclamation) {
            return null;
        }

        int pos = tokenPos + 1;
        if (this.tokens.length <= pos) {
            return null;
        }

        TokenData data = new TokenData(this.tokens[pos]);
        if (!data.isKuten()) {
            return null;
        }

        List<String> l = Arrays.asList(shuffleElementsKutenToExclamation);
Debug.println(Level.FINER, "shuffleElementsKutenToExclamation: " + l);
        Collections.shuffle(l, random);
        return new StringResult(l.get(0), pos);
    }

    //----

    /** */
    private boolean isPrefixAppendable(TokenData data) {
        if (!equalsFeatures(data.features, new Feature.Feat().setElements("名詞", "一般")) &&
                !equalsFeatures(slice(data.features, 0, 2), new Feature.Feat().setElements("名詞", "固有名詞"))) {
            return false;
        }

        // 丁寧語の場合は「お」を付けない
        return !data.isPoliteWord();
    }

    /** */
    private static class AppendResult {
        String result;
        boolean nounKeep;

        AppendResult(String result, boolean nounKeep) {
            this.result = result;
            this.nounKeep = nounKeep;
        }
    }

    /** surface の前に「お」を付ける。 */
    private AppendResult appendPrefix(TokenData data, int i, String surface, boolean nounKeep) {
        if (!isPrefixAppendable(data)) {
            return new AppendResult(surface, false);
        }

        // 次のトークンが動詞の場合は「お」を付けない。
        // 例: プレイする
        if (i + 1 < this.tokens.length) {
            data = new TokenData(this.tokens[i + 1]);
            if (equalsFeatures(data.features, new Feature.Feat().setElements("動詞", "自立"))) {
                return new AppendResult(surface, nounKeep);
            }
        }

        // すでに「お」を付与されているので、「お」を付与しない
        if (nounKeep) {
            return new AppendResult(surface, false);
        }

        if (0 < i) {
            data = new TokenData(this.tokens[i - 1]);

            // 手前のトークンが「お」の場合は付与しない
            if (equalsFeatures(data.features, new Feature.Feat().setElements("接頭詞", "名詞接続"))) {
                return new AppendResult(surface, false);
            }

            // サ変接続が来ても付与しない。
            // 例: 横断歩道、解体新書
            if (equalsFeatures(data.features, new Feature.Feat().setElements("名詞", "サ変接続"))) {
                return new AppendResult(surface, false);
            }
        }

        return new AppendResult((this.opt == null || !this.opt.disablePrefix ? "お" : "") + surface, true);
    }
}
