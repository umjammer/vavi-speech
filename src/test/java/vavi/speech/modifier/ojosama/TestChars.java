/*
 * https://github.com/jiro4989/ojosama/blob/chars/chars_test.go
 */

package vavi.speech.modifier.ojosama;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static vavi.speech.modifier.ojosama.EQMark.MeaningType;
import static vavi.speech.modifier.ojosama.EQMark.Shuffler;
import static vavi.speech.modifier.ojosama.EQMark.StyleType;
import static vavi.speech.modifier.ojosama.EQMark.findExclamationQuestionByStyleAndMeaning;
import static vavi.speech.modifier.ojosama.EQMark.isExclamationQuestionMark;
import static vavi.speech.modifier.ojosama.EQMark.newExcl;
import static vavi.speech.modifier.ojosama.EQMark.newQues;
import static vavi.speech.modifier.ojosama.EQMark.sampleExclamationQuestionByValue;
import static vavi.speech.modifier.ojosama.UtilTest.getFinalStatic;
import static vavi.speech.modifier.ojosama.UtilTest.setFinalStatic;


class TestChars {

    static Shuffler original;

    @BeforeAll
    static void setup() throws Exception {
        original = ((Shuffler) getFinalStatic(EQMark.class.getDeclaredField("shuffler")));
    }

    @AfterEach
    void teardown() throws Exception {
        setFinalStatic(EQMark.class.getDeclaredField("shuffler"), original);
    }

    static Stream<Arguments> sourceIsExclamationQuestionMark() {
        return Stream.of(
                arguments("正常系: ！とはマッチいたしますわ", "！", true, newExcl("！", StyleType.FullWidth)),
                arguments("正常系: ❓とはマッチいたしますわ", "❓", true, newQues("❓", StyleType.Emoji)),
                arguments("正常系: 漆とはマッチいたしませんわ", "漆", false, null)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceIsExclamationQuestionMark")
    void testIsExclamationQuestionMark(String desc, String s, boolean wantOK, EQMark wantEQ) {
        EQMark got = isExclamationQuestionMark(s);
        assertEquals(wantOK, got != null);
        if (wantOK) {
            assertEquals(wantEQ, got);
        }
    }

    static Stream<Arguments> sourceSampleExclamationQuestionByValue() {
        return Stream.of(
                arguments("正常系: ！とはマッチいたしますわ", "！", 0, newExcl("！", StyleType.FullWidth), false),
                arguments("正常系: ❓とはマッチいたしますわ", "❓", 2, newQues("❓", StyleType.Emoji), false),
                arguments("正常系: 菫とはマッチいたしませんわ", "菫", 2, null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceSampleExclamationQuestionByValue")
    void testSampleExclamationQuestionByValue(String desc,
                                              String v,
                                              int pos,
                                              EQMark want,
                                              boolean wantNull) throws Exception {
        Shuffler mock = new OjosamaTest.TestShuffler(pos);
        setFinalStatic(EQMark.class.getDeclaredField("shuffler"), mock);
        EQMark got = sampleExclamationQuestionByValue(v);
        if (wantNull) {
            assertNull(got);
        } else {
            assertEquals(want, got);
        }
    }

    static Stream<Arguments> sourceFindExclamationQuestionByStyleAndMeaning() {
        return Stream.of(
                arguments("正常系: ❗を指定いたしますわ", StyleType.Emoji, MeaningType.Excl, newExcl("❗", StyleType.Emoji), false),
                arguments("正常系: ？を指定いたしますわ", StyleType.FullWidth, MeaningType.Ques, newQues("？", StyleType.FullWidth), false),
                arguments("正常系: 不明な要素の場合は何もお返しいたしませんわ", StyleType.Unknown, MeaningType.Excl, null, true),
                arguments("正常系: 不明な要素の場合は何もお返しいたしませんわ", StyleType.FullWidth, MeaningType.Unknown, null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceFindExclamationQuestionByStyleAndMeaning")
    void testFindExclamationQuestionByStyleAndMeaning(
            String desc,
            StyleType s,
            MeaningType m,
            EQMark want,
            boolean wantNull
    ) {
        EQMark got = findExclamationQuestionByStyleAndMeaning(s, m);
        if (wantNull) {
            assertNull(got);
        } else {
            assertEquals(want, got);
        }
    }
}