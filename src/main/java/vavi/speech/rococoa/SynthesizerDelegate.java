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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.rococoa.cocoa.foundation.NSRange;
import vavix.rococoa.avfoundation.AVSpeechSynthesizer;
import vavix.rococoa.avfoundation.AVSpeechUtterance;

import static java.lang.System.getLogger;


/**
 * SynthesizerDelegate.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
public class SynthesizerDelegate implements AVSpeechSynthesizer.AVSpeechSynthesizerDelegate {

    private static final Logger logger = getLogger(SynthesizerDelegate.class.getName());

    private volatile boolean success = false;
    private List<String> wordsSpoken = new ArrayList<>();
    private List<String> phonemesSpoken = new ArrayList<>();
    private String wordWaitingFor;
    private CountDownLatch speechDoneMonitor;
    private CountDownLatch waitForSpeechWordMonitor;

    public SynthesizerDelegate(AVSpeechSynthesizer ss) {
        ss.setDelegate(this);
    }

    public void reset() {
        success = false;
        wordsSpoken.clear();
        phonemesSpoken.clear();
        wordWaitingFor = null;
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

    public void waitForSpeechDone(long interval, boolean stoppedNormally) {
        try {
            speechDoneMonitor = new CountDownLatch(1);
            speechDoneMonitor.await(interval, TimeUnit.MILLISECONDS);
            if (stoppedNormally != isSuccess()) {
                throw new IllegalStateException("Success flag check failed");
            }
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void waitForNextWord(long interval) {
        try {
            waitForSpeechWordMonitor = new CountDownLatch(1);
            waitForSpeechWordMonitor.await(interval, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void waitForWord(long interval, String word) {
        wordWaitingFor = word;
        try {
            waitForSpeechWordMonitor = new CountDownLatch(1);
            waitForSpeechWordMonitor.await(interval, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @SuppressWarnings("unused")
    private static String getCallerName() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            if (ste.getMethodName().startsWith("test")) {
                return ste.getMethodName();
            }
        }
        return "Unknown method";
    }

    @Override
    public void speechSynthesizer_didStartSpeechUtterance(AVSpeechSynthesizer synthesizer, AVSpeechUtterance avSpeechUtterance) {
logger.log(Level.TRACE, "didStartSpeechUtterance: " + synthesizer);
        phonemesSpoken.add(avSpeechUtterance.speechString());
    }

    @Override
    public void speechSynthesizer_willSpeakRangeOfSpeechString_utterance(AVSpeechSynthesizer synthesizer, NSRange characterRange, AVSpeechUtterance utterance) {
logger.log(Level.TRACE, "willSpeakRangeOfSpeechString_utterance: " + synthesizer);
        wordsSpoken.add(utterance.speechString().substring((int) characterRange.getLocation(), (int) characterRange.getEndLocation()));
        if (wordWaitingFor == null && waitForSpeechWordMonitor != null || wordsSpoken.get(wordsSpoken.size() - 1).equals(wordWaitingFor)) {
            waitForSpeechWordMonitor.countDown();
        }
    }

    @Override
    public void speechSynthesizer_didPauseSpeechUtterance(AVSpeechSynthesizer synthesizer, AVSpeechUtterance utterance) {
logger.log(Level.TRACE, "didPauseSpeechUtterance: " + synthesizer);
    }

    @Override
    public void speechSynthesizer_didContinueSpeechUtterance(AVSpeechSynthesizer synthesizer, AVSpeechUtterance utterance) {
logger.log(Level.TRACE, "didContinueSpeechUtterance: " + synthesizer);
    }

    @Override
    public void speechSynthesizer_didFinishSpeechUtterance(AVSpeechSynthesizer synthesizer, AVSpeechUtterance utterance) {
logger.log(Level.TRACE, "didFinishSpeechUtterance: " + synthesizer);
        this.success = true;
        speechDoneMonitor.countDown();
    }

    @Override
    public void speechSynthesizer_didCancelSpeechUtterance(AVSpeechSynthesizer synthesizer, AVSpeechUtterance utterance) {
logger.log(Level.TRACE, "didCancelSpeechUtterance: " + synthesizer);
        this.success = false;
        speechDoneMonitor.countDown();
    }
}
