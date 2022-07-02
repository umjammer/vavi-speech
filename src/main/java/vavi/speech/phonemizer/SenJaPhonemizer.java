/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.io.IOException;

import net.java.sen.StringTagger;
import net.java.sen.Token;
import vavi.util.Locales;


/**
 * SenJaPhonemizer.
 * <p>
 * system property
 * <ul>
 * <li> "sen.home" ... a directory includes a sen dictionary and settings
 * </ul>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
@Locales(countries = "Japan", languages = "Japanese")
public class SenJaPhonemizer implements JaPhonemizer {

    private DigitJaPhonemizer converter = new DigitJaPhonemizer();

    @Override
    public String phoneme(String text) {
        StringBuilder sb = new StringBuilder();
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] token = tagger.analyze(text);
            if (token != null) {
                for (int i = 0; i < token.length; i++) {
System.err.println(token[i].toString() + "\t("
               + token[i].getBasicString() + ")" + "\t" + token[i].getPos()
               + "(" + token[i].start() + "," + token[i].end() + ","
               + token[i].length() + ")\t" + token[i].getReading() + "\t"
               + token[i].getPronunciation());
                    if (token[i].getReading() != null) {
                        sb.append(token[i].getPronunciation());
                    } else {
                        sb.append(token[i].getBasicString());
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
System.err.println(sb);
        return converter.convertFrom(sb.toString());
    }
}

/* */
