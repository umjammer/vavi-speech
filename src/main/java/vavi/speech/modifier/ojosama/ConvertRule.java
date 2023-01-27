/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_rule.go
 */

package vavi.speech.modifier.ojosama;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static vavi.speech.modifier.ojosama.ConvertCondition.newCond;
import static vavi.speech.modifier.ojosama.ConvertCondition.newCondAuxiliaryVerb;
import static vavi.speech.modifier.ojosama.ConvertCondition.newCondRe;
import static vavi.speech.modifier.ojosama.ConvertCondition.newCondSentenceEndingParticle;
import static vavi.speech.modifier.ojosama.ConvertCondition.newConds;
import static vavi.speech.modifier.ojosama.Util.AdjectivesSelfSupporting;
import static vavi.speech.modifier.ojosama.Util.VerbNotIndependence;


/**
 * ConvertRule は 単独のTokenに対して、Conditionsがすべてマッチしたときに変換するルール。
 * <p>
 * 基本的な変換はこの型で定義する。
 */
public class ConvertRule {

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

    // builders

    public ConvertRule setConditions(ConvertCondition[] conditions) {
        this.conditions = conditions;
        return this;
    }

    public ConvertRule setBeforeIgnoreConditions(ConvertCondition[] beforeIgnoreConditions) {
        this.beforeIgnoreConditions = beforeIgnoreConditions;
        return this;
    }

    public ConvertRule setAfterIgnoreConditions(ConvertCondition[] afterIgnoreConditions) {
        this.afterIgnoreConditions = afterIgnoreConditions;
        return this;
    }

    public ConvertRule setEnableWhenSentenceSeparation(boolean enableWhenSentenceSeparation) {
        this.enableWhenSentenceSeparation = enableWhenSentenceSeparation;
        return this;
    }

    public ConvertRule setAppendLongNote(boolean appendLongNote) {
        this.appendLongNote = appendLongNote;
        return this;
    }

    public ConvertRule setDisablePrefix(boolean disablePrefix) {
        this.disablePrefix = disablePrefix;
        return this;
    }

    public ConvertRule setEnableKutenToExclamation(boolean enableKutenToExclamation) {
        this.enableKutenToExclamation = enableKutenToExclamation;
        return this;
    }

    public ConvertRule setValue(String value) {
        this.value = value;
        return this;
    }

    // convenient instanciators

    private static ConvertRule newRule(String[] features, String surface, String value) {
        return new ConvertRule()
                .setConditions(new ConvertCondition[] {
                        newCond(features, surface)
                })
                .setValue(value);
    }

    private static ConvertRule newRulePronounGeneral(String surface, String value) {
        return newRule(Util.PronounGeneral, surface, value);
    }

    private static ConvertRule newRuleNounsGeneral(String surface, String value) {
        return newRule(Util.NounsGeneral, surface, value);
    }

    private static ConvertRule newRuleAdnominalAdjective(String surface, String value) {
        return newRule(Util.AdnominalAdjective, surface, value);
    }

    private static ConvertRule newRuleAdjectivesSelfSupporting(String surface, String value) {
        return newRule(AdjectivesSelfSupporting, surface, value);
    }

    private static ConvertRule newRuleInterjection(String surface, String value) {
        return newRule(Util.Interjection, surface, value);
    }

    /** ContinuousConditionsConvertRule は連続する条件がすべてマッチしたときに変換するルール。 */
    public static class ContinuousConditionsConvertRule {
        ConvertCondition[] conditions;
        boolean appendLongNote;
        boolean enableKutenToExclamation;
        String value;

        // builders

        public ContinuousConditionsConvertRule setConditions(ConvertCondition[] conditions) {
            this.conditions = conditions;
            return this;
        }

        public ContinuousConditionsConvertRule setAppendLongNote(boolean appendLongNote) {
            this.appendLongNote = appendLongNote;
            return this;
        }

        public ContinuousConditionsConvertRule setEnableKutenToExclamation(boolean enableKutenToExclamation) {
            this.enableKutenToExclamation = enableKutenToExclamation;
            return this;
        }

        public ContinuousConditionsConvertRule setValue(String value) {
            this.value = value;
            return this;
        }
    }

    /** SentenceEndingParticleConvertRule は「名詞」＋「動詞」＋「終助詞」の組み合わせによる変換ルール。 */
    public static class SentenceEndingParticleConvertRule {
        /** 一番最初に評価されるルール */
        ConvertCondition[] conditions1;
        /** 二番目に評価されるルール */
        ConvertCondition[] conditions2;
        /** 助動詞。マッチしなくても次にすすむ */
        ConvertCondition[] auxiliaryVerb;
        /** 終助詞 */
        Map<MeaningType, ConvertCondition[]> sentenceEndingParticle;
        Map<MeaningType, String[]> value;

