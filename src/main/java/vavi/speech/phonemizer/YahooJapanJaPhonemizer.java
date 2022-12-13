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

import vavix.util.screenscrape.annotation.JsonPathParser;
import vavix.util.screenscrape.annotation.PostInputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * YahooJapanJaPhonemizer.
 *
 * set environment variable YAHOOJAPAN_API_KEY
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/27 umjammer initial version <br>
 * @see "https://developer.yahoo.co.jp/webapi/jlp/furigana/v2/furigana.html"
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

    @WebScraper(url = "https://jlp.yahooapis.jp/FuriganaService/V2/furigana",
            isDebug = false,
            input = PostInputHandler.class,
            parser = JsonPathParser.class,
            value = "$..result.word")
    public static class Result {
        @Target
        String furigana;
        @Target
        String surface;
        @Target
        String roman;
        public String toString() {
            String sb = surface +
                    "," +
                    furigana +
                    "," +
                    roman;
            return sb;
        }
        static final String BODY = "" +
                "{\n" +
                "  \"id\": \"{0}\",\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"method\": \"jlp.furiganaservice.furigana\",\n" +
                "  \"params\": {\n" +
                "    \"q\": \"{1}\",\n" +
                "    \"grade\": 1\n" +
                "  }\n" +
                "}";
        static final String UA = "Yahoo AppID: %s";
    }

    @Override
    public String phoneme(String text) {
        try {
            StringBuilder sb = new StringBuilder();
            System.setProperty("vavix.util.screenscrape.annotation.PostInputHandler.userAgent", String.format(Result.UA, apiKey));
            WebScraper.Util.foreach(Result.class, m -> {
System.err.println(m);
                // TODO 助詞 は、へ
                if (m.furigana != null && !m.furigana.isEmpty()) {
                    sb.append(m.furigana);
                } else if (m.surface != null && !m.surface.isEmpty()) {
                    sb.append(m.surface);
                }
            }, Result.BODY, "application/json", "1234-1", text);
System.err.println(sb);
            return converter.convertFrom(sb.toString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
