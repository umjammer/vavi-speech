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

import org.junit.jupiter.api.Test;


/**
 * FreeTSSTest. (FreeTSS)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
class FreeTSSTest {

    @Test
    void test01() throws Exception {
        speak("She sells seashells by the seashore.");
    }

    void speak(String text) throws Exception {
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

        synthesizer.speakPlainText(text, null);

        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }

    public static void main(String[] args) throws Exception {
        FreeTSSTest app = new FreeTSSTest();
        Arrays.asList("Hello World", "Make yourself at home.", "By the way, I want to eat udon", "Do you speak English?", args[0]).forEach(t -> {
            try {
                app.speak(t);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }
}

/* */
