/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rococoa.jsapi;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
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

import com.sun.jna.Callback;
import com.sun.speech.engine.synthesis.BaseSynthesizerProperties;
import org.rococoa.ObjCObject;
import org.rococoa.Rococoa;
import vavi.speech.JavaSoundPlayer;
import vavi.speech.Player;
import vavi.speech.rococoa.SynthesizerDelegate;
import vavi.util.Debug;
import vavix.rococoa.avfoundation.AVSpeechSynthesizer;
import vavix.rococoa.avfoundation.AVSpeechUtterance;


/**
 * Provides  partial support for a JSAPI 1.0 synthesizer for the
 * AquesTalk speech synthesis system.
 */
public class RococoaSynthesizer implements Synthesizer {

    /** */
    private SynthesizerModeDesc desc;

    /** */
    private SynthesizerProperties properties = new BaseSynthesizerProperties();

    /**
     * Creates a new Synthesizer in the DEALLOCATED state.
     *
     * @param desc describes the allowed mode of operations for this
     *         synthesizer.
     */
    public RococoaSynthesizer(RococoaSynthesizerModeDesc desc) {
        this.desc = desc;
    }

    /** */
    private static class Pair {
        public Pair(String text, SpeakableListener listener) {
            this.text = text;
            this.listener = listener;
        }
        String text;
        SpeakableListener listener;
    }

    /** */
    private Queue<Pair> queue = new LinkedList<>();

    @Override
    public Enumeration<?> enumerateQueue() throws EngineStateError {
        return Collections.enumeration(queue);
    }

    @Override
    public void cancel() throws EngineStateError {
        synthesizer.stopSpeakingAtBoundary(AVSpeechSynthesizer.AVSpeechBoundaryImmediate);
    }

    @Override
    public void cancel(Object source)
        throws IllegalArgumentException, EngineStateError {

        synthesizer.stopSpeakingAtBoundary(AVSpeechSynthesizer.AVSpeechBoundaryImmediate);
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
        return properties;
    }

    @Override
    public String phoneme(String text) throws EngineStateError {
        return text;
    }

    @Override
    public void removeSpeakableListener(SpeakableListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void speak(String JSMLText, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        speak(new Pair(JSMLText, listener));
    }

    @Override
    public void speak(URL JSMLurl, SpeakableListener listener)
        throws JSMLException, IOException, EngineStateError {

        throw new UnsupportedOperationException();
    }

    @Override
    public void speak(Speakable JSMLtext, SpeakableListener listener)
        throws JSMLException, EngineStateError {

        speak(new Pair(JSMLtext.getJSMLText(), listener));
    }

    /* */
    public void speakPlainText(String text, SpeakableListener listener)
        throws EngineStateError {

        speak(new Pair(text, listener));
    }

    private void speak(Pair pair) throws EngineStateError {
        if (synthesizer == null) {
            throw new EngineStateError("not allocated");
        }
        queue.offer(pair);
    }

    /** */
    private final List<EngineListener> listeners = new ArrayList<>();

    @Override
    public void addEngineListener(EngineListener listener) {
        listeners.add(listener);
    }

    /** */
    private AVSpeechSynthesizer synthesizer;

    /** */
    private SynthesizerDelegate delegate;

    /** */
    private ExecutorService executer = Executors.newSingleThreadExecutor();

    /** */
    private boolean looping = true;

    /** */
    private boolean playing;

    /** */
    private Player player = new JavaSoundPlayer();

    @Override
    public void allocate() throws EngineException, EngineStateError {
        try {
            synthesizer = AVSpeechSynthesizer.newInstance();
            delegate = new SynthesizerDelegate(synthesizer);
            properties.setVolume(1f);
            executer.execute(() -> {
                while (looping) {
                    try {
                        Pair pair = queue.poll();
                        if (pair != null) {
                            if (pair.listener != null) {
                                pair.listener.speakableStarted(new SpeakableEvent(RococoaSynthesizer.this, SpeakableEvent.SPEAKABLE_STARTED));
                            }
                            if (false) { // TODO block
                                playing = true;
Debug.println(Level.FINE, "\n" + pair.text);
                                player.setVolume(properties.getVolume());
                                player.play(synthe(pair.text));
                                playing = false;
                            } else {
                                synthe2(pair.text);
                            }
                            if (pair.listener != null) {
                                pair.listener.speakableEnded(new SpeakableEvent(RococoaSynthesizer.this, SpeakableEvent.SPEAKABLE_ENDED));
                            }
                        }
                        Thread.sleep(300);
                    } catch (Exception e) {
e.printStackTrace();
                    }
                }
            });
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    /** */
    private void synthe2(String text) {
        AVSpeechUtterance utterance = AVSpeechUtterance.of(text);
        utterance.setVolume(getSynthesizerProperties().getVolume());
        synthesizer.speakUtterance(utterance);
        delegate.waitForSpeechDone(10000, true);
    }

    /** */
    private byte[] synthe(String text) {
        try {
//Debug.println(Level.FINER, "vioce: " + getSynthesizerProperties().getVoice());
            Path path = Files.createTempFile(getClass().getName(), ".aiff");
            AVSpeechUtterance utterance = AVSpeechUtterance.of(text);


            synthesizer.speakUtterance(utterance);

            // TODO objc block doesn't work
            Callback bufferCallback = new Callback() {
            };
            ObjCObject bufferCallbackProxy = Rococoa.proxy(bufferCallback);

            synthesizer.writeUtterance_toBufferCallback(utterance, bufferCallbackProxy.id());



            delegate.waitForSpeechDone(10000, true);
            byte[] wav = Files.readAllBytes(path);
            return wav;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void deallocate() throws EngineException, EngineStateError {
        looping = false;
        executer.shutdown();
        synthesizer.release();
    }

    @Override
    public AudioManager getAudioManager() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EngineModeDesc getEngineModeDesc() throws SecurityException {
        return desc;
    }

    @Override
    public EngineProperties getEngineProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getEngineState() {
        return (synthesizer != null ? Synthesizer.ALLOCATED : 0) |
               (synthesizer.isPaused() ? Synthesizer.PAUSED : 0) |
               (queue.isEmpty() ? Synthesizer.QUEUE_EMPTY : 0) |
               (!queue.isEmpty() ? Synthesizer.QUEUE_NOT_EMPTY : 0);
    }

    @Override
    public VocabManager getVocabManager() throws EngineStateError {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void pause() throws EngineStateError {
        synthesizer.pauseSpeakingAtBoundary(AVSpeechSynthesizer.AVSpeechBoundaryWord);
    }

    @Override
    public void removeEngineListener(EngineListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void resume() throws AudioException, EngineStateError {
        synthesizer.continueSpeaking();
    }

    @Override
    public boolean testEngineState(long state) throws IllegalArgumentException {
        if (state == Synthesizer.QUEUE_EMPTY) {
            return queue.peek() == null;
        } else if (state == Synthesizer.QUEUE_NOT_EMPTY) {
            return queue.peek() != null;
        } else {
            throw new IllegalArgumentException("unsupported sate: " + state);
        }
    }

    @Override
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
