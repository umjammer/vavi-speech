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
 * GooFuriganaJaPhonemizer.
 *
 * set environment variable DOCOMO_GOO_HIRAGANA_API_KEY
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 */
@PropsEntity
@Locales(countries = "Japan", languages = "Japanese")
public class GooFuriganaJaPhonemizer implements JaPhonemizer {

    @Env(name = "DOCOMO_GOO_HIRAGANA_API_KEY")
    String apiKey;

    {
        try {
            PropsEntity.Util.bind(this);
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("set environment variable DOCOMO_GOO_HIRAGANA_API_KEY");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @WebScraper(url = "https://labs.goo.ne.jp/api/hiragana",
                input = PostInputHandler.class,
                parser = JsonPathParser.class)
    public static class Result {
        @Target
        String request_id;
        @Target
        String output_type;
        @Target
        String converted;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(request_id);
            sb.append(",");
            sb.append(output_type);
            sb.append(",");
            sb.append(converted);
            return sb.toString();
        }
    }

    @Override
    public String phoneme(String text) {
        try {
            Result result = WebScraper.Util
                    .scrape(Result.class,
                            "{\"app_id\":\"{0}\",\"request_id\":\"001\", \"sentence\":\"{1}\",\"output_type\":\"katakana\"}",
                            "application/json",
                            apiKey,
                            text.replace("\n", "")).get(0);
System.err.println(result);
            // TODO 助詞 は、へ, space
            return result.converted;
        } catch (IOException e) {
System.err.println(text);
            throw new IllegalStateException(e);
        }
    }
}

/* */
