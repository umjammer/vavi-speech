/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo;

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
     * ConvertRule is a rule that converts when all Conditions match for a single Token.
     * <p>
     * Basic conversions are defined in this type.
     */
    public static class ConvertRule {

        boolean disabled;
        /** Conversion conditions that serve as the starting point */
        ConvertCondition[] conditions;
        /** Ignore if the previous Token matches the condition */
        ConvertCondition[] beforeIgnoreConditions;
        /** Ignore if the next Token matches the condition */
        ConvertCondition[] afterIgnoreConditions;
        /** Enable only when there is a sentence separator (a period or comma follows the word, or there is nothing) */
        boolean enableWhenSentenceSeparation;
        /** class#method */
        public String extraRule3;
        /** class#method */
        public String extraRule;
        /** boolean */
        public String extraRule2;
        /** Replace with this string */
        public String value;
        @Override public String toString() {
            return "ConvertRule{" +
                    "disabled=" + disabled +
                    ", conditions=" + Arrays.toString(conditions) +
                    ", beforeIgnoreConditions=" + Arrays.toString(beforeIgnoreConditions) +
                    ", afterIgnoreConditions=" + Arrays.toString(afterIgnoreConditions) +
                    ", enableWhenSentenceSeparation=" + enableWhenSentenceSeparation +
                    ", extraRule3=" + extraRule3 +
                    ", extraRule=" + extraRule +
                    ", extraRule2=" + extraRule2 +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    /** ContinuousConditionsConvertRule is a rule that converts when all consecutive conditions match. */
    public static class ContinuousConditionsConvertRule {
        public String value;
        /** class#method */
        public String extraRule3;
        public ConvertCondition[] conditions;
        /** class#method */
        String extraRule;
        @Override public String toString() {
            return "ContinuousConditionsConvertRule{" +
                    "conditions=" + Arrays.toString(conditions) +
                    ", extraRule3=" + extraRule3 +
                    ", extraRule=" + extraRule +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    /** SentenceEndingParticleConvertRule is a conversion rule based on the combination of "Noun" + "Verb" + "Sentence-ending particle". */
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

        /** MeaningType is the semantic classification of words. */
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

        /** Rule evaluated first */
        ConvertCondition[] conditions1;
        /** Rule evaluated second */
        ConvertCondition[] conditions2;
        /** Sentence-ending particle */
        Map<MeaningType, ConvertCondition[]> sentenceEndingParticle;
        /** Auxiliary verb. Proceed to the next even if it doesn't match */
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
     * continuousConditionsConvertRules are rules that convert when all consecutive conditions match.
     * <p>
     * For example, define conditions where multiple Tokens in this order form a single meaning,
     * such as "壱百満天原サロメ" or "横断歩道".
     */
    public ContinuousConditionsConvertRule[] continuousConditionsConvertRules;

    /**
     * ConvertRules are rules that convert when all Conditions match for a single Token.
     * <p>
     * Basic conversions are defined here.
     */
    public ConvertRule[] convertRules;

    /**
     * ExcludeRules are rules to ignore conversion processing.
     * This rule is evaluated with higher priority than ConvertRules.
     */
    public ConvertRule[] excludeRules;
}
