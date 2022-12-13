/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import vavi.util.Locales;


/**
 * SudachiJaPhonemizer.
 * <p>
 * system property
 * <ul>
 * <li> "sudachi.dir" ... a directory includes a sudachi dictionary and settings
 * </ul>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/27 umjammer initial version <br>
 */
@Locales(countries = "Japan", languages = "Japanese")
public class SudachiJaPhonemizer implements JaPhonemizer {

    private final Tokenizer tokenizer;

    public SudachiJaPhonemizer() {
        try {
            Config config = Config.fromClasspath("sudachi.json");
            try (Dictionary dict = new DictionaryFactory().create(config)) {
                tokenizer = dict.create();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String phoneme(String text) {
        StringBuilder sb = new StringBuilder();
        for (Morpheme m : tokenizer.tokenize(Tokenizer.SplitMode.C, text)) {
System.err.println(m.surface() + "\t" + m.partOfSpeech() +
                 "\t" + m.normalizedForm() +
                 "\t" + m.dictionaryForm() +
                 "\t" + m.readingForm() +
                 "\t" + m.getDictionaryId());
            if (m.partOfSpeech().get(0).equals("助詞")) {
                if (m.surface().equals("は")) {
                    sb.append("ワ");
                } else if (m.surface().equals("へ")) {
                    sb.append("エ");
                } else {
                    sb.append(m.readingForm());
                }
            } else {
                sb.append(m.readingForm());
            }
        }
System.err.println(sb);
        return sb.toString();
    }
}

/* */
