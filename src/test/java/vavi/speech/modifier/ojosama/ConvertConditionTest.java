/*
 * https://github.com/jiro4989/ojosama/blob/converter/convert_condition_test.go
 */

package vavi.speech.modifier.ojosama;


import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static vavi.speech.modifier.ojosama.Util.Feature;


class ConvertConditionTest {

    static Stream<Arguments> sourceEqualsTokenData() {
        return Stream.of(
                arguments(
                        "正常系: 複数の条件が定義されている場合ANDで評価いたしますわ",
                        new ConvertCondition()
                                .setFeatures(new Feature("名詞"))
                                .setReading("a")
                                .setSurface("b")
                                .setBaseForm("c")
                                .setReadingRe(Pattern.compile("a+"))
                                .setSurfaceRe(Pattern.compile("b+"))
                                .setBaseFormRe(Pattern.compile("c+")),
                        new TokenData()
                                .setFeatures(new Feature("名詞"))
                                .setReading("a")
                                .setSurface("b")
                                .setBaseForm("c"),
                        true),
                arguments(
                        "正常系: Featuresが存在して、且つ不一致な場合は false ですわ",
                        new ConvertCondition().setFeatures(new Feature("名詞")),
                        new TokenData().setFeatures(new Feature("動詞")),
                        false),
                arguments(
                        "正常系: Surfaceが存在して、且つ不一致な場合は false ですわ",
                        new ConvertCondition().setSurface("a"),
                        new TokenData().setSurface("b"),
                        false),
                arguments(
                        "正常系: Readingが存在して、且つ不一致な場合は false ですわ",
                        new ConvertCondition().setReading("a"),
                        new TokenData().setReading("b"),
                        false),
                arguments(
                        "正常系: BaseFormが存在して、且つ不一致な場合は false ですわ",
                        new ConvertCondition().setBaseForm("a"),
                        new TokenData().setBaseForm("b"),
                        false),
                arguments(
                        "正常系: SurfaceReが存在して、且つ不一致な場合は false ですわ",
                        new ConvertCondition().setSurfaceRe(Pattern.compile("a+")),
                        new TokenData().setSurface("b"),
                        false),
                arguments(
                        "正常系: ReadingReが存在して、且つ不一致な場合は false ですわ",
                        new ConvertCondition().setReadingRe(Pattern.compile("a+")),
                        new TokenData().setReading("b"),
                        false),
                arguments(
                        "正常系: BaseFormReが存在して、且つ不一致な場合は false ですわ",
                        new ConvertCondition().setBaseFormRe(Pattern.compile("a+")),
                        new TokenData().setBaseForm("b"),
                        false)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceEqualsTokenData")
    void testEqualsTokenData(
            String desc,
            ConvertCondition c,
            TokenData data,
            boolean want
    ) {
        boolean got = c.equalsTokenData(data);
        assertEquals(want, got);
    }

    static Stream<Arguments> sourceMatchAllTokenData() {
        return Stream.of(
                arguments(
                        "正常系: すべての評価がtrueの場合にtrueを返しますわ",
                        new ConvertCondition[] {
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setReading("a"),
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setSurface("b"),
                        },
                        new TokenData()
                                .setFeatures(new Feature("名詞"))
                                .setReading("a")
                                .setSurface("b")
                                .setBaseForm("c"),
                        true),
                arguments(
                        "正常系: 一つで不一致の場合はfalseですわ",
                        new ConvertCondition[] {
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setReading("a"),
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setSurface("c"),
                        },
                        new TokenData()
                                .setFeatures(new Feature("名詞"))
                                .setReading("a")
                                .setSurface("b")
                                .setBaseForm("c"),
                        false)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceMatchAllTokenData")
    void testMatchAllTokenData(
            String desc,
            ConvertCondition[] c,
            TokenData data,
            boolean want
    ) {
        boolean got = data.matchAllTokenData(c);
        assertEquals(want, got);
    }

    static Stream<Arguments> sourceMatchAnyTokenData() {
        return Stream.of(
                arguments(
                        "正常系: どれか1つがtrueになればtrueですわ",
                        new ConvertCondition[] {
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setReading("z"),
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setSurface("b"),
                        },
                        new TokenData()
                                .setFeatures(new Feature("名詞"))
                                .setReading("a")
                                .setSurface("b")
                                .setBaseForm("c"),
                        true),
                arguments(
                        "正常系: すべて不一致の場合はfalseですわ",
                        new ConvertCondition[] {
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setReading("z"),
                                new ConvertCondition()
                                        .setFeatures(new Feature("名詞"))
                                        .setSurface("z"),
                        },
                        new TokenData()
                                .setFeatures(new Feature("名詞"))
                                .setReading("a")
                                .setSurface("b")
                                .setBaseForm("c"),
                        false)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceMatchAnyTokenData")
    void testMatchAnyTokenData(
            String desc,
            ConvertCondition[] c,
            TokenData data,
            boolean want) {
        boolean got = data.matchAnyTokenData(c);
        assertEquals(want, got);
    }
}