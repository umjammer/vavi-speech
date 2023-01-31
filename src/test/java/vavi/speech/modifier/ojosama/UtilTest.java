/*
 * https://github.com/jiro4989/ojosama/blob/tokendata/util_test.go
 */

package vavi.speech.modifier.ojosama;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static vavi.speech.modifier.ojosama.Feature.containsFeatures;
import static vavi.speech.modifier.ojosama.Feature.containsString;
import static vavi.speech.modifier.ojosama.Feature.equalsFeatures;
import static vavi.speech.modifier.ojosama.Feature.Pos;
import static vavi.speech.modifier.ojosama.Feature.Feat;


class UtilTest {

    static void setSecurityLow(Field field) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

    /** modify a static final field */
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        setSecurityLow(field);
        field.set(null, newValue);
    }

    static Object getFinalStatic(Field field) throws Exception {
        setSecurityLow(field);
        return field.get(null);
    }

    static Stream<Arguments> sourceIsKuten() {
        return Stream.of(
                arguments(
                        "正常系: 句点の場合はtrueですわ",
                        new TokenData()
                                .setFeatures(Pos.Kuten)
                                .setSurface("。"),
                        true),
                arguments(
                        "正常系: 句点でない場合はfalseですわ",
                        new TokenData()
                                .setFeatures(Pos.Kuten)
                                .setSurface("、"),
                        false)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceIsKuten")
    void testIsKuten(
            String desc,
            TokenData data,
            boolean want
    ) {
        boolean got = data.isKuten();
        assertEquals(want, got);
    }

    static Stream<Arguments> sourceIsPoliteWord() {
        return Stream.of(
                arguments(
                        "正常系: 「オ」で始まることばは丁寧語ですわ",
                        new TokenData().setReading("オニギリ"),
                        true),
                arguments(
                        "正常系: 「オ」で始まっていない言葉は丁寧語ではありませんわ",
                        new TokenData().setReading("メカブ"),
                        false)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceIsPoliteWord")
    void testIsPoliteWord(
            String desc,
            TokenData data,
            boolean want
    ) {
        boolean got = data.isPoliteWord();
        assertEquals(want, got);
    }

    static Stream<Arguments> sourceEqualsFeatures() {
        return Stream.of(
                arguments(
                        "正常系: * 以降は無視されますわ",
                        new Feat().setElements("名詞", "代名詞", "一般", "*", "*"),
                        new Feat().setElements("名詞", "代名詞", "一般"),
                        true
                ),
                arguments(
                        "正常系: 途中までしかあっていない場合はfalseですわ",
                        new Feat().setElements("名詞", "代名詞", "一般", "*", "*"),
                        new Feat().setElements("名詞", "代名詞"),
                        false
                ),
                arguments(
                        "正常系: 1つでもずれてたらfalseですわ",
                        new Feat().setElements("名詞", "代名詞", "一般", "*", "*"),
                        new Feat().setElements("名詞", "寿司", "一般"),
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("sourceEqualsFeatures")
    void testEqualsFeatures(
            String desc,
            Feature a, Feature b,
            boolean want
    ) {
        boolean got = equalsFeatures(a, b);
        assertEquals(want, got);
    }

    static Stream<Arguments> sourceContainsFeatures() {
        return Stream.of(
                arguments(
                        "正常系: どれか1つと一致すればOKですわ",
                        new Feat[] {
                                new Feat().setElements("名詞", "代名詞"),
                                new Feat().setElements("名詞", "一般"),
                                new Feat().setElements("名詞", "固有名詞"),
                        },
                        new Feat().setElements("名詞", "一般", "*", "*", "*"),
                        true
                ),
                arguments(
                        "正常系: 1つも一致しなければfalseですわ",
                        new Feat[] {
                                new Feat().setElements("名詞", "代名詞"),
                                new Feat().setElements("名詞", "固有名詞"),
                        },
                        new Feat().setElements("名詞", "一般", "*", "*", "*"),
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("sourceContainsFeatures")
    void testContainsFeatures(
            String desc,
            Feature[] a,
            Feature b,
            boolean want
    ) {
        boolean got = containsFeatures(a, b);
        assertEquals(want, got);
    }

    static Stream<Arguments> sourceContainsString() {
        return Stream.of(
                arguments(
                        "正常系: どれか1つと一致すればOKですわ",
                        new String[] {"a", "b"},
                        "b",
                        true),
                arguments(
                        "正常系: 1つも一致しなければfalseですわ",
                        new String[] {"a", "b"},
                        "c",
                        false
                ));
    }

    @ParameterizedTest
    @MethodSource("sourceContainsString")
    void testContainsString(
            String desc,
            String[] a,
            String b,
            boolean want
    ) {
        boolean got = containsString(a, b);
        assertEquals(want, got);
    }
}