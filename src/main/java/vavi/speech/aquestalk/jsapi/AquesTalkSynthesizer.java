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

import vavi.speech.aquestalk.AquesTalkDa;


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
     * 		synthesizer.
     */
    public AquesTalkSynthesizer(AquesTalkSynthesizerModeDesc desc) {
        this.desc = desc;
    }

    /** */
    private class Pair {
        public Pair(String text, SpeakableListener listener) {
            this.text = text;
            this.listener = listener;
        }
        String text;
        SpeakableListener listener;
    }

    /** */
    private Queue<Pair> queue = new LinkedList<Pair>();

    /* */
    public Enumeration<?> enumerateQueue() throws EngineStateError {
        return null;
    }

    /* */
    public void cancel() throws EngineStateError {
        aquesTalkDa.stop();
    }

    /* */
    public void cancel(Object source)
        throws IllegalArgumentException, EngineStateError {
    }

    /* */
    public void cancelAll() throws EngineStateError {
    }

    /* */
    public void addSpeakableListener(SpeakableListener listener) {
        // TODO Auto-generated method stub
        
    }

    /* */
    public SynthesizerProperties getSynthesizerProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    public String phoneme(String arg0) throws EngineStateError {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    public void removeSpeakableListener(SpeakableListener listener) {
        // TODO Auto-generated method stub
        
    }

    /* */
    public void speak(String arg0, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        queue.add(new Pair(arg0, listener));
    }

    /* */
    public void speak(URL url, SpeakableListener listener)
        throws JSMLException, MalformedURLException, IOException, EngineStateError {
        // TODO Auto-generated method stub
        
    }

    /* */
    public void speak(Speakable arg0, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        queue.offer(new Pair(arg0.getJSMLText(), listener));
    }

    /* */
    public void speakPlainText(String arg0, SpeakableListener listener)
        throws EngineStateError {

        queue.offer(new Pair(arg0, listener));
    }

    /* */
    public void addEngineListener(EngineListener listener) {
        // TODO Auto-generated method stub
        
    }

    /* */
    public void allocate() throws EngineException, EngineStateError {
        this.aquesTalkDa = new AquesTalkDa();
    }

    /* */
    public void deallocate() throws EngineException, EngineStateError {
        aquesTalkDa = null;
    }

    /* */
    public AudioManager getAudioManager() {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    public EngineModeDesc getEngineModeDesc() throws SecurityException {
        return desc;
    }

    /* */
    public EngineProperties getEngineProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    public long getEngineState() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* */
    public VocabManager getVocabManager() throws EngineStateError {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    public void pause() throws EngineStateError {
        // TODO Auto-generated method stub
        
    }

    /* */
    public void removeEngineListener(EngineListener listener) {
        // TODO Auto-generated method stub
        
    }

    /* */
    public void resume() throws AudioException, EngineStateError {
        while (queue.size() > 0) {
            Pair pair = queue.poll();
            if (pair.listener != null) {
                pair.listener.speakableStarted(new SpeakableEvent(this, 0));
            }
            aquesTalkDa.play(pair.text);
            while (aquesTalkDa.isPlay()) {
                Thread.yield();
            }
            if (pair.listener != null) {
                pair.listener.speakableEnded(new SpeakableEvent(this, 0));
            }
        }
    }

    /* */
    public boolean testEngineState(long state) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return false;
    }

    /* */
    public void waitEngineState(long state) throws InterruptedException, IllegalArgumentException {
        // TODO Auto-generated method stub
        
    }
}

/* */
