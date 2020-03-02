/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phoneme;

import org.junit.jupiter.api.Test;

import vavi.speech.Phonemer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JaPhonemerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/01 umjammer initial version <br>
 */
class JaPhonemerTest {

//    @Test
    void test() {
//        Phonemer phonemer = new vavi.speech.phoneme.SenJaPhonemer();
//        Phonemer phonemer = new vavi.speech.phoneme.KuromojiJaPhonemer();
        Phonemer phonemer = new vavi.speech.phoneme.SudachiJaPhonemer();
        String text = phonemer.phoneme("今日は。今日は天気が良いですね。旅へ出ようと思います。");
System.err.println(text);
        assertEquals("キョウワ。キョウワテンキガヨイデスネ。タビエデヨウトオモイマス。", text);
    }

    @Test
    void test2() {
        Phonemer phonemer = new vavi.speech.phoneme.KuromojiJaPhonemer();
        String text = phonemer.phoneme("Amazon か Yahoo どっちでお買い物しようかな？");
        System.err.println(text);
    }
}

/* */
