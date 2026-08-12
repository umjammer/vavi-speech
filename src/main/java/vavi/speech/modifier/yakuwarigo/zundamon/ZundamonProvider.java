/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo.zundamon;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vavi.speech.modifier.yakuwarigo.Provider;
import vavi.speech.modifier.yakuwarigo.TokenData;
import vavi.speech.modifier.yakuwarigo.YakuwarigoModifier;

import static vavi.speech.modifier.yakuwarigo.Rule.ContinuousConditionsConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.ConvertRule;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConversionResult;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.StringResult;


/**
 * ZundamonProvider.
 * <p>
 * ずんだもん ends almost every sentence with "～のだ" / "～なのだ", and never speaks 丁寧語.
 * Dropping 丁寧語 means putting a verb back into its plain form, which a plain string in
 * the rule json cannot express, so the placeholders below are resolved here instead.
 * <ul>
 *  <li>{@code @n} ... surface of the n-th token of the matched sequence (1 origin)</li>
 *  <li>{@code @nb} ... its 基本形 (e.g. 行き -&gt; 行く, しよ -&gt; する)</li>
 *  <li>{@code @nt} ... its 過去形 (e.g. 読み -&gt; 読んだ, 書き -&gt; 書いた)</li>
 *  <li>{@code @nn} ... its 未然形, to be followed by ない etc. (e.g. 行き -&gt; 行か)</li>
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-04-23 nsano initial version <br>
 */
public class ZundamonProvider implements Provider {

    /** {@code @<index>[b|t|n]} */
    private static final Pattern placeholder = Pattern.compile("@(\\d+)([btn]?)");

    /** 活用型 -> { suffix of the 基本形 to be replaced, 過去形, 未然形 } */
    private static final Map<String, String[]> conjugations = Map.ofEntries(
            Map.entry("一段", new String[] {"る", "た", ""}),
            Map.entry("一段・得ル", new String[] {"る", "た", ""}),
            Map.entry("一段・クレル", new String[] {"る", "た", ""}),
            Map.entry("カ変・来ル", new String[] {"る", "た", ""}),
            Map.entry("カ変・クル", new String[] {"くる", "きた", "こ"}),
            Map.entry("サ変・スル", new String[] {"する", "した", "し"}),
            Map.entry("サ変・−スル", new String[] {"する", "した", "し"}),
            Map.entry("サ変・−ズル", new String[] {"ずる", "じた", "じ"}),
            Map.entry("五段・カ行イ音便", new String[] {"く", "いた", "か"}),
            Map.entry("五段・カ行促音便", new String[] {"く", "った", "か"}),
            Map.entry("五段・カ行促音便ユク", new String[] {"く", "った", "か"}),
            Map.entry("五段・ガ行", new String[] {"ぐ", "いだ", "が"}),
            Map.entry("五段・サ行", new String[] {"す", "した", "さ"}),
            Map.entry("五段・タ行", new String[] {"つ", "った", "た"}),
            Map.entry("五段・ナ行", new String[] {"ぬ", "んだ", "な"}),
            Map.entry("五段・バ行", new String[] {"ぶ", "んだ", "ば"}),
            Map.entry("五段・マ行", new String[] {"む", "んだ", "ま"}),
            Map.entry("五段・ラ行", new String[] {"る", "った", "ら"}),
            Map.entry("五段・ラ行特殊", new String[] {"る", "った", "ら"}),
            Map.entry("五段・ラ行アル", new String[] {"る", "った", "ら"}),
            Map.entry("五段・ワ行促音便", new String[] {"う", "った", "わ"}),
            Map.entry("五段・ワ行ウ音便", new String[] {"う", "うた", "わ"}),
            Map.entry("形容詞・アウオ段", new String[] {"い", "かった", "く"}),
            Map.entry("形容詞・イ段", new String[] {"い", "かった", "く"}));

    /** */
    private YakuwarigoModifier context;

    @Override
    public String getName() {
        return "zundamon";
    }

    @Override
    public void setContext(YakuwarigoModifier context) {
        this.context = context;
    }

    @Override
    public StringResult convert(ContinuousConditionsConvertRule mc, int tokenPos) {
        int p = tokenPos + mc.conditions.length - 1;
        return new StringResult(expand(mc.value, tokenPos, mc.conditions.length), p);
    }

    @Override
    public ConversionResult convert(ConvertRule c, TokenData data, int p, String surface, boolean nounKeep) {
        if (c == null) {
            return new ConversionResult(surface, false, p, null);
        }
        return new ConversionResult(expand(c.value, p, 1), false, p, c.extraRule);
    }

    @Override
    public String convert(TokenData data) {
        return data.surface;
    }

    //----

    /** Replaces every placeholder in value by the token it points at. */
    private String expand(String value, int tokenPos, int length) {
        Matcher matcher = placeholder.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            int i = tokenPos + Integer.parseInt(matcher.group(1)) - 1;
            String replacement;
            if (i < tokenPos || tokenPos + length <= i || context.tokens.length <= i) {
                // out of the matched sequence, leave it as it is
                replacement = matcher.group();
            } else {
                TokenData data = new TokenData(context.tokens[i]);
                replacement = switch (matcher.group(2)) {
                    case "b" -> baseForm(data);
                    case "t" -> inflect(data, 1);
                    case "n" -> inflect(data, 2);
                    default -> data.surface;
                };
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 基本形, falls back to the surface when the dictionary doesn't tell it. */
    private static String baseForm(TokenData data) {
        String baseForm = data.getBaseForm();
        return baseForm == null || baseForm.isEmpty() || baseForm.equals("*") ? data.surface : baseForm;
    }

    /**
     * Inflects the 基本形 of data.
     *
     * @param form index of {@link #conjugations} value, 1: 過去形, 2: 未然形
     */
    private static String inflect(TokenData data, int form) {
        String baseForm = baseForm(data);
        String[] elements = data.features.elements();
        String[] conjugation = elements.length > 4 ? conjugations.get(elements[4]) : null;
        if (conjugation == null || !baseForm.endsWith(conjugation[0])) {
            // an unknown 活用型 (or an already inflected 基本形): the surface is the best guess
            return form == 1 ? data.surface + "た" : data.surface;
        }
        return baseForm.substring(0, baseForm.length() - conjugation[0].length()) + conjugation[form];
    }
}
