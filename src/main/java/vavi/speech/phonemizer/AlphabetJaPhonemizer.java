/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import vavi.util.Locales;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * This class does transliteration an alphabetic word.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/01 umjammer initial version <br>
 * @see "https://fastapi.metacpan.org/source/MASH/Lingua-JA-Yomi-0.01/lib/Lingua/JA"
 * @see "https://github.com/KEINOS/google-ime-user-dictionary-ja-en"
 */
@Locales(languages = "Japanese")
public class AlphabetJaPhonemizer implements JaPhonemizer {

    /** alphabet words -> Katakana dictionary */
    private static Properties dictionary = new Properties();

    static {
        try {
            dictionary.load(new InputStreamReader(AlphabetJaPhonemizer.class.getResourceAsStream("/bep-eng.dic")));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param text a word
     * @return the original text if a replacement is not found in the dictionary.
     */
    public String phoneme(String text) {
        String source = text.toUpperCase();
//System.err.println(source);
        String result = dictionary.getProperty(source);
        return result == null ? text : result;
    }
}