        public SentenceEndingParticleConvertRule(ConvertCondition[] conditions1,
                                                 ConvertCondition[] conditions2,
                                                 HashMap<MeaningType, ConvertCondition[]> sentenceEndingParticle,
                                                 ConvertCondition[] auxiliaryVerb,
                                                 HashMap<MeaningType, String[]> value) {
            this.conditions1 = conditions1;
            this.conditions2 = conditions2;
            this.auxiliaryVerb = auxiliaryVerb;
            this.sentenceEndingParticle = sentenceEndingParticle;
            this.value = value;
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

    public static final SentenceEndingParticleConvertRule[] SentenceEndingParticleConvertRules = new SentenceEndingParticleConvertRule[] {
            new SentenceEndingParticleConvertRule(
                    new ConvertCondition[] {
                            new ConvertCondition().setFeatures(Util.NounsGeneral),
                            new ConvertCondition().setFeatures(Util.NounsSaDynamic),
                    },
                    new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Util.VerbIndependence)
                                    .setBaseForm("する"),
                            new ConvertCondition()
                                    .setFeatures(Util.VerbIndependence)
                                    .setBaseForm("やる"),
                    },
                    new HashMap<MeaningType, ConvertCondition[]>() {{
                        put(MeaningType.Hope,
                                new ConvertCondition[] {
                                        newCondSentenceEndingParticle("ぜ"),
                                        newCondSentenceEndingParticle("よ"),
                                        newCondSentenceEndingParticle("べ"),
                                });
                        put(MeaningType.Poem,
                                new ConvertCondition[] {
                                        // これだけ特殊
                                        newCond(new String[] {"助詞", "副助詞／並立助詞／終助詞"}, "か"),
                                });
                        put(MeaningType.Prohibition,
                                new ConvertCondition[] {
                                        newCondSentenceEndingParticle("な"),
                                });

                        put(MeaningType.Coercion,
                                new ConvertCondition[] {
                                        newCondSentenceEndingParticle("ぞ"),
                                        newCondSentenceEndingParticle("の"),
                                });
                    }},
                    new ConvertCondition[] {
                            newCondAuxiliaryVerb("う"),
                    },
                    new HashMap<MeaningType, String[]>() {{
                        put(MeaningType.Hope, new String[] {"をいたしませんこと",});
                        put(MeaningType.Poem, new String[] {"をいたしますわ",});
                        put(MeaningType.Prohibition, new String[] {"をしてはいけませんわ",});
                        put(MeaningType.Coercion, new String[] {"をいたしますわよ",});
                    }}
            )
    };

    /** */
    private static final ConvertCondition condNounsGeneral = new ConvertCondition().setFeatures(
            Util.NounsGeneral
    );

    /** */
    private static final ConvertCondition condPronounsGeneral = new ConvertCondition().setFeatures(
            Util.PronounGeneral
    );

