/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import net.java.sen.StringTagger;
import net.java.sen.Token;


/**
 * Test1. (sen test)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/03/14 nsano initial version <br>
 */
@DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
class Test1 {

    @Test
    void test() throws Exception {
        Test1 app = new Test1();
        app.test("すもももももももものうち");
    }

    void test(String text) throws Exception {
        StringTagger tagger = StringTagger.getInstance();
        Token[] token = tagger.analyze(text);
        if (token != null) {
            for (int i = 0; i < token.length; i++) {
System.err.println(token[i].toString() + "\t("
           + token[i].getBasicString() + ")" + "\t" + token[i].getPos()
           + "(" + token[i].start() + "," + token[i].end() + ","
           + token[i].length() + ")\t" + token[i].getReading() + "\t"
           + token[i].getPronunciation());
            }
        }
    }
}

/* */
