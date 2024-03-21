/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.io.IOException;
import java.util.logging.Level;

import net.java.sen.StringTagger;
import net.java.sen.Token;
import vavi.util.Debug;
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
@Locales(languages = "Japanese")
public class SenJaPhonemizer implements JaPhonemizer {

    private DigitJaPhonemizer converter = new DigitJaPhonemizer();

    @Override
    public String phoneme(String text) {
        StringBuilder sb = new StringBuilder();
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] token = tagger.analyze(text);
            if (token != null) {
                for (Token value : token) {
if (Debug.isLoggable(Level.FINE)) {
 System.err.println(value.toString() + "\t("
            + value.getBasicString() + ")" + "\t" + value.getPos()
            + "(" + value.start() + "," + value.end() + ","
            + value.length() + ")\t" + value.getReading() + "\t"
            + value.getPronunciation());
}
                    if (value.getReading() != null) {
                        sb.append(value.getPronunciation());
                    } else {
                        sb.append(value.getBasicString());
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
if (Debug.isLoggable(Level.FINE)) {
 System.err.println(sb);
}
        return converter.convertFrom(sb.toString());
    }
}
