/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import vavi.speech.Phonemizer;
import vavi.util.Locales;


/**
 * JaPhonemizer.
 *
 * * param text consists of Kana, Kanji, numbers, alphabet
 * * return text consists of Kana
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/01/25 umjammer initial version <br>
 */
@Locales(countries = "Japan", languages = "Japanese")
public interface JaPhonemizer extends Phonemizer {

}

/* */
