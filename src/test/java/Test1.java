/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import net.java.sen.StringTagger;
import net.java.sen.Token;


/**
 * Test1. (sen test) 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/03/14 nsano initial version <br>
 */
public class Test1 {

    public static void main(String[] args) throws Exception {
        StringTagger tagger = StringTagger.getInstance();
        Token[] token = tagger.analyze(args[0]);
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
