/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phoneme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * DigitKanaConverterTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/28 umjammer initial version <br>
 */
class DigitKanaConverterTest {

    @Test
    void test() {
        DigitJaPhonemer converter = new DigitJaPhonemer();
        assertEquals("さのいちおくにせんさんびゃくよんじゅうごまんろくせんななひゃくはちじゅうきゅうなおいちおくにせんさんびゃくよんじゅうごまんろくせんななひゃくはちじゅうきゅうひで", converter.convertFrom("さの123456789なお123456789ひで"));
        assertEquals("いちおくにせんさんびゃくよんじゅうごまんろくせんななひゃくはちじゅうきゅう", converter.phoneme("123456789"));
        assertEquals("いちおくにせんさんびゃくよんじゅうごまんろくせんななひゃくはちじゅうきゅう", converter.phoneme("123,456,789"));
        assertEquals("じゅうにまんさんぜんよんひゃくごじゅうろくてんななはちきゅう", converter.phoneme("123,456.789"));
        assertEquals("じゅうにまんさんぜんよんひゃくごじゅうろく", converter.phoneme("123,456."));
        assertEquals("はっせんはっぴゃくはちじゅうはちふかしぎはっせんはっぴゃくはちじゅうはちなゆたはっせんはっぴゃくはちじゅうはちあそうぎはっせんはっぴゃくはちじゅうはちごうがしゃはっせんはっぴゃくはちじゅうはちごくはっせんはっぴゃくはちじゅうはちさいはっせんはっぴゃくはちじゅうはちせいはっせんはっぴゃくはちじゅうはちかんはっせんはっぴゃくはちじゅうはちこうはっせんはっぴゃくはちじゅうはちじょうはっせんはっぴゃくはちじゅうはちじょはっせんはっぴゃくはちじゅうはちがいはっせんはっぴゃくはちじゅうはっけいはっせんはっぴゃくはちじゅうはっちょうはっせんはっぴゃくはちじゅうはちおくはっせんはっぴゃくはちじゅうはちまんはっせんはっぴゃくはちじゅうはち", converter.phoneme("88888888888888888888888888888888888888888888888888888888888888888888"));
    }
}

/* */
