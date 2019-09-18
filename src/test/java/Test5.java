/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import vavi.speech.aquestalk.jsapi.AquesTalkSynthesizerModeDesc;


/**
 * Test5. (JSAPI)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test5 {

    public void test01(String text) throws Exception {
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
        app.test01(args[0]);
    }
}

/* */
