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

import vavi.speech.googlecloud.jsapi.GoogleCloudTextToSpeechSynthesizerModeDesc;


/**
 * JSAPITest_google. (google cloud text to speech, JSAPI)
 *
 * <p>
 * specify the environment variable "GOOGLE_APPLICATION_CREDENTIALS"
 * for google credential json.
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
class JSAPITest_google {

    @Test
    public void test01() throws Exception {
        speak("すもももももももものうち");
    }

    public void speak(String text) throws Exception {
        // シンセザイザのモードを指定
        SynthesizerModeDesc desc = new GoogleCloudTextToSpeechSynthesizerModeDesc("GoogleCloudtextToSpeechEngineCentral", "general", Locale.JAPAN);
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
        JSAPITest_google app = new JSAPITest_google();
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
