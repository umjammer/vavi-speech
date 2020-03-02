/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phoneme;

import java.io.IOException;
import java.util.Scanner;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;

import vavi.speech.Phonemer;


/**
 * SudachiJaPhonemer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/27 umjammer initial version <br>
 */
public class SudachiJaPhonemer implements Phonemer {

    private Tokenizer tokenizer;

    public SudachiJaPhonemer() {
        try {
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(SudachiJaPhonemer.class.getResourceAsStream("/sudachi.json"));
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            scanner.close();
//System.err.println(sb);
            Dictionary dict = new DictionaryFactory().create(System.getProperty("sudachi.dir"), sb.toString());
            tokenizer = dict.create();
        } catch (IOException e) {
            throw new IllegalStateException(e);
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
