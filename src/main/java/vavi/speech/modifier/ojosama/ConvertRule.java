/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_rule.go
 */

package vavi.speech.modifier.ojosama;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static vavi.speech.modifier.ojosama.Util.Feature;
import static vavi.speech.modifier.ojosama.Util.Pos;


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
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsSaDynamic),
                    },
                    new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbIndependence)
                                    .setBaseForm("する"),
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbIndependence)
                                    .setBaseForm("やる"),
                    },
                    new HashMap<MeaningType, ConvertCondition[]>() {{
                        put(MeaningType.Hope,
                                new ConvertCondition[] {
                                        new ConvertCondition()
                                                .setFeatures(Pos.SentenceEndingParticle)
                                                .setSurface("ぜ"),
                                        new ConvertCondition()
                                                .setFeatures(Pos.SentenceEndingParticle)
                                                .setSurface("よ"),
                                        new ConvertCondition()
                                                .setFeatures(Pos.SentenceEndingParticle)
                                                .setSurface("べ"),
                                });
                        put(MeaningType.Poem,
                                new ConvertCondition[] {
                                        // これだけ特殊
                                        new ConvertCondition()
                                                .setFeatures(new Feature("助詞", "副助詞／並立助詞／終助詞"))
                                                .setSurface("か"),
                                });
                        put(MeaningType.Prohibition,
                                new ConvertCondition[] {
                                        new ConvertCondition()
                                                .setFeatures(Pos.SentenceEndingParticle)
                                                .setSurface("な"),
                                });

                        put(MeaningType.Coercion,
                                new ConvertCondition[] {
                                        new ConvertCondition()
                                                .setFeatures(Pos.SentenceEndingParticle)
                                                .setSurface("ぞ"),
                                        new ConvertCondition()
                                                .setFeatures(Pos.SentenceEndingParticle)
                                                .setSurface("の"),
                                });
                    }},
                    new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("う"),
                    },
                    new HashMap<MeaningType, String[]>() {{
                        put(MeaningType.Hope, new String[] {"をいたしませんこと",});
                        put(MeaningType.Poem, new String[] {"をいたしますわ",});
                        put(MeaningType.Prohibition, new String[] {"をしてはいけませんわ",});
                        put(MeaningType.Coercion, new String[] {"をいたしますわよ",});
                    }}
            )
    };

    /**
     * continuousConditionsConvertRules は連続する条件がすべてマッチしたときに変換するルール。
     * <p>
     * 例えば「壱百満天原サロメ」や「横断歩道」のように、複数のTokenがこの順序で連続
     * して初めて1つの意味になるような条件を定義する。
     */
    public static final ContinuousConditionsConvertRule[] ContinuousConditionsConvertRules = new ContinuousConditionsConvertRule[] {
            new ContinuousConditionsConvertRule()
                    .setValue("壱百満天原サロメ")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setSurface("壱"),
                            new ConvertCondition()
                                    .setSurface("百"),
                            new ConvertCondition()
                                    .setSurface("満天"),
                            new ConvertCondition()
                                    .setSurface("原"),
                            new ConvertCondition()
                                    .setSurface("サロメ"),
                    }),
            new ContinuousConditionsConvertRule()
                    .setValue("壱百満天原")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setSurface("壱"),
                            new ConvertCondition()
                                    .setSurface("百"),
                            new ConvertCondition()
                                    .setSurface("満天"),
                            new ConvertCondition()
                                    .setSurface("原"),
                    }),
            new ContinuousConditionsConvertRule()
                    .setValue("壱百満点")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setSurface("壱"),
                            new ConvertCondition()
                                    .setSurface("百"),
                            new ConvertCondition()
                                    .setSurface("満点"),
                    }),
            new ContinuousConditionsConvertRule()
                    .setValue("いたしますわ")
                    .setAppendLongNote(true)
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(new Feature("動詞", "自立"))
                                    .setSurface("し"),
                            new ConvertCondition()
                                    .setFeatures(new Feature("助動詞"))
                                    .setSurface("ます"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("ですので")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(new Feature("助動詞"))
                                    .setSurface("だ"),
                            new ConvertCondition()
                                    .setFeatures(new Feature("助詞", "接続助詞"))
                                    .setSurface("から"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(new Feature("助動詞"))
                                    .setSurface("な"),
                            new ConvertCondition()
                                    .setFeatures(new Feature("名詞", "非自立", "一般"))
                                    .setSurface("ん"),
                            new ConvertCondition()
                                    .setFeatures(new Feature("助動詞"))
                                    .setSurface("だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("ですわ")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(new Feature("助動詞"))
                                    .setSurface("だ"),
                            new ConvertCondition()
                                    .setFeatures(new Feature("助詞", "終助詞"))
                                    .setSurface("よ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの").setConditions(
                            new ConvertCondition[] {
                                    new ConvertCondition()
                                            .setFeatures(Pos.PronounGeneral)
                                            .setSurface("なん"),
                                    new ConvertCondition()
                                            .setFeatures(Pos.SubPostpositionalParticle)
                                            .setSurface("じゃ"),
                            })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("なん"),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("なんですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("なん"),
                            new ConvertCondition()
                                    .setFeatures(Pos.AssistantParallelParticle)
                                    .setSurface("や"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("じゃ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("や"),
                    })
                    .setEnableKutenToExclamation(
                    true
            ),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("じゃ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("だ"),
                    })
                    .setEnableKutenToExclamation(true),
            new ContinuousConditionsConvertRule()
                    .setValue("@1ですの")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("や"),
                    })
                    .setEnableKutenToExclamation(true),
            // 名詞＋した＋終助詞は文の終わり
            new ContinuousConditionsConvertRule()
                    .setValue("@1をいたしましたわ")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbIndependence)
                                    .setSurface("し"),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("た"),
                            new ConvertCondition().setFeatures(Pos.SentenceEndingParticle),
                    })
                    .setEnableKutenToExclamation(true),
            // 名詞＋やる＋終助詞は文の終わり
            new ContinuousConditionsConvertRule()
                    .setValue("@1をいたしましたわ")
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral),
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbIndependence)
                                    .setSurface("やっ"),
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("た"),
                            new ConvertCondition().setFeatures(Pos.SentenceEndingParticle),
                    })
                    .setEnableKutenToExclamation(true),
    };

    /**
     * ExcludeRules は変換処理を無視するルール。
     * このルールは ConvertRules よりも優先して評価される。
     */
    public static final ConvertRule[] ExcludeRules;

    static {
        Pattern surfaceRe = Pattern.compile("^(ー+|～+)$");
        ExcludeRules = new ConvertRule[] {
                new ConvertRule()
                        .setConditions(new ConvertCondition[] {
                        new ConvertCondition()
                                .setFeatures(Pos.SpecificGeneral)
                                .setSurface("カス"),
                }),
                new ConvertRule()
                        .setConditions(new ConvertCondition[] {
                        new ConvertCondition()
                                .setFeatures(Pos.NounsGeneral)
                                .setSurfaceRe(surfaceRe),
                }),
        };
    }

    /**
     * ConvertRules は 単独のTokenに対して、Conditionsがすべてマ ッチしたときに変換するルール。
     * <p>
     * 基本的な変換はここに定義する。
     */
    public static ConvertRule[] ConvertRules = new ConvertRule[] {
            // 一人称
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("俺")
                    })
                    .setValue("私"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("オレ")
                    })
                    .setValue("ワタクシ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("おれ")
                    })
                    .setValue("わたくし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("僕")
                    })
                    .setValue("私"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("ボク")
                    })
                    .setValue("ワタクシ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("ぼく")
                    })
                    .setValue("わたくし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("あたし")
                    })
                    .setValue("わたくし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("わたし")
                    })
                    .setValue("わたくし"),

            // 二人称
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("あなた")
                    })
                    .setValue("貴方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("あんた")
                    })
                    .setValue("貴方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("おまえ")
                    })
                    .setValue("貴方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("お前")
                    })
                    .setValue("貴方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("てめぇ")
                    })
                    .setValue("貴方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("てめえ")
                    })
                    .setValue("貴方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral)
                                    .setSurface("貴様")
                    })
                    .setValue("貴方").setDisablePrefix(true),
            // newRulePronounGeneral("きさま", "貴方"),
            // newRulePronounGeneral("そなた", "貴方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("君")
                    })
                    .setValue("貴方"),

            // 三人称
            // TODO: AfterIgnore系も簡単に定義できるようにしたい
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral)
                                    .setSurface("パパ"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setSurface("上"),
                    })
                    .setValue("パパ上"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral)
                                    .setSurface("ママ"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setSurface("上"),
                    })
                    .setValue("ママ上"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("皆")
                    })
                    .setValue("皆様方"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NounsGeneral)
                                    .setSurface("皆様")
                    })
                    .setValue("皆様方").setDisablePrefix(true),

            // こそあど言葉
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("これ")
                    })
                    .setValue("こちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("それ")
                    })
                    .setValue("そちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("あれ")
                    })
                    .setValue("あちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("どれ")
                    })
                    .setValue("どちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("この")
                    })
                    .setValue("こちらの"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("その")
                    })
                    .setValue("そちらの"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("あの")
                    })
                    .setValue("あちらの"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("どの")
                    })
                    .setValue("どちらの"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("ここ")
                    })
                    .setValue("こちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("そこ")
                    })
                    .setValue("そちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("あそこ")
                    })
                    .setValue("あちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.PronounGeneral)
                                    .setSurface("どこ")
                    })
                    .setValue("どちら"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("こんな")
                    })
                    .setValue("このような"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("そんな")
                    })
                    .setValue("そのような"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("あんな")
                    })
                    .setValue("あのような"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdnominalAdjective)
                                    .setSurface("どんな")
                    })
                    .setValue("どのような"),

            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.NotIndependenceGeneral)
                                    .setSurface("もん"),
                    })
                    .setValue("もの"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("です"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.SubParEndParticle),
                    })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ですわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("だ"),
                    })
                    .setAfterIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.SubParEndParticle),
                    })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ですわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbIndependence)
                                    .setSurface("する"),
                    })
                    .setEnableWhenSentenceSeparation(true)
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("いたしますわ"),
            new ConvertRule().setConditions(
                            new ConvertCondition[] {
                                    new ConvertCondition()
                                            .setFeatures(Pos.VerbIndependence)
                                            .setSurface("なる"),
                            })
                    .setEnableWhenSentenceSeparation(true)
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("なりますわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbIndependence)
                                    .setSurface("ある"),
                    })
                    .setValue("あります"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.SubPostpositionalParticle)
                                    .setSurface("じゃ"),
                    })
                    .setValue("では"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.SubParEndParticle)
                                    .setSurface("か"),
                    })
                    .setValue("の"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.SentenceEndingParticle)
                                    .setSurface("わ"),
                    })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ですわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.SentenceEndingParticle)
                                    .setSurface("な")
                    })
                    .setValue("ね"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.SentenceEndingParticle)
                                    .setSurface("さ")
                    })
                    .setValue(""),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.ConnAssistant)
                                    .setSurface("から")
                    })
                    .setValue("ので"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.ConnAssistant)
                                    .setSurface("けど")
                    })
                    .setValue("けれど"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.ConnAssistant)
                                    .setSurface("し")
                    })
                    .setValue("ですし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("まし"),
                    })
                    .setBeforeIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setFeatures(Pos.VerbIndependence),
                    })
                    .setValue("おりまし"),
            new ConvertRule()
                    .setConditions(
                            new ConvertCondition[] {
                                    new ConvertCondition()
                                            .setFeatures(Pos.AuxiliaryVerb)
                                            .setSurface("ます"),
                            })
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("ますわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("た"),
                    })
                    .setEnableWhenSentenceSeparation(true)
                    .setAppendLongNote(true)
                    .setEnableKutenToExclamation(true)
                    .setValue("たわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("だろ"),
                    })
                    .setValue("でしょう"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AuxiliaryVerb)
                                    .setSurface("ない"),
                    })
                    .setBeforeIgnoreConditions(new ConvertCondition[] {
                            new ConvertCondition().setFeatures(Pos.VerbIndependence),
                    })
                    .setValue("ありません"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbNotIndependence)
                                    .setSurface("ください"),
                    })
                    .setEnableKutenToExclamation(true)
                    .setValue("くださいまし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbNotIndependence)
                                    .setSurface("くれ"),
                    })
                    .setEnableKutenToExclamation(true)
                    .setValue("くださいまし"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.Interjection)
                                    .setSurface("ありがとう")
                    })
                    .setValue("ありがとうございますわ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.Interjection)
                                    .setSurface("じゃぁ")
                    })
                    .setValue("それでは"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.Interjection)
                                    .setSurface("じゃあ")
                    })
                    .setValue("それでは"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.VerbNotIndependence)
                                    .setSurface("くれる"),
                    })
                    .setValue("くれます"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdjectivesSelfSupporting)
                                    .setSurface("汚い")
                    })
                    .setValue("きったねぇ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdjectivesSelfSupporting)
                                    .setSurface("きたない")
                    })
                    .setValue("きったねぇ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdjectivesSelfSupporting)
                                    .setSurface("臭い")
                    })
                    .setValue("くっせぇ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdjectivesSelfSupporting)
                                    .setSurface("くさい")
                    })
                    .setValue("くっせぇ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.Interjection)
                                    .setSurface("うふ")
                    })
                    .setValue("おほ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.Interjection)
                                    .setSurface("うふふ")
                    })
                    .setValue("おほほ"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.Interjection)
                                    .setSurface("う")
                    })
                    .setValue("お"),
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.Interjection)
                                    .setSurface("ふふふ")
                    })
                    .setValue("ほほほ"),

            // 形容詞文。形容詞で文が終わる時に変換する
            new ConvertRule()
                    .setConditions(new ConvertCondition[] {
                            new ConvertCondition()
                                    .setFeatures(Pos.AdjectivesSelfSupporting)
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