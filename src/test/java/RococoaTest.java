/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.rococoa.cocoa.foundation.NSRange;
import org.rococoa.contrib.appkit.NSSpeechSynthesizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


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

    void test01(String text) throws Exception {
        speak("すもももももももものうち");
    }

    void speak(String text) throws Exception {
        NSSpeechSynthesizer ss = NSSpeechSynthesizer.synthesizerWithVoice(null);
        SynthesizerDelegate sd = new SynthesizerDelegate(ss);
        ss.setVolume(0.2f);
        ss.startSpeakingString(text);
        sd.waitForSpeechDone(TIME_TO_WAIT, true);
    }

    public static void main(String[] args) throws Exception {
        RococoaTest app = new RococoaTest();
        app.speak(args[0]);
    }

    static class SynthesizerDelegate implements NSSpeechSynthesizer.NSSpeechSynthesizerDelegate {

        private volatile boolean success = false;
        private List<String> wordsSpoken = new ArrayList<>();
        private List<String> phonemesSpoken = new ArrayList<>();
        private String wordWaitingFor;
        @SuppressWarnings("unused")
        private int position = -1;
        @SuppressWarnings("unused")
        private String synchMark;
        @SuppressWarnings("unused")
        private String errorMessage;
        private static final Object speechDoneMonitor = new Object();
        private static final Object waitForSpeechWordMonitor = new Object();

        SynthesizerDelegate(NSSpeechSynthesizer ss) {
            ss.setDelegate(this);
        }

        public void reset() {
            success = false;
            wordsSpoken.clear();
            phonemesSpoken.clear();
            wordWaitingFor = null;
            position = -1;
            errorMessage = null;
            synchMark = null;
        }

        public boolean isSuccess() {
            return success;
        }

        public List<String> getWordsSpoken() {
            return wordsSpoken;
        }

        public List<String> getPhonemesSpoken() {
            return phonemesSpoken;
        }

        public void speechSynthesizer_didFinishSpeaking(NSSpeechSynthesizer sender, final boolean success) {
            this.success = success;
            synchronized (speechDoneMonitor) {
                speechDoneMonitor.notify();
            }
        }

        public void waitForSpeechDone(long interval, boolean stoppedNormally) {
            synchronized (speechDoneMonitor) {
                try {
                    speechDoneMonitor.wait(interval);
                    assertEquals(stoppedNormally, isSuccess(), "Success flag check failed");
                } catch (InterruptedException ex) {
                    fail("Should have been notified in " + getCallerName() + " but interrupted out: " + ex);
                }
            }
        }

        public void waitForNextWord(long interval) {
            synchronized (waitForSpeechWordMonitor) {
                try {
                    waitForSpeechWordMonitor.wait(interval);
                } catch (InterruptedException ex) {
                    fail("Should have been notified in " + getCallerName() + " but interrupted out: " + ex);
                }
            }
        }

        public void waitForWord(long interval, final String word) {
            synchronized (waitForSpeechWordMonitor) {
                wordWaitingFor = word;
                try {
                    waitForSpeechWordMonitor.wait(interval);
                } catch (InterruptedException ex) {
                    fail("Should have been notified in " + getCallerName() + " but interrupted out: " + ex);
                }
            }
        }

        private String getCallerName() {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if ( ste.getMethodName().startsWith("test") ) {
                    return ste.getMethodName();
                }
            }
            return "Unknown method";
        }

        public void speechSynthesizer_didEncounterErrorAtIndex_ofString_message(NSSpeechSynthesizer sender, Integer characterIndex, String text, String errorMessage) {
            position = characterIndex;
            this.errorMessage = errorMessage;
//System.out.println(errorMessage);
//System.out.println("In callback: " + sender.getError());
        }

        public void speechSynthesizer_didEncounterSyncMessage(NSSpeechSynthesizer sender, String synchMark) {
            this.synchMark = synchMark;
//System.out.println("In callback, sync: " + sender.getRecentSync());
        }

        public synchronized void speechSynthesizer_willSpeakPhoneme(NSSpeechSynthesizer sender, short phonemeOpcode) {
            phonemesSpoken.add(sender.opcodeToPhoneme(phonemeOpcode));
        }

        public void speechSynthesizer_willSpeakWord_ofString(NSSpeechSynthesizer sender, NSRange wordToSpeak, String text) {
            wordsSpoken.add(text.substring((int) wordToSpeak.getLocation(), (int) wordToSpeak.getEndLocation()));
            if ( wordWaitingFor == null || wordsSpoken.get(wordsSpoken.size()-1).equals(wordWaitingFor)) {
                synchronized(waitForSpeechWordMonitor) {
                    waitForSpeechWordMonitor.notify();
                }
            }
        }
    }
}

/* */
