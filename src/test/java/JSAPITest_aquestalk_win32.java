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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import vavi.speech.aquestalk.jsapi.AquesTalkSynthesizerModeDesc;


/**
 * JSAPITest_aquestalk_win32. (JSAPI)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledIfSystemProperty(named = "os.arch", matches = "x86")
class JSAPITest_aquestalk_win32 {

    @Test
    void test01(String file) throws Exception {
        speak("すもももももももものうち");
    }

    public static void speak(String file) throws Exception {
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

    /** */
    public static void main(String[] args) throws Exception {
        JSAPITest_aquestalk_win32 app = new JSAPITest_aquestalk_win32();
        JSAPITest_aquestalk_win32.speak(args[0]);
    }
}

/* */
