/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import vavi.speech.Phonemizer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * JaPhonemizerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/01 umjammer initial version <br>
 */
class JaPhonemizerTest {

    // TODO make results same
    static Stream<Arguments> phonemizerProvider() {
        return Stream.of(
            arguments(new vavi.speech.phonemizer.KuromojiJaPhonemizer(), "キョーワ。キョーワテンキガヨイデスネ。タビエデヨウトオモイマス。"),
            arguments(new vavi.speech.phonemizer.GooFuriganaJaPhonemizer(), "コンニチハ。 キョウハ テンキガ ヨイデスネ。 タビヘ デヨウト オモイマス。"),
            arguments(new vavi.speech.phonemizer.YahooJapanJaPhonemizer(), "きょうは。きょうはてんきがよいですね。たびへでようとおもいます。")
        );
    }

    @EnabledIfEnvironmentVariable(named = "YAHOOJAPAN_API_KEY", matches = ".*")
    @EnabledIfEnvironmentVariable(named = "DOCOMO_GOO_HIRAGANA_API_KEY", matches = ".*")
    @ParameterizedTest
    @MethodSource("phonemizerProvider")
    void test(Phonemizer phonemizer, String actual) throws Exception {
Debug.println("-------------------------- " + phonemizer.getClass().getSimpleName() + " --------------------------");
        String text = phonemizer.phoneme("今日は。今日は天気が良いですね。旅へ出ようと思います。");
Debug.println("result: " + text);
        assertEquals(actual, text);
    }

    // for w/ outer dictionary
    static Stream<Arguments> phonemizerProvider1() {
        return Stream.of(
            arguments(new vavi.speech.phonemizer.SenJaPhonemizer(), "キョーワ。キョーワテンキガヨイデスネ。タビエデヨウトオモイマス。"),
            arguments(new vavi.speech.phonemizer.SudachiJaPhonemizer(), "キョウワ。キョウワテンキガヨイデスネ。タビエデヨウトオモイマス。")
        );
    }

    @EnabledIfSystemProperty(named = "sen.home", matches = ".*")
    @ParameterizedTest
    @MethodSource("phonemizerProvider1")
    void test1(Phonemizer phonemizer, String actual) throws Exception {
Debug.println("-------------------------- " + phonemizer.getClass().getSimpleName() + " --------------------------");
        String text = phonemizer.phoneme("今日は。今日は天気が良いですね。旅へ出ようと思います。");
Debug.println("result: " + text);
        assertEquals(actual, text);
    }

    @Test
    void test2() {
        Phonemizer phonemizer = new vavi.speech.phonemizer.KuromojiJaPhonemizer();
        String text = phonemizer.phoneme("Amazon か Yahoo どっちでお買い物しようかな？");
Debug.println(text);
        assertEquals("Amazon カ ヤフー ドッチデオカイモノシヨウカナ？", text);
    }
}
