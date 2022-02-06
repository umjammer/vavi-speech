/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;

import vavi.speech.phonemizer.SudachiJaPhonemizer;
import vavi.util.properties.annotation.Env;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.JsonPathParser;
import vavix.util.screenscrape.annotation.PlainInputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * Sudachi proofreading by oov.
 *
 * set environment variable RECRUIT_PROOFREADING_API_KEY
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/28 umjammer initial version <br>
 * @see "https://a3rt.recruit-tech.co.jp/product/proofreadingAPI/"
 */
@PropsEntity
public class Test14 {

    /**
     * @param args 0: file name, 1: exclude regex
     */
    public static void main(String[] args) throws Exception {
        Test14 app = new Test14();
        PropsEntity.Util.bind(app);
        if (app.apiKey == null || app.apiKey.isEmpty()) {
            throw new IllegalStateException("set env RECRUIT_PROOFREADING_API_KEY");
        }
        app.exec(args[0], args.length == 2 ? args[1] : "");
    }

    private static final String c1 = (char) 0x1b + "[" + 37 + "m";
    private static final String c0 = (char) 0x1b + "[" + 0 + "m";

    /** check single character and checked by proofreading */
    private boolean checkSingle = false;
    /** check only checked by proofreading */
    private boolean checkAr3t = true;

    @Env(name = "RECRUIT_PROOFREADING_API_KEY")
    String apiKey;

    @WebScraper(url = "https://api.a3rt.recruit-tech.co.jp/proofreading/v2/typo?apikey={0}&sentence={1}&sensitivity={2}",
                parser = JsonPathParser.class,
                input = PlainInputHandler.class,
                isDebug = false,
                isCollection = false)
    public static class Result {
        @Target
        String resultID;
        @Target
        int status;
        @Target
        String message;
        @Target
        String inputSentence;
        @Target
        String normalizedSentence;
        @Target
        String checkedSentence;
        class Alert {
            int pos;
            String word;
            float score;
            List<String> suggestions;
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("Arart [pos=");
                sb.append(pos);
                sb.append(", word=");
                sb.append(word);
                sb.append(", score=");
                sb.append(score);
                sb.append(", suggestions=");
                sb.append(suggestions);
                sb.append("]");
                return sb.toString();
            }
        }
        // TODO should be eliminated
        public static class MyTypeToken extends com.google.gson.reflect.TypeToken<ArrayList<Alert>> { public MyTypeToken() { super(); }};
        @Target(optional = true, option = MyTypeToken.class) /* should be "option = Alert.class" */
        List<Alert> alerts;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(resultID);
            sb.append(",");
            sb.append(status);
            sb.append(",");
            sb.append(message);
            sb.append(",");
            sb.append(inputSentence);
            sb.append(",");
            sb.append(normalizedSentence);
            sb.append(",");
            sb.append(checkedSentence);
            sb.append(",");
            sb.append(alerts);
            return sb.toString();
        }
    }

    public Result proofread(String text) {
        try {
            return WebScraper.Util.scrape(Result.class, apiKey, text, "low").get(0);
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

    /** line number */
    private int l = 1;

    /**
     * @param exclusionPattern regex
     */
    void exec(String file, String exclusionPattern) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(SudachiJaPhonemizer.class.getResourceAsStream("/sudachi.json"))) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
        }

        Dictionary dict = new DictionaryFactory().create(System.getProperty("sudachi.dir"), sb.toString());
        Tokenizer tokenizer = dict.create();

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replaceAll("(｜|［＃.+?］|《.+?》)", ""); // exclude aozora markup tag and ruby
                Result result = null;
                List<Result.Alert> others = new ArrayList<>();
                for (Morpheme m : tokenizer.tokenize(Tokenizer.SplitMode.C, line)) {
                    if (m.isOOV() &&
                        containsJaCharacters(m.surface()) &&
                        !m.surface().matches(exclusionPattern)
                       ) {
                        // TODO google suggest
                        String p1 = c1 + line.substring(Math.max(0, m.begin() - 30), m.begin()) + c0;
                        String p2 = line.substring(m.begin(), m.end());
                        String p3 = c1 + line.substring(m.end(), Math.min(line.length(), m.end() + 30)) + c0;
System.err.printf("%sA: %04d%s\t%s\t%s%s%s\n", c1, l, c0, m.surface(), p1, p2, p3);
                    } else {
                        final String target = "[名詞, 普通名詞, 一般, *, *, *]";
                        if (checkSingle &&
                            m.surface().length() == 1 &&
                            m.partOfSpeech().toString().equals(target) &&
                            !m.surface().matches("[　…？！０-９]")
                            ) {
                            String p1 = c1 + line.substring(Math.max(0, m.begin() - 30), m.begin()) + c0;
                            String p2 = line.substring(m.begin(), m.end());
                            String p3 = c1 + line.substring(m.end(), Math.min(line.length(), m.end() + 30)) + c0;

                            if (result == null) {
                                result = proofread(line);
                            }
                            if (result.status == 1) {
                                result.alerts.forEach(a -> {
                                    if (a.pos == m.begin()) {
                                        // ひらがなは確実っぽい
                                        // sensitivity=low なら許せる数
System.err.printf("%sB: %04d%s\t%s\t%s%s%s\t\t%s%f\t%s%s\n", c1, l, c0, m.surface(), p1, p2, p3, c1, a.score, a.suggestions, c0);
                                    } else {
                                        others.add(a);
                                    }
                                });
                            } else {
//System.err.printf("%sC: %04d%s\t%s\t%s%s%s\t\t%s\n", c1, l, c0, m.surface(), p1, p2, p3, "");
                            }
                        }
                    }
                }

                if (checkAr3t) {
                    if (result == null) {
                        result = proofread(line);
                        if (result.status == 1) {
                            others.addAll(result.alerts);
                        }
                    }
                    String xline = result.normalizedSentence;
                    others.forEach(a -> {
                        if (a.score >= 0.90) {
                            String q1 = c1 + xline.substring(Math.max(0, a.pos - 30), a.pos) + c0;
                            String q2 = xline.substring(a.pos, a.pos + a.word.length());
                            String q3 = c1 + xline.substring(a.pos + a.word.length(), Math.min(xline.length(), a.pos + a.word.length() + 30)) + c0;
System.err.printf("%sD: %04d%s\t%s\t%s%s%s\t\t%s%f\t%s%s\n", c1, l, c0, a.word, q1, q2, q3, c1, a.score, a.suggestions, c0);
                        }
                    });
                }

                result = null;
                others.clear();
                l++;
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
