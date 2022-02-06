/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phoneme;

import org.junit.jupiter.api.Test;

import vavi.speech.Phonemizer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.*;

class RococoaPhonemerTest {

    @Test
    void test() {
        Phonemizer phonemizer = new vavi.speech.phonemizer.RococoaJaPhonemizer();
        String text = phonemizer.phoneme("Amazon か Yahoo どっちでお買い物しようかな？");
Debug.println(text);
        assertEquals("あまぞんかやふうどっちでおかいものしようかな?", text);
        text = phonemizer.phoneme("一つ２つ三つ");
Debug.println(text);
    }
}

/* */
