/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phoneme;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import vavi.speech.Phonemer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * JaPhonemerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/01 umjammer initial version <br>
 */
class JaPhonemerTest {

    // TODO make results same
    static Stream<Arguments> phonemerProvider() {
        return Stream.of(
            arguments(new vavi.speech.phoneme.SenJaPhonemer(), "キョーワ。キョーワテンキガヨイデスネ。タビエデヨウトオモイマス。"),
            arguments(new vavi.speech.phoneme.KuromojiJaPhonemer(), "キョーワ。キョーワテンキガヨイデスネ。タビエデヨウトオモイマス。"),
            arguments(new vavi.speech.phoneme.GooFuriganaJaPhonemer(), "コンニチハ。 キョウハ テンキガ ヨイデスネ。 タビヘ デヨウト オモイマス。"),
            arguments(new vavi.speech.phoneme.YahooJapanJaPhonemer(), "きょうは。きょうはてんきがよいですね。たびへでようとおもいます。")
        );
    }

    @ParameterizedTest
    @MethodSource("phonemerProvider")
    void test(Phonemer phonemer, String actual) throws Exception {
Debug.println("-------------------------- " + phonemer.getClass().getSimpleName() + " --------------------------");
        String text = phonemer.phoneme("今日は。今日は天気が良いですね。旅へ出ようと思います。");
Debug.println("result: " + text);
        assertEquals(actual, text);
    }

    static Stream<Arguments> phonemerProvider1() {
        return Stream.of(
            arguments(new vavi.speech.phoneme.SudachiJaPhonemer(), "キョウワ。キョウワテンキガヨイデスネ。タビエデヨウトオモイマス。")
        );
    }

    @ParameterizedTest
    @MethodSource("phonemerProvider1")
    void test1(Phonemer phonemer, String actual) throws Exception {
Debug.println("-------------------------- " + phonemer.getClass().getSimpleName() + " --------------------------");
        String text = phonemer.phoneme("今日は。今日は天気が良いですね。旅へ出ようと思います。");
Debug.println("result: " + text);
        assertEquals(actual, text);
    }

    @Test
    void test2() {
        Phonemer phonemer = new vavi.speech.phoneme.KuromojiJaPhonemer();
        String text = phonemer.phoneme("Amazon か Yahoo どっちでお買い物しようかな？");
Debug.println(text);
        assertEquals("Amazon カ ヤフー ドッチデオカイモノシヨウカナ？", text);
    }
}

/* */
