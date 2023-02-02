/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_rule_test.go
 */

package vavi.speech.modifier.yakuwarigo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule.MeaningType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static vavi.speech.modifier.yakuwarigo.Feature.Feat;
import static vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule.getMeaningType;


class ConvertRuleTest {

    static Stream<Arguments> sourceGetMeaningType() {
        return Stream.of(
                arguments(
                        "正常系: 一致したmeaningTypeを返却いたしますわ",
                        new HashMap<MeaningType, ConvertCondition[]>() {{
                            put(Rule.SentenceEndingParticleConvertRule.MeaningType.Coercion,
                                    new ConvertCondition[] {
                                            new ConvertCondition() {{
                                                    feature = new Feat().setElements("名詞");
                                                    reading = "a";
                                            }},
                                            new ConvertCondition() {{
                                                    feature = new Feat().setElements("名詞");
                                                    reading = "b";
                                            }}
                                    });
                            put(Rule.SentenceEndingParticleConvertRule.MeaningType.Hope,
                                    new ConvertCondition[] {
                                            new ConvertCondition() {{
                                                    feature = new Feat().setElements("名詞");
                                                    reading = "c";
                                            }}
                                    });
                        }},
                        new TokenData()
                                .setFeatures(new Feat().setElements("名詞"))
                                .setReading("b"),
                        Rule.SentenceEndingParticleConvertRule.MeaningType.Coercion,
                        true),
                arguments(
                        "正常系: いずれとも一致しない場合はunknownですわ",
                        new HashMap<MeaningType, ConvertCondition[]>() {{
                            put(Rule.SentenceEndingParticleConvertRule.MeaningType.Coercion,
                                    new ConvertCondition[] {
                                            new ConvertCondition() {{
                                                    feature = new Feat().setElements("名詞");
                                                    reading = "z";
                                            }},
                                            new ConvertCondition() {{
                                                    feature = new Feat().setElements("名詞");
                                                    reading = "z";
                                            }}
                                    });
                        }},
                        new TokenData()
                                .setFeatures(new Feat().setElements("名詞"))
                                .setReading("b"),
                        Rule.SentenceEndingParticleConvertRule.MeaningType.Unknown,
                        false)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceGetMeaningType")
    void testGetMeaningType(
            String desc,
            Map<MeaningType, ConvertCondition[]> typeMap,
            TokenData data,
            MeaningType wantMT,
            boolean wantOK) {

        MeaningType gotMT = getMeaningType(typeMap, data);
        assertEquals(wantMT, gotMT);
        assertEquals(wantOK, gotMT != Rule.SentenceEndingParticleConvertRule.MeaningType.Unknown);
    }
}