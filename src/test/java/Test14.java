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
     * @param args 0: file name, 1: exclude regex
     */
    public static void main(String[] args) throws Exception {
        Test14 app = new Test14();
        app.exec(args[0], args.length == 2 ? args[1] : "");
    }

    final static String c1 = (char) 0x1b + "[" + 37 + "m";
    final static String c0 = (char) 0x1b + "[" + 0 + "m";

    /**
     * @param exclusionPattern regex
     */
    void exec(String file, String exclusionPattern) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(SudachiJaPhonemer.class.getResourceAsStream("/sudachi.json"))) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
        }

        Dictionary dict = new DictionaryFactory().create(System.getProperty("sudachi.dir"), sb.toString());
        Tokenizer tokenizer = dict.create();

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            int l = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replaceAll("(｜|［＃.+?］|《.+?》)", ""); // exclude aozora markup tag and ruby
                for (Morpheme m : tokenizer.tokenize(Tokenizer.SplitMode.C, line)) {
                    if (m.isOOV() && containsJaCharacters(m.surface()) &&
                        !m.surface().matches(exclusionPattern)
                       ) {
                        // TODO google suggest
                        // TODO mark up location in line
                        String p1 = c1 + line.substring(Math.max(0, m.begin() - 30), m.begin()) + c0;
                        String p2 = line.substring(m.begin(), m.end());
                        String p3 = c1 + line.substring(m.end(), Math.min(line.length(), m.end() + 30)) + c0;
System.err.printf("%s%03d%s\t%s\t%s%s%s\n", c1, l++, c0, m.surface(), p1, p2, p3);
                    }
                }
            }
        }
    }

    boolean containsJaCharacters(String str) {
        for (int i = 0;i < str.length(); i++) {
            int code = str.charAt(i);
            if (code >= 0x4e00 && code <= 0x9fcf) { // kanji
                return true;
            }
            if (code >= 0x30A0 && code <= 0x30FF) { // katakana
                return true;
            }
            if (code >= 0x3095 && code <= 0x3096) { // hiragana
                return true;
            }
        }
        return false;
    }
}

/* */
