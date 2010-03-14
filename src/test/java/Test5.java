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
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test5 {

    public void test01(String text) throws Exception {
        Synthesizer synthesizer;
        // シンセザイザのモードを指定
//        SynthesizerModeDesc desc = new SynthesizerModeDesc("AquesTalkSynthEngineCentral", "general", Locale.JAPAN, false, null);
        SynthesizerModeDesc desc = new AquesTalkSynthesizerModeDesc("AquesTalkSynthEngineCentral", "general", Locale.JAPAN);
        // シンセザイザを作成
        synthesizer = Central.createSynthesizer(desc);
        synthesizer.speakPlainText("ハローワールド", null);
        synthesizer.speakPlainText("ゆっくりしていってね", null);
        synthesizer.speakPlainText("そんなことよりおうどんたべたい", null);
        synthesizer.speakPlainText("漢字読めるの？", null);
        synthesizer.speakPlainText(text, null);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

        synthesizer.allocate();
        synthesizer.resume();

        synthesizer.deallocate();
    }

    public static void main(String[] args) throws Exception {
        Test5 test5 = new Test5();
        test5.test01(args[0]);
    }
}

/* */
