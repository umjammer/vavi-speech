/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import net.java.sen.StringTagger;
import net.java.sen.Token;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;


/**
 * SenTest. (sen test)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/03/14 nsano initial version <br>
 */
@DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
@EnabledIfSystemProperty(named = "sen.home", matches = ".*")
class SenTest {

    @Test
    void test() throws Exception {
        SenTest app = new SenTest();
        app.test("すもももももももものうち");
    }

    void test(String text) throws Exception {
        StringTagger tagger = StringTagger.getInstance();
        Token[] token = tagger.analyze(text);
        if (token != null) {
            for (Token value : token) {
                System.err.println(value.toString() + "\t("
                        + value.getBasicString() + ")" + "\t" + value.getPos()
                        + "(" + value.start() + "," + value.end() + ","
                        + value.length() + ")\t" + value.getReading() + "\t"
                        + value.getPronunciation());
            }
        }
    }
}

/* */
