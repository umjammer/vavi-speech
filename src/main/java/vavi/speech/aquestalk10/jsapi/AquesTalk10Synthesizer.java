/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk10.jsapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import vavi.speech.JavaSoundPlayer;
import vavi.speech.Phonemer;
import vavi.speech.Player;
import vavi.speech.aquestalk10.jna.AquesTalk10Wrapper;
import vavi.speech.phoneme.KuromojiJaPhonemer;


/**
 * Provides  partial support for a JSAPI 1.0 synthesizer for the
 * AquesTalk speech synthesis system.
 */
public class AquesTalk10Synthesizer implements Synthesizer {

    /** */
    private SynthesizerModeDesc desc;

    /**
     * Creates a new Synthesizer in the DEALLOCATED state.
     *
     * @param desc describes the allowed mode of operations for this
     *         synthesizer.
     */
    public AquesTalk10Synthesizer(AquesTalk10SynthesizerModeDesc desc) {
        this.desc = desc;
    }

    /** */
    private Phonemer phonemer = new KuromojiJaPhonemer();

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
    private Queue<Pair> queue = new LinkedList<>();

    /* */
    public Enumeration<?> enumerateQueue() throws EngineStateError {
        return Collections.enumeration(queue);
    }

    /* */
    public void cancel() throws EngineStateError {
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

        speak(new Pair(JSMLText, listener));
    }

    /* */
    public void speak(URL JSMLurl, SpeakableListener listener)
        throws JSMLException, MalformedURLException, IOException, EngineStateError {

        throw new UnsupportedOperationException();
    }

    /* */
    public void speak(Speakable JSMLtext, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        speak(new Pair(JSMLtext.getJSMLText(), listener));
    }

    /* */
    public void speakPlainText(String text, SpeakableListener listener)
        throws EngineStateError {

        speak(new Pair(text, listener));
    }

    /** */
    private void speak(Pair pair) throws EngineStateError {
        if (aquesTalk10 == null) {
            throw new EngineStateError("not allocated");
        }
        queue.offer(pair);
    }

    /** */
    private List<EngineListener> listeners = new ArrayList<>();

    /* */
    public void addEngineListener(EngineListener listener) {
        listeners.add(listener);
    }

    /** */
    private AquesTalk10Wrapper aquesTalk10;

    /** */
    private ExecutorService executer = Executors.newSingleThreadExecutor();

    /** */
    private boolean looping = true;

    /** */
    private boolean playing;

    /** */
    private Player player = new JavaSoundPlayer();

    /* */
    public void allocate() throws EngineException, EngineStateError {
        aquesTalk10 = AquesTalk10Wrapper.getInstance();
        executer.execute(() -> {
            while (looping) {
                try {
                    Pair pair = queue.poll();
                    if (pair != null) {
                        if (pair.listener != null) {
                            pair.listener.speakableStarted(new SpeakableEvent(AquesTalk10Synthesizer.this, SpeakableEvent.SPEAKABLE_STARTED));
                        }
System.err.println(pair.text);
                        playing = true;
                        player.play(aquesTalk10.synthe(phonemer.phoneme(pair.text)));
                        playing = false;
                        if (pair.listener != null) {
                            pair.listener.speakableEnded(new SpeakableEvent(AquesTalk10Synthesizer.this, SpeakableEvent.SPEAKABLE_ENDED));
                        }
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
e.printStackTrace();
                }
            }
        });
    }

    /* */
    public void deallocate() throws EngineException, EngineStateError {
        looping = false;
        executer.shutdown();
        aquesTalk10 = null;
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
        listeners.remove(listener);
    }

    /* */
    public void resume() throws AudioException, EngineStateError {
    }

    /* */
    public boolean testEngineState(long state) throws IllegalArgumentException {
        if (state == Synthesizer.QUEUE_EMPTY) {
            return queue.isEmpty();
        } else if (state == Synthesizer.QUEUE_NOT_EMPTY) {
                return!queue.isEmpty();
        } else {
            throw new IllegalArgumentException("unsupported sate: " + state);
        }
    }

    /* */
    public void waitEngineState(long state) throws InterruptedException, IllegalArgumentException {
        if (state == Synthesizer.QUEUE_EMPTY) {
            while (!queue.isEmpty() || playing) {
                Thread.sleep(100);
            }
        } else {
            throw new IllegalArgumentException("unsupported sate: " + state);
        }
    }
}

/* */
