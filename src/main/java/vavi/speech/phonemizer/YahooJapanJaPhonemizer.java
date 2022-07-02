/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.io.IOException;

import vavi.util.Locales;
import vavi.util.properties.annotation.Env;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * YahooJapanJaPhonemizer.
 *
 * set environment variable YAHOOJAPAN_API_KEY
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/27 umjammer initial version <br>
 */
@PropsEntity
@Locales(countries = "Japan", languages = "Japanese")
public class YahooJapanJaPhonemizer implements JaPhonemizer {

    private DigitJaPhonemizer converter = new DigitJaPhonemizer();

    @Env(name = "YAHOOJAPAN_API_KEY")
    String apiKey;

    {
        try {
            PropsEntity.Util.bind(this);
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("set environment variable YAHOOJAPAN_API_KEY");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // value: because of fuckin' xml namespace
    @WebScraper(url = "https://jlp.yahooapis.jp/FuriganaService/V1/furigana?appid={0}&grade=1&sentence={1}",
            value = "//*[local-name()='Result']/*[local-name()='WordList']/*[local-name()='Word']")
    public static class Result {
        @Target(value = "/Word/Furigana")
        String furigana;
        @Target(value = "/Word/Surface")
        String surface;
        @Target(value = "/Word/Roman")
        String roman;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(surface);
            sb.append(",");
            sb.append(furigana);
            sb.append(",");
            sb.append(roman);
            return sb.toString();
        }
    }

    @Override
    public String phoneme(String text) {
        try {
            StringBuilder sb = new StringBuilder();
            WebScraper.Util.foreach(Result.class, m -> {
System.err.println(m);
                // TODO 助詞 は、へ
                if (m.furigana.isEmpty()) {
                    sb.append(m.surface);
                } else {
                    sb.append(m.furigana);
                }
            }, apiKey, text);
System.err.println(sb);
            return converter.convertFrom(sb.toString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
