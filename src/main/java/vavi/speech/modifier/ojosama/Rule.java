/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.ojosama;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


/**
 * Rule represents deserialized json object.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-01-31 nsano initial version <br>
 */
public class Rule {

    /**
     * ConvertRule は 単独のTokenに対して、Conditionsがすべてマッチしたときに変換するルール。
     * <p>
     * 基本的な変換はこの型で定義する。
     */
    public static class ConvertRule {

        boolean disabled;
        /** 起点になる変換条件 */
        ConvertCondition[] conditions;
        /** 前のTokenで条件にマッチした場合は無視する */
        ConvertCondition[] beforeIgnoreConditions;
        /** 次のTokenで条件にマッチした場合は無視する */
        ConvertCondition[] afterIgnoreConditions;
        /** 文の区切り（単語の後に句点か読点がくる、あるいは何もない）場合だけ有効にする */
        boolean enableWhenSentenceSeparation;
        /** 波線を追加する */
        boolean appendLongNote;
        /** 「お」を手前に付与しない */
        boolean disablePrefix;
        /** 直後に句点が来たとき確率で！に変換する */
        boolean enableKutenToExclamation;
        /** この文字列に置換する */
        String value;
        @Override public String toString() {
            return "ConvertRule{" +
                    "disabled=" + disabled +
                    ", conditions=" + Arrays.toString(conditions) +
                    ", beforeIgnoreConditions=" + Arrays.toString(beforeIgnoreConditions) +
                    ", afterIgnoreConditions=" + Arrays.toString(afterIgnoreConditions) +
                    ", enableWhenSentenceSeparation=" + enableWhenSentenceSeparation +
                    ", appendLongNote=" + appendLongNote +
                    ", disablePrefix=" + disablePrefix +
                    ", enableKutenToExclamation=" + enableKutenToExclamation +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    /** ContinuousConditionsConvertRule は連続する条件がすべてマッチしたときに変換するルール。 */
    public static class ContinuousConditionsConvertRule {
        String value;
        boolean appendLongNote;
        ConvertCondition[] conditions;
        boolean enableKutenToExclamation;
        @Override public String toString() {
            return "ContinuousConditionsConvertRule{" +
                    "conditions=" + Arrays.toString(conditions) +
                    ", appendLongNote=" + appendLongNote +
                    ", enableKutenToExclamation=" + enableKutenToExclamation +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    /** SentenceEndingParticleConvertRule は「名詞」＋「動詞」＋「終助詞」の組み合わせによる変換ルール。 */
    public static class SentenceEndingParticleConvertRule {

        public static MeaningType getMeaningType(Map<MeaningType, ConvertCondition[]> typeMap, TokenData data) {
            Optional<Map.Entry<MeaningType, ConvertCondition[]>> e = typeMap.entrySet().stream()
                    .filter(m -> data.matchAnyTokenData(m.getValue())).findFirst();
            if (e.isPresent()) {
                return e.get().getKey();
            } else {
                return MeaningType.Unknown;
            }
        }

        /** MeaningType は言葉の意味分類。 */
        public enum MeaningType {
            Unknown,
            /** 希望 */
            Hope,
            /** 詠嘆 */
            Poem,
            /** 禁止 */
            Prohibition,
            /** 強制 */
            Coercion
        }

        /** 一番最初に評価されるルール */
        ConvertCondition[] conditions1;
        /** 二番目に評価されるルール */
        ConvertCondition[] conditions2;
        /** 終助詞 */
        Map<MeaningType, ConvertCondition[]> sentenceEndingParticle;
        /** 助動詞。マッチしなくても次にすすむ */
        ConvertCondition[] auxiliaryVerb;
        Map<MeaningType, String[]> value;

        @Override public String toString() {
            return "SentenceEndingParticleConvertRule{" +
                    "conditions1=" + Arrays.toString(conditions1) +
                    ", conditions2=" + Arrays.toString(conditions2) +
                    ", auxiliaryVerb=" + Arrays.toString(auxiliaryVerb) +
                    ", sentenceEndingParticle=" + sentenceEndingParticle +
                    ", value=" + value +
                    '}';
        }
    }

    public SentenceEndingParticleConvertRule[] sentenceEndingParticleConvertRules;

    /**
     * continuousConditionsConvertRules は連続する条件がすべてマッチしたときに変換するルール。
     * <p>
     * 例えば「壱百満天原サロメ」や「横断歩道」のように、複数のTokenがこの順序で連続
     * して初めて1つの意味になるような条件を定義する。
     */
    public ContinuousConditionsConvertRule[] continuousConditionsConvertRules;

    /**
     * ConvertRules は 単独のTokenに対して、Conditionsがすべてマ ッチしたときに変換するルール。
     * <p>
     * 基本的な変換はここに定義する。
     */
    public ConvertRule[] convertRules;

    /**
     * ExcludeRules は変換処理を無視するルール。
     * このルールは ConvertRules よりも優先して評価される。
     */
    public ConvertRule[] excludeRules;
}
