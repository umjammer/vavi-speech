/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;

import vavi.speech.phoneme.SudachiJaPhonemer;


/**
 * Sudachi proofreading by oov.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/28 umjammer initial version <br>
 */
public class Test14 {

    /**
     * @param args 0: file name
     */
    public static void main(String[] args) throws Exception {
        Test14 app = new Test14();
        app.exec(args[0]);
    }

    void exec(String file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(SudachiJaPhonemer.class.getResourceAsStream("/sudachi.json"))) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
        }

        Dictionary dict = new DictionaryFactory().create(System.getProperty("sudachi.dir"), sb.toString());
        Tokenizer tokenizer = dict.create();

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replaceAll("(［＃.+?］|《.+?》)", ""); // exclude aozora markup tag and ruby
                for (Morpheme m : tokenizer.tokenize(Tokenizer.SplitMode.C, line)) {
                    if (m.isOOV() && containsKanji(m.surface())) {
System.err.println(m.surface() + "\t" + line);
                    }
                }
            }
        }
    }

    boolean containsKanji(String str) {
        for (int i = 0;i < str.length(); i++) {
            int code = str.charAt(i);
            if (code >= 0x4e00 && code <= 0x9fcf) {
                return true;
            }
        }
        return false;
    }
}

/* */
