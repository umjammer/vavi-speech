/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phoneme;

import java.io.IOException;

import vavi.speech.Phonemer;

import net.java.sen.StringTagger;
import net.java.sen.Token;


/**
 * SenJaPhonemer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
public class SenJaPhonemer implements Phonemer {

    // TODO disable omit 記号
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
                    if (token[i].getReading() != null && !token[i].getPos().startsWith("記号")) {
                        sb.append(token[i].getPronunciation());
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
System.err.println(sb);
        return sb.toString();
    }
}

/* */
