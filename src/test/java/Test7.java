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

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import vavi.speech.aquestalk10.jsapi.AquesTalk10SynthesizerModeDesc;


/**
 * Test7. (aquestalk10, JSAPI)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
public class Test7 {

    public void test01() throws Exception {
        speak("すもももももももものうち");
    }

    public void speak(String text) throws Exception {
        // シンセザイザのモードを指定
        SynthesizerModeDesc desc = new AquesTalk10SynthesizerModeDesc("AquesTalk10EngineCentral", "general", Locale.JAPAN);
System.err.println("---- voices ----");
Arrays.asList(desc.getVoices()).forEach(v -> System.err.println(v.getName()));
System.err.println("---");
        // シンセザイザを作成
        Synthesizer synthesizer = Central.createSynthesizer(desc);
        synthesizer.allocate();
        synthesizer.resume();

        synthesizer.speakPlainText("ハローワールド", null);
        synthesizer.speakPlainText("ゆっくりしていってね", null);
        synthesizer.speakPlainText("そんなことよりおうどんたべたい", null);
        synthesizer.speakPlainText("漢字読めるの？", null);
        synthesizer.speakPlainText(text, null);

        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }

    public static void main(String[] args) throws Exception {
        Test7 app = new Test7();
        app.speak(args[0]);
    }
}

/* */
