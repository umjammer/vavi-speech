/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Level;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import vavi.util.Debug;
import vavi.util.Locales;


/**
 * SudachiJaPhonemizer.
 * <p>
 * system property
 * <ul>
 * <li> "sudachi.dir" ... a directory includes a sudachi dictionary and settings
 * </ul>
 * TODO crash on several jvm e.g 1.8.0_341-b10 ar DirectIntBufferU#get
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/27 umjammer initial version <br>
 */
@Locales(countries = "Japan", languages = "Japanese")
public class SudachiJaPhonemizer implements JaPhonemizer {

    private final Tokenizer tokenizer;

    public SudachiJaPhonemizer() {
        try {
            Config config = Config.fromClasspath("sudachi.json");
            Dictionary dict = new DictionaryFactory().create(config); // DON'T CLOSE in this block
            tokenizer = dict.create();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { dict.close(); } catch (IOException e) { throw new UncheckedIOException(e); }
            }));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String phoneme(String text) {
        StringBuilder sb = new StringBuilder();
        for (Morpheme m : tokenizer.tokenize(Tokenizer.SplitMode.C, text)) {
if (Debug.isLoggable(Level.INFO)) {
 System.err.println(m.surface() + "\t" + m.partOfSpeech() +
                 "\t" + m.normalizedForm() +
                 "\t" + m.dictionaryForm() +
                 "\t" + m.readingForm() +
                 "\t" + m.getDictionaryId());
}
            if (m.partOfSpeech().get(0).equals("助詞")) {
                if (m.surface().equals("は")) {
                    sb.append("ワ");
                } else if (m.surface().equals("へ")) {
                    sb.append("エ");
                } else {
                    sb.append(m.readingForm());
                }
            } else if (m.partOfSpeech().get(0).equals("空白")) {
                sb.append(" ");
            } else {
                sb.append(m.readingForm());
            }
        }
if (Debug.isLoggable(Level.INFO)) {
 System.err.println(sb);
}
        return sb.toString();
    }
}
