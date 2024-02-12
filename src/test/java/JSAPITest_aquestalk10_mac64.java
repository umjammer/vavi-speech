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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vavi.speech.aquestalk10.jsapi.AquesTalk10SynthesizerModeDesc;


/**
 * JSAPITest_aquestalk10_mac64. (aquestalk10, JSAPI)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledIfSystemProperty(named = "os.arch", matches = "x86_64")
@DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
class JSAPITest_aquestalk10_mac64 {

    @Test
    void test01() throws Exception {
        speak("すもももももももものうち");
    }

    SynthesizerModeDesc desc;

    Synthesizer synthesizer;

    @BeforeEach
    void setup() throws Exception {
        // シンセザイザのモードを指定
        desc = new AquesTalk10SynthesizerModeDesc("AquesTalk10EngineCentral", "general", Locale.JAPAN);
System.err.println("---- voices ----");
Arrays.asList(desc.getVoices()).forEach(v -> System.err.println(v.getName()));
System.err.println("---");
        // シンセザイザを作成
        synthesizer = Central.createSynthesizer(desc);
        synthesizer.allocate();
        synthesizer.resume();

        synthesizer.getSynthesizerProperties().setVolume(0.01f);
    }

    @AfterEach
    void teardown() throws Exception {
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }

    public void speak(String text) throws Exception {
        synthesizer.speakPlainText(text, null);
    }

    /** */
    public static void main(String[] args) throws Exception {
        JSAPITest_aquestalk10_mac64 app = new JSAPITest_aquestalk10_mac64();
        app.setup();
        Arrays.asList(
                "ハローワールド",
                "ゆっくりしていってね",
                "そんなことよりおうどんたべたい",
                "漢字読めるの？",
                args[0]
        ).forEach(t -> {
            try {
                System.err.println(t);
                app.speak(t);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        app.teardown();
    }
}

/* */
