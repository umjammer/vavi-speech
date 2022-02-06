/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import vavi.util.CharNormalizerJa;


/**
 * KuromojiJaPhonemizer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
public class KuromojiJaPhonemizer implements JaPhonemizer {

    private Tokenizer tokenizer = new Tokenizer();

    private DigitJaPhonemizer digitConverter = new DigitJaPhonemizer();
    private AlphabetJaPhonemizer alphabetConverter = new AlphabetJaPhonemizer();

    @Override
    public String phoneme(String text) {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokenizer.tokenize(CharNormalizerJa.ToHalfDigit.normalize(text))) {
System.err.println(token.getSurface() + "\t("
           + token.getBaseForm() + ")" + "\t" + token.getAllFeaturesArray()[0] + "-" + token.getAllFeaturesArray()[1]
           + "(" + token.getPosition() + ")\t" + token.getReading() + "\t"
           + token.getPronunciation());
            if (token.getPronunciation().equals("*")) {
                if (token.getAllFeaturesArray()[1].equals("数")) {
                    sb.append(digitConverter.phoneme(token.getSurface()));
                } else if (token.getAllFeaturesArray()[1].equals("固有名詞") && isAlphabet(token.getSurface())) {
                    sb.append(alphabetConverter.phoneme(token.getSurface()));
                } else {
                    sb.append(token.getSurface());
                }
            } else {
                sb.append(token.getPronunciation());
            }
        }
System.err.println(sb);
        return sb.toString();
    }

    private static boolean isAlphabet(String str) {
        for (int i = 0;i < str.length(); i++) {
            int code = str.charAt(i);
            if (!Character.isAlphabetic(code)) {
                return false;
            }
        }
        return true;
    }
}

/* */
