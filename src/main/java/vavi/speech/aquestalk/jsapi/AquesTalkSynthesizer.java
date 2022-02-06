/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk.jsapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;

import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.EngineException;
import javax.speech.EngineListener;
import javax.speech.EngineModeDesc;
import javax.speech.EngineProperties;
import javax.speech.EngineStateError;
import javax.speech.VocabManager;
import javax.speech.synthesis.JSMLException;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;

import vavi.speech.Phonemizer;
import vavi.speech.aquestalk.AquesTalkDa;
import vavi.speech.phonemizer.SenJaPhonemizer;


/**
 * Provides  partial support for a JSAPI 1.0 synthesizer for the 
 * AquesTalk speech synthesis system.
 */
public class AquesTalkSynthesizer implements Synthesizer {

    /** */
    private AquesTalkDa aquesTalkDa;

    /** */
    private SynthesizerModeDesc desc;

    /**
     * Creates a new Synthesizer in the DEALLOCATED state.
     *
     * @param desc describes the allowed mode of operations for this
     *         synthesizer.
     */
    public AquesTalkSynthesizer(AquesTalkSynthesizerModeDesc desc) {
        this.desc = desc;
    }

    /** */
    private Phonemizer phonemizer = new SenJaPhonemizer();

    /**
     * <pre>
     * -Dsen.home=${sen.home}
     * </pre>
     */
    private class Pair {
        public Pair(String text, SpeakableListener listener) {
            this.text = phonemizer.phoneme(text);
            this.listener = listener;
        }
        String text;
        SpeakableListener listener;
    }

    /** */
    private Queue<Pair> queue = new LinkedList<>();

    @Override
    public Enumeration<?> enumerateQueue() throws EngineStateError {
        return null;
    }

    @Override
    public void cancel() throws EngineStateError {
        aquesTalkDa.stop();
    }

    @Override
    public void cancel(Object source)
        throws IllegalArgumentException, EngineStateError {
    }

    @Override
    public void cancelAll() throws EngineStateError {
    }

    @Override
    public void addSpeakableListener(SpeakableListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public SynthesizerProperties getSynthesizerProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String phoneme(String text) throws EngineStateError {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeSpeakableListener(SpeakableListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void speak(String JSMLText, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        queue.add(new Pair(JSMLText, listener));
    }

    @Override
    public void speak(URL JSMLurl, SpeakableListener listener)
        throws JSMLException, MalformedURLException, IOException, EngineStateError {
        // TODO Auto-generated method stub

    }

    @Override
    public void speak(Speakable JSMLtext, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        queue.offer(new Pair(JSMLtext.getJSMLText(), listener));
    }

    @Override
    public void speakPlainText(String text, SpeakableListener listener)
        throws EngineStateError {

        queue.offer(new Pair(text, listener));
    }

    @Override
    public void addEngineListener(EngineListener listener) {
    }

    @Override
    public void allocate() throws EngineException, EngineStateError {
        this.aquesTalkDa = new AquesTalkDa();
    }

    @Override
    public void deallocate() throws EngineException, EngineStateError {
        aquesTalkDa = null;
    }

    @Override
    public AudioManager getAudioManager() {
        return null;
    }

    @Override
    public EngineModeDesc getEngineModeDesc() throws SecurityException {
        return desc;
    }

    @Override
    public EngineProperties getEngineProperties() {
        return null;
    }

    @Override
    public long getEngineState() {
        return 0;
    }

    @Override
    public VocabManager getVocabManager() throws EngineStateError {
        return null;
    }

    @Override
    public void pause() throws EngineStateError {
    }

    @Override
    public void removeEngineListener(EngineListener listener) {
    }

    @Override
    public void resume() throws AudioException, EngineStateError {
        while (queue.size() > 0) {
            Pair pair = queue.poll();
            if (pair.listener != null) {
                pair.listener.speakableStarted(new SpeakableEvent(this, 0));
            }
            try {
                aquesTalkDa.play(pair.text);
            } catch (IllegalStateException e) {
e.printStackTrace(System.err);
            }
            while (aquesTalkDa.isPlay()) {
                Thread.yield();
            }
            if (pair.listener != null) {
                pair.listener.speakableEnded(new SpeakableEvent(this, 0));
            }
        }
    }

    @Override
    public boolean testEngineState(long state) throws IllegalArgumentException {
        return false;
    }

    @Override
    public void waitEngineState(long state) throws InterruptedException, IllegalArgumentException {
    }
}

/* */
