/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import vavi.speech.aquestalk.jsapi.AquesTalkSynthesizerModeDesc;


/**
 * Test2. (JSAPI)
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test2 {

    public void test01(String file) throws Exception {
        SynthesizerModeDesc desc = new AquesTalkSynthesizerModeDesc("AquesTalkSynthEngineCentral", "general", Locale.JAPAN);
        Synthesizer synthesizer = Central.createSynthesizer(desc);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String line = reader.readLine();
            synthesizer.speakPlainText(line, null);
        }
        reader.close();
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

        synthesizer.allocate();
        synthesizer.resume();

        synthesizer.deallocate();
    }

    public static void main(String[] args) throws Exception {
        Test2 app = new Test2();
        app.test01(args[0]);
    }
}

/* */
