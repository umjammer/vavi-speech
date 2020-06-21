/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import vavi.speech.aquestalk.jsapi.AquesTalkSynthesizerModeDesc;


/**
 * Test5. (JSAPI)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledIf("#{systemProperties['os.arch'].equals('x86')}")
class Test5 {

    @Test
    public void test01() throws Exception {
        speak("すもももももももものうち");
    }

    void speak(String text) throws Exception {
        // シンセザイザのモードを指定
        SynthesizerModeDesc desc = new AquesTalkSynthesizerModeDesc("AquesTalkEngineCentral", "general", Locale.JAPAN);
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
        Test5 app = new Test5();
        app.speak(args[0]);
    }
}

/* */
