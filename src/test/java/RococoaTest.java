/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import vavi.speech.rococoa.SynthesizerDelegate;
import vavi.util.Debug;
import vavix.rococoa.avfoundation.AVSpeechSynthesisVoice;
import vavix.rococoa.avfoundation.AVSpeechSynthesizer;
import vavix.rococoa.avfoundation.AVSpeechUtterance;


/**
 * RococoaTest. (rococoa)
 *
 * TODO doesn't sound voice by current jna
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledOnOs(OS.MAC)
class RococoaTest {

    private static final int TIME_TO_WAIT = 50000;

    @Test
    void test01() throws Exception {
        speak("すもももももももものうち");
    }

    void speak(String text) throws Exception {
        AVSpeechSynthesizer ss = AVSpeechSynthesizer.newInstance();
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
Debug.println(Locale.getDefault().toString());
        AVSpeechSynthesisVoice voice = AVSpeechSynthesisVoice.withLanguage(Locale.getDefault().toString());
        AVSpeechUtterance utterance = AVSpeechUtterance.of(text);
        utterance.setVoice(voice);
        utterance.setVolume(.2f);
        ss.speakUtterance(utterance);
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }

    public static void main(String[] args) throws Exception {
        RococoaTest app = new RococoaTest();
        app.speak(args[0]);
    }
}

/* */
