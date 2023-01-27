/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import vavi.speech.googlecloud.jsapi.GoogleCloudTextToSpeechSynthesizerModeDesc;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


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
@PropsEntity(url = "file:local.properties")
class JSAPITest_google {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "google.credential")
    String json = "tmp/google.json";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
        Path p = Paths.get(json);
Debug.printf("google.json: exists: %s, size: %s, env: %s", Files.exists(p), Files.size(p), System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
    }

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

    /** */
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
