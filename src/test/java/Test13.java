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

import vavi.speech.googlectts.jsapi.GoogleCloudTextToSpeechSynthesizerModeDesc;


/**
 * Test13. (google cloud text to speech, JSAPI)
 *
 * <p>
 * specify the environment variable "GOOGLE_APPLICATION_CREDENTIALS"
 * for google credential json.
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test13 {

    public void test01(String text) throws Exception {
        // シンセザイザのモードを指定
        SynthesizerModeDesc desc = new GoogleCloudTextToSpeechSynthesizerModeDesc("GoogleCloudtextToSpeechEngineCentral", "general", Locale.JAPAN);
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
        Test13 app = new Test13();
        app.test01(args[0]);
    }
}

/* */