    /**
     * continuousConditionsConvertRules は連続する条件がすべてマッチしたときに変換するルール。
     * <p>
     * 例えば「壱百満天原サロメ」や「横断歩道」のように、複数のTokenがこの順序で連続
     * して初めて1つの意味になるような条件を定義する。
     */
    public static final ContinuousConditionsConvertRule[] ContinuousConditionsConvertRules = new ContinuousConditionsConvertRule[] {
            new ContinuousConditionsConvertRule()
                    .setValue("壱百満天原サロメ")
                    .setConditions(newConds(new String[] {"壱", "百", "満天", "原", "サロメ"})
            ),
            new ContinuousConditionsConvertRule()
                    .setValue("壱百満天原")
                    .setConditions(newConds(new String[] {"壱", "百", "満天", "原"})
            ),
            new ContinuousConditionsConvertRule()
                    .setValue("壱百満点")
                    .setConditions(newConds(new String[] {"壱", "百", "満点"})
            ),
            new ContinuousConditionsConvertRule()
                    .setValue("いたしますわ")
                    .setAppendLongNote(true)
                    .setConditions(new ConvertCondition[] {
                            newCond(new String[] {"動詞", "自立"}, "し"),
                            newCond(new String[] {"助動詞"}, "ます"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("ですので")
                    .setConditions(new ConvertCondition[] {
                            newCond(new String[] {"助動詞"}, "だ"),
                            newCond(new String[] {"助詞", "接続助詞"}, "から"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの")
                    .setConditions(new ConvertCondition[] {
                            newCond(new String[] {"助動詞"}, "な"),
                            newCond(new String[] {"名詞", "非自立", "一般"}, "ん"),
                            newCond(new String[] {"助動詞"}, "だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("ですわ")
                    .setConditions(new ConvertCondition[] {
                            newCond(new String[] {"助動詞"}, "だ"),
                            newCond(new String[] {"助詞", "終助詞"}, "よ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの").setConditions(
                            new ConvertCondition[] {
                                    newCond(Util.PronounGeneral, "なん"),
                                    newCond(Util.SubPostpositionalParticle, "じゃ"),
                            })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの")
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.PronounGeneral, "なん"),
                            newCond(Util.AuxiliaryVerb, "だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの")
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.PronounGeneral, "なん"),
                            newCond(Util.AssistantParallelParticle, "や"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            condNounsGeneral,
                            newCond(Util.AuxiliaryVerb, "じゃ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            condNounsGeneral,
                            newCond(Util.AuxiliaryVerb, "だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            condNounsGeneral,
                            newCond(Util.AuxiliaryVerb, "や"),
                    })
                    .setEnableKutenToExclamation(
                    true
            ),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            condPronounsGeneral,
                            newCond(Util.AuxiliaryVerb, "じゃ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            condPronounsGeneral,
                            newCond(Util.AuxiliaryVerb, "だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            condPronounsGeneral,
                            newCond(Util.AuxiliaryVerb, "や"),
                    })
                    .setEnableKutenToExclamation(true),
            // 名詞＋した＋終助詞は文の終わり
            new ContinuousConditionsConvertRule()
                    .setValue("@1をいたしましたわ")
                    .setConditions(new ConvertCondition[] {
                            condNounsGeneral,
                            newCond(Util.VerbIndependence, "し"),
                            newCond(Util.AuxiliaryVerb, "た"),
                            new ConvertCondition().setFeatures(Util.SentenceEndingParticle),
                    })
                    .setEnableKutenToExclamation(true),
            // 名詞＋やる＋終助詞は文の終わり
            new ContinuousConditionsConvertRule()
                    .setValue("@1をいたしましたわ")
                    .setConditions(new ConvertCondition[] {
                            condNounsGeneral,
                            newCond(Util.VerbIndependence, "やっ"),
                            newCond(Util.AuxiliaryVerb, "た"),
                            new ConvertCondition().setFeatures(Util.SentenceEndingParticle),
                    })
                    .setEnableKutenToExclamation(true),
    };

    /**
     * ExcludeRules は変換処理を無視するルール。
     * このルールは ConvertRules よりも優先して評価される。
     */
    public static ConvertRule[] ExcludeRules = new ConvertRule[] {
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                    newCond(Util.SpecificGeneral, "カス"),
            }),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                    newCondRe(Util.NounsGeneral, Pattern.compile("^(ー+|～+)$")),
            }),
    };

    /**
     * ConvertRules は 単独のTokenに対して、Conditionsがすべてマ ッチしたときに変換するルール。
     * <p>
     * 基本的な変換はここに定義する。
     */
    public static ConvertRule[] ConvertRules = new ConvertRule[] {
            // 一人称
            newRulePronounGeneral("俺", "私"),
            newRulePronounGeneral("オレ", "ワタクシ"),
            newRulePronounGeneral("おれ", "わたくし"),
            newRulePronounGeneral("僕", "私"),
            newRulePronounGeneral("ボク", "ワタクシ"),
            newRulePronounGeneral("ぼく", "わたくし"),
            newRulePronounGeneral("あたし", "わたくし"),
            newRulePronounGeneral("わたし", "わたくし"),

            // 二人称
            newRulePronounGeneral("あなた", "貴方"),
            newRulePronounGeneral("あんた", "貴方"),
            newRulePronounGeneral("おまえ", "貴方"),
            newRulePronounGeneral("お前", "貴方"),
            newRulePronounGeneral("てめぇ", "貴方"),
            newRulePronounGeneral("てめえ", "貴方"),
            newRuleNounsGeneral("貴様", "貴方").setDisablePrefix(true),
            // newRulePronounGeneral("きさま", "貴方"),
            // newRulePronounGeneral("そなた", "貴方"),
            newRulePronounGeneral("君", "貴方"),

            // 三人称
            // TODO: AfterIgnore系も簡単に定義できるようにしたい
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.NounsGeneral, "パパ"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setSurface("上"),
                    })
                    .setValue("パパ上"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.NounsGeneral, "ママ"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setSurface("上"),
                    })
                    .setValue("ママ上"),
            newRulePronounGeneral("皆", "皆様方"),
            newRuleNounsGeneral("皆様", "皆様方").setDisablePrefix(true),

            // こそあど言葉
            newRulePronounGeneral("これ", "こちら"),
            newRulePronounGeneral("それ", "そちら"),
            newRulePronounGeneral("あれ", "あちら"),
            newRulePronounGeneral("どれ", "どちら"),
            newRuleAdnominalAdjective("この", "こちらの"),
            newRuleAdnominalAdjective("その", "そちらの"),
            newRuleAdnominalAdjective("あの", "あちらの"),
            newRuleAdnominalAdjective("どの", "どちらの"),
            newRulePronounGeneral("ここ", "こちら"),
            newRulePronounGeneral("そこ", "そちら"),
            newRulePronounGeneral("あそこ", "あちら"),
            newRulePronounGeneral("どこ", "どちら"),
            newRuleAdnominalAdjective("こんな", "このような"),
            newRuleAdnominalAdjective("そんな", "そのような"),
            newRuleAdnominalAdjective("あんな", "あのような"),
            newRuleAdnominalAdjective("どんな", "どのような"),

            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.NotIndependenceGeneral, "もん"),
                    })
                    .setValue("もの"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.AuxiliaryVerb, "です"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setFeatures(Util.SubParEndParticle),
                    })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ですわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.AuxiliaryVerb, "だ"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setFeatures(Util.SubParEndParticle),
                    })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ですわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.VerbIndependence, "する"),
                    })
                    .setEnableWhenSentenceSeparation(true)
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("いたしますわ"),
            new ConvertRule().setConditions(
                            new ConvertCondition[] {
                                    newCond(Util.VerbIndependence, "なる"),
                            })
                    .setEnableWhenSentenceSeparation(true)
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("なりますわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.VerbIndependence, "ある"),
                    })
                    .setValue("あります"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.SubPostpositionalParticle, "じゃ"),
                    })
                    .setValue("では"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.SubParEndParticle, "か"),
                    })
                    .setValue("の"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.SentenceEndingParticle, "わ"),
                    })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ですわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.SentenceEndingParticle, "な")
                    })
                    .setValue("ね"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.SentenceEndingParticle, "さ")
                    })
                    .setValue(""),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.ConnAssistant, "から")
                    })
                    .setValue("ので"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.ConnAssistant, "けど")
                    })
                    .setValue("けれど"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.ConnAssistant, "し")
                    })
                    .setValue("ですし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.AuxiliaryVerb, "まし"),
                    })
                    .setBeforeIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setFeatures(Util.VerbIndependence),
                    })
                    .setValue("おりまし"),
            new ConvertRule()
                    .setConditions(
                            new ConvertCondition[] {
                                    newCond(Util.AuxiliaryVerb, "ます"),
                            })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ますわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.AuxiliaryVerb, "た"),
                    })
                    .setEnableWhenSentenceSeparation(true)
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("たわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.AuxiliaryVerb, "だろ"),
                    })
                    .setValue("でしょう"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(Util.AuxiliaryVerb, "ない"),
                    })
                    .setBeforeIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setFeatures(Util.VerbIndependence),
                    })
                    .setValue("ありません"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(VerbNotIndependence, "ください"),
                    })
                    .setEnableKutenToExclamation(true)
                    .setValue("くださいまし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(VerbNotIndependence, "くれ"),
                    })
                    .setEnableKutenToExclamation(true)
                    .setValue("くださいまし"),
            newRuleInterjection("ありがとう", "ありがとうございますわ"),
            newRuleInterjection("じゃぁ", "それでは"),
            newRuleInterjection("じゃあ", "それでは"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            newCond(VerbNotIndependence, "くれる"),
                    })
                    .setValue("くれます"),
            newRuleAdjectivesSelfSupporting("汚い", "きったねぇ"),
            newRuleAdjectivesSelfSupporting("きたない", "きったねぇ"),
            newRuleAdjectivesSelfSupporting("臭い", "くっせぇ"),
            newRuleAdjectivesSelfSupporting("くさい", "くっせぇ"),
            newRuleInterjection("うふ", "おほ"),
            newRuleInterjection("うふふ", "おほほ"),
            newRuleInterjection("う", "お"),
            newRuleInterjection("ふふふ", "ほほほ"),

            // 形容詞文。形容詞で文が終わる時に変換する
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition().setFeatures(AdjectivesSelfSupporting)
                    })
                    .setEnableWhenSentenceSeparation(true)
                    .setEnableKutenToExclamation(true)
                    .setAppendLongNote(true)
                    .setValue("@1ですわ")
    };

    public static MeaningType getMeaningType(Map<MeaningType, ConvertCondition[]> typeMap, TokenData data) {
        Optional<Map.Entry<MeaningType, ConvertCondition[]>> e = typeMap.entrySet().stream()
                .filter(m -> data.matchAnyTokenData(m.getValue())).findFirst();
        if (e.isPresent()) {
            return e.get().getKey();
        } else {
            return MeaningType.Unknown;
        }
    }
}