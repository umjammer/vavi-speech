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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import vavi.speech.googlecloud.jsapi.GoogleCloudTextToSpeechSynthesizerModeDesc;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * JSAPITest_google. (google cloud text to speech, JSAPI)
 *
 * <p>
 * specify the environment variable "GOOGLE_APPLICATION_CREDENTIALS"
 * for google credential json. e.g. "tmp/google.json"
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@PropsEntity(url = "file:local.properties")
@EnabledIfEnvironmentVariable(named = "GOOGLE_APPLICATION_CREDENTIALS", matches = ".*")
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
Debug.printf("google.json: exists: %s, size: %s, env: %s".formatted(Files.exists(p), Files.size(p), System.getenv("GOOGLE_APPLICATION_CREDENTIALS")));

        // シンセザイザのモードを指定
        desc = new GoogleCloudTextToSpeechSynthesizerModeDesc("GoogleCloudtextToSpeechEngineCentral", "general", Locale.JAPAN);
System.err.println("---- voices ----");
Arrays.asList(desc.getVoices()).forEach(v -> System.err.println(v.getName()));
System.err.println("---");
        // シンセザイザを作成
        synthesizer = Central.createSynthesizer(desc);
        synthesizer.allocate();
        synthesizer.resume();

        synthesizer.getSynthesizerProperties().setVolume(0.03f);
    }

    @AfterEach
    void teardown() throws Exception {
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }

    SynthesizerModeDesc desc;

    Synthesizer synthesizer;

    @Test
    public void test01() throws Exception {
        speak("すもももももももものうち");
    }

    public void speak(String text) throws Exception {
        synthesizer.speakPlainText(text, null);
    }

    /** */
    public static void main(String[] args) throws Exception {
        JSAPITest_google app = new JSAPITest_google();
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
