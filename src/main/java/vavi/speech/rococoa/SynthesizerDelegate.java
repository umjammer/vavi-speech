/*
 * Copyright 2007, 2008, 2009 Duncan McGregor
 *
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 *
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */

package vavi.speech.rococoa;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.rococoa.cocoa.foundation.NSRange;
import org.rococoa.cocoa.appkit.NSSpeechSynthesizer;
import vavi.util.Debug;


/**
 * SynthesizerDelegate.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
public class SynthesizerDelegate implements NSSpeechSynthesizer.NSSpeechSynthesizerDelegate {

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

    public SynthesizerDelegate(NSSpeechSynthesizer ss) {
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

    public void speechSynthesizer_didFinishSpeaking(NSSpeechSynthesizer sender, boolean success) {
        this.success = success;
        synchronized (speechDoneMonitor) {
            speechDoneMonitor.notify();
        }
    }

    public void waitForSpeechDone(long interval, boolean stoppedNormally) {
        synchronized (speechDoneMonitor) {
            try {
                speechDoneMonitor.wait(interval);
                if (stoppedNormally != isSuccess()) {
                    throw new IllegalStateException("Success flag check failed");
                }
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    public void waitForNextWord(long interval) {
        synchronized (waitForSpeechWordMonitor) {
            try {
                waitForSpeechWordMonitor.wait(interval);
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    public void waitForWord(long interval, String word) {
        synchronized (waitForSpeechWordMonitor) {
            wordWaitingFor = word;
            try {
                waitForSpeechWordMonitor.wait(interval);
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    @SuppressWarnings("unused")
    private String getCallerName() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            if (ste.getMethodName().startsWith("test")) {
                return ste.getMethodName();
            }
        }
        return "Unknown method";
    }

    @Override
    public void speechSynthesizer_didEncounterErrorAtIndex_ofString_message(NSSpeechSynthesizer sender,
                                                                            Integer characterIndex,
                                                                            String text,
                                                                            String errorMessage) {
Debug.println(Level.FINER, "speechSynthesizer_didEncounterErrorAtIndex_ofString_message: " + sender);
        position = characterIndex;
        this.errorMessage = errorMessage;
    }

    @Override
    public void speechSynthesizer_didEncounterSyncMessage(NSSpeechSynthesizer sender, String synchMark) {
Debug.println(Level.FINER, "speechSynthesizer_didEncounterSyncMessage: " + sender);
        this.synchMark = synchMark;
    }

    @Override
    public synchronized void speechSynthesizer_willSpeakPhoneme(NSSpeechSynthesizer sender, short phonemeOpcode) {
Debug.println(Level.FINER, "speechSynthesizer_willSpeakPhoneme: " + sender);
        phonemesSpoken.add(sender.opcodeToPhoneme(phonemeOpcode));
    }

    @Override
    public void speechSynthesizer_willSpeakWord_ofString(NSSpeechSynthesizer sender, NSRange wordToSpeak, String text) {
Debug.println(Level.FINER, "speechSynthesizer_willSpeakWord_ofString: " + sender);
        wordsSpoken.add(text.substring((int) wordToSpeak.getLocation(), (int) wordToSpeak.getEndLocation()));
        if (wordWaitingFor == null || wordsSpoken.get(wordsSpoken.size() - 1).equals(wordWaitingFor)) {
            synchronized (waitForSpeechWordMonitor) {
                waitForSpeechWordMonitor.notify();
            }
        }
    }
}
