/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Arrays;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import vavi.speech.rococoa.jsapi.RococoaSynthesizerModeDesc;


/**
 * Test11. (rococoa, JSAPI)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledOnOs(OS.MAC)
class Test11 {

    @Test
    void test01() throws Exception {
        speak("すもももももももものうち");
    }

    void speak(String text) throws Exception {
        // シンセザイザのモードを指定
        SynthesizerModeDesc desc = new RococoaSynthesizerModeDesc("RococaEngineCentral", "general", Locale.JAPAN);
System.err.println("---- voices ----");
Arrays.asList(desc.getVoices()).forEach(v -> System.err.println(v.getName()));
System.err.println("---");
        // シンセザイザを作成
        Synthesizer synthesizer = Central.createSynthesizer(desc);
        synthesizer.allocate();
        synthesizer.resume();

        synthesizer.speakPlainText(text, null);

        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }

    public static void main(String[] args) throws Exception {
        Test11 app = new Test11();
        Arrays.asList("ハローワールド", "ゆっくりしていってね", "そんなことよりおうどんたべたい", "漢字読めるの？", args[0]).forEach(t -> {
            try {
                app.speak(t);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }
}

/* */
