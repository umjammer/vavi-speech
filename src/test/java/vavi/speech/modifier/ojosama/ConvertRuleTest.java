/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_rule_test.go
 */

package vavi.speech.modifier.ojosama;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vavi.speech.modifier.ojosama.ConvertRule.MeaningType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static vavi.speech.modifier.ojosama.ConvertRule.getMeaningType;
import static vavi.speech.modifier.ojosama.Util.Feature;


class ConvertRuleTest {

    static Stream<Arguments> sourceGetMeaningType() {
        return Stream.of(
                arguments(
                        "正常系: 一致したmeaningTypeを返却いたしますわ",
                        new HashMap<MeaningType, ConvertCondition[]>() {{
                            put(MeaningType.Coercion,
                                    new ConvertCondition[] {
                                            new ConvertCondition()
                                                    .setFeatures(new Feature("名詞"))
                                                    .setReading("a"),
                                            new ConvertCondition()
                                                    .setFeatures(new Feature("名詞"))
                                                    .setReading("b"),
                                    });
                            put(MeaningType.Hope,
                                    new ConvertCondition[] {
                                            new ConvertCondition()
                                                    .setFeatures(new Feature("名詞"))
                                                    .setReading("c"),
                                    });
                        }},
                        new TokenData()
                                .setFeatures(new Feature("名詞"))
                                .setReading("b"),
                        MeaningType.Coercion,
                        true),
                arguments(
                        "正常系: いずれとも一致しない場合はunknownですわ",
                        new HashMap<MeaningType, ConvertCondition[]>() {{
                            put(MeaningType.Coercion,
                                    new ConvertCondition[] {
                                            new ConvertCondition()
                                                    .setFeatures(new Feature("名詞"))
                                                    .setReading("z"),
                                            new ConvertCondition()
                                                    .setFeatures(new Feature("名詞"))
                                                    .setReading("z"),
                                    });
                        }},
                        new TokenData()
                                .setFeatures(new Feature("名詞"))
                                .setReading("b"),
                        MeaningType.Unknown,
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
        assertEquals(wantOK, gotMT != MeaningType.Unknown);
    }
}