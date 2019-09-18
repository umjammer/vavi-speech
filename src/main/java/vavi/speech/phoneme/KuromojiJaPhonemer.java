/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phoneme;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import vavi.speech.Phonemer;


/**
 * KuromojiJaPhonemer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
public class KuromojiJaPhonemer implements Phonemer {

    private Tokenizer tokenizer = new Tokenizer();

    @Override
    public String phoneme(String text) {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokenizer.tokenize(text)) {
System.err.println(token.getSurface() + "\t("
           + token.getBaseForm() + ")" + "\t" + token.getAllFeaturesArray()[0] + "-" + token.getAllFeaturesArray()[1]
           + "(" + token.getPosition() + ")\t" + token.getReading() + "\t"
           + token.getPronunciation());
            sb.append(token.getPronunciation());
        }
System.err.println(sb);
        return sb.toString();
    }
}

/* */
