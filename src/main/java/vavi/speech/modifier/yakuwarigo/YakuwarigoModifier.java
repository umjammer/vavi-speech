/*
 * https://github.com/jiro4989/ojosama/blob/main/ojosama.go
 */

package vavi.speech.modifier.yakuwarigo;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.regex.Pattern;

import net.java.sen.StringTagger;
import net.java.sen.Token;

import static java.lang.System.getLogger;
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

    private static final Logger logger = getLogger(YakuwarigoModifier.class.getName());

    /** Settings */
    public static class ConvertOption {
        /**
         * Turn off the feature that converts periods to "!"
         * Converting periods to "!" can destroy the nuance of the original sentence,
         * so this can be disabled with an optional parameter.
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
     * @param opt is an optional parameter for fine-tuning the behavior.
     *           If you do not need it, just pass null.
     */
    public YakuwarigoModifier(ConvertOption opt) throws IOException {
        this.opt = opt;
        this.provider = Provider.getProvider(opt != null && opt.name != null ? opt.name : "salome");
        this.rule = provider.getRule();

        this.tokenizer = StringTagger.getInstance(); // TODO OmitBosEos
    }

    /**
     * Convert converts the text into a tone similar to that of 壱百満天原サロメお嬢様 and returns it.
     * <p>
     * To put it simply, it converts "ハーブですわ！" into "おハーブですわ～～！！！"
     * There are several other variations as well.
     * <p>
     * Since elements are selected randomly during some conversions,
     * the caller must initialize the random number seed.
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

            // If the word is alphanumeric only, do nothing
            if (alnumRegexp.matcher(buf).matches()) {
                result.append(buf);
                continue;
            }

            // Converts combinations of noun + verb + sentence-final particle
            StringResult s = convertSentenceEndingParticle(p);
            if (s != null) {
                p = s.pos;
                result.append(s.str);
                continue;
            }

            // Performing transformations based on a series of conditions
            s = convertContinuousConditions(p);
            if (s != null) {
                p = s.pos;
                result.append(s.str);
                continue;
            }

            // Prioritize and ignore specific conditions
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
logger.log(Level.TRACE, "token[%d] result: %s".formatted(p, buf));
            result.append(buf);
        }
        return result.toString();
    }

    /**
     * convertSentenceEndingParticle converts sentences that satisfy all combinations of
     * noun + verb (+ auxiliary verb) + sentence-ending particle.
     * <p>
     * Final particles are used at the end of a sentence to complete the sentence while adding meanings
     * such as "希望" "禁止," "詠嘆," and "強意" to the sentence.
     * <p>
     * For example, "野球しようぜ" is broken down into "名詞：野球" "動詞：しよ" "助動詞：う" and "終助詞：ぜ"
     * <p>
     * As a sentence-final particle, "ぜ" has the connotation of "希望"
     * so it is converted into a young lady's word with a hopeful meaning.
     * Example: お野球をいたしませんこと
     * <p>
     * Another example is "野球するな" which becomes "お野球をしてはいけませんわ"
     */
    private StringResult convertSentenceEndingParticle(int tokenPos) {
        for (SentenceEndingParticleConvertRule r : rule.sentenceEndingParticleConvertRules) {
            StringBuilder result = new StringBuilder();
            int p = tokenPos;
            TokenData data = new TokenData(tokens[p]);

            // If the beginning matches, move to the next word
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
            // From the second onwards,
            // do not execute result.append(data.Surface) as it will be replaced with the value of value.

            // The second one matches any verb. If it doesn't match, we go back to the beginning.
            if (!data.matchAnyTokenData(r.conditions2)) {
                continue;
            }
            if (tokens.length <= p + 1) {
                continue;
            }
            p++;
            data = new TokenData(tokens[p]);

            // If there is an auxiliary verb, ignore it and proceed with the token.
            // It doesn't really have to be there.
            if (data.matchAllTokenData(r.auxiliaryVerb)) {
                if (tokens.length <= p + 1) {
                    continue;
                }
                p++;
                data = new TokenData(tokens[p]);
            }

            // Finally, determine which semantic category the final particle belongs to.
            MeaningType mt = getMeaningType(r.sentenceEndingParticle, data);
            if (mt == MeaningType.Unknown) {
                continue;
            }

            // Returns the conversion candidate string that matches the semantic classification
            // TODO Currently there is only one, so just return the first one.
            result.append(r.value.get(mt)[0]);
            return new StringResult(result.toString(), p);
        }
        return null;
    }

    /**
     * Returns the conversion result that matches the conversion rules based on a series of conditions.
     * <p>
     * For example, a result is returned when multiple tokens, such as "壱百満天原サロメ" or "横断歩道",
     * meet all the conditions that make up a single meaning only when they are consecutively placed in this order.
     * <p>
     * If consecutive conditions are matched, tokenPos must be advanced accordingly,
     * so the advanced tokenPos is returned.
     * <p>
     * The third argument returns whether the conversion rule matches.
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
     * Determine whether the tokens from position tokenPos in tokens match all the consecutive conditions.
     * <p>
     * If the next token does not exist or if any of the conditions are not met, false is returned.
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

    /** Determine if there is a match for the exclusion rule. */
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

    /** Perform basic conversions. */
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
logger.log(Level.TRACE, "skipped: " + c.value);
                continue;
            }

            // Ignore conversion based on preceding word
            if (beforeTokenOK && c.beforeIgnoreConditions != null && beforeToken.matchAnyTokenData(c.beforeIgnoreConditions)) {
logger.log(Level.DEBUG, "break cause before token");
                break;
            }

            // Look at the next word and ignore the conversion
            if (afterTokenOK && c.afterIgnoreConditions != null && afterToken.matchAnyTokenData(c.afterIgnoreConditions)) {
logger.log(Level.DEBUG, "break cause after token");
                break;
            }

            // It is only enabled at the end of a sentence or at the end of a sentence.
            // If the next token exists and is not a token that separates a sentence, no conversion is performed.
            if (c.enableWhenSentenceSeparation && afterTokenOK && !isSentenceSeparation(afterToken)) {
logger.log(Level.DEBUG, "break cause sentence termination");
                break;
            }

logger.log(Level.DEBUG, "select: " + c.value + " for surface: " + tokens[p].getSurface());
            return c;
        }
        return null;
    }

    /** isSentenceSeparation determines whether data is a token used to separate sentences. */
    private static boolean isSentenceSeparation(TokenData data) {
        return containsFeatures(new Feature[] {Feature.Kuten, Feature.Toten}, data.features) ||
                containsString(new String[] {"！", "!", "？", "?"}, data.surface);
    }
}
