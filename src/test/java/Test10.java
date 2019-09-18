/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;


/**
 * Test10. (FreeTSS)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test10 {

    public void test01(String text) throws Exception {
        // シンセザイザのモードを指定
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        SynthesizerModeDesc desc = new SynthesizerModeDesc(Locale.US);
        Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
        Synthesizer synthesizer = Central.createSynthesizer(desc);

        synthesizer.allocate();
        synthesizer.resume();

        SynthesizerModeDesc smd = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
System.err.println("---- voices ----");
Arrays.asList(smd.getVoices()).forEach(v -> System.err.println(v.getName()));
System.err.println("---");
        Optional<Voice> voice = Arrays.stream(smd.getVoices())
                .filter(v -> v.getName().equals("kevin16"))
                .findFirst();
        synthesizer.getSynthesizerProperties().setVoice(voice.get());

        synthesizer.speakPlainText("Hello World", null);
        synthesizer.speakPlainText("Make yourself at home.", null);
        synthesizer.speakPlainText("By the way, I want to eat udon", null);
        synthesizer.speakPlainText("Do you speak English?", null);

        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }

    public static void main(String[] args) throws Exception {
        Test10 app = new Test10();
        app.test01(args[0]);
    }
}

/* */
