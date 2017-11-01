/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk2.jsapi;

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

import net.java.sen.StringTagger;
import net.java.sen.Token;

import vavi.speech.aquestalk.AquesTalkDa;


/**
 * Provides  partial support for a JSAPI 1.0 synthesizer for the 
 * AquesTalk speech synthesis system.
 */
public class AquesTalk2Synthesizer implements Synthesizer {

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
    public AquesTalk2Synthesizer(AquesTalk2SynthesizerModeDesc desc) {
        this.desc = desc;
    }

    /** */
    private class Pair {
        public Pair(String text, SpeakableListener listener) {
            StringBuilder sb = new StringBuilder();
            try {
                StringTagger tagger = StringTagger.getInstance();
                Token[] token = tagger.analyze(text);
                if (token != null) {
                    for (int i = 0; i < token.length; i++) {
System.err.println(token[i].toString() + "\t("
                   + token[i].getBasicString() + ")" + "\t" + token[i].getPos()
                   + "(" + token[i].start() + "," + token[i].end() + ","
                   + token[i].length() + ")\t" + token[i].getReading() + "\t"
                   + token[i].getPronunciation());
                        if (token[i].getReading() != null && !token[i].getPos().startsWith("記号")) {
                            sb.append(token[i].getReading());
                        }
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
System.err.println(sb);
            this.text = sb.toString();
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
    public String phoneme(String text) throws EngineStateError {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    public void removeSpeakableListener(SpeakableListener listener) {
        // TODO Auto-generated method stub
        
    }

    /* */
    public void speak(String JSMLText, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        queue.add(new Pair(JSMLText, listener));
    }

    /* */
    public void speak(URL JSMLurl, SpeakableListener listener)
        throws JSMLException, MalformedURLException, IOException, EngineStateError {
        // TODO Auto-generated method stub
        
    }

    /* */
    public void speak(Speakable JSMLtext, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        queue.offer(new Pair(JSMLtext.getJSMLText(), listener));
    }

    /* */
    public void speakPlainText(String text, SpeakableListener listener)
        throws EngineStateError {

        queue.offer(new Pair(text, listener));
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
