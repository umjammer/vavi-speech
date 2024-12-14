/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk10.jsapi;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
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

import com.sun.speech.engine.synthesis.BaseSynthesizerProperties;
import vavi.beans.InstanciationBinder;
import vavi.speech.Player;
import vavi.speech.aquestalk10.jna.AquesTalk10Wrapper;
import vavi.speech.phonemizer.JaPhonemizer;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * Provides  partial support for a JSAPI 1.0 synthesizer for the
 * AquesTalk speech synthesis system.
 * <p>
 * property file
 * <ul>
 * <li> aquestalk10.properties ... select a phonemizer class, locate in the class path.
 * </ul>
 * like
 * <pre>
 * phonemizer=vavi.speech.phonemizer.SudachiJaPhonemizer
 * </pre>
 * default class is {@link vavi.speech.phonemizer.KuromojiJaPhonemizer}
 */
@PropsEntity(url = "classpath:aquestalk10.properties")
public class AquesTalk10Synthesizer implements Synthesizer {

    private static final Logger logger = System.getLogger(AquesTalk10Synthesizer.class.getName());

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
    public AquesTalk10Synthesizer(AquesTalk10SynthesizerModeDesc desc) {
        this.desc = desc;
        try {
            PropsEntity.Util.bind(this);
logger.log(Level.INFO, "use " + phonemizer);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    @Property(binder = InstanciationBinder.class, value = "vavi.speech.phonemizer.KuromojiJaPhonemizer")
    private JaPhonemizer phonemizer;

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
    }

    @Override
    public void cancel(Object source) throws IllegalArgumentException, EngineStateError {
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeSpeakableListener(SpeakableListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void speak(String JSMLText, SpeakableListener listener) throws JSMLException, EngineStateError {
        speak(new Pair(JSMLText, listener));
    }

    @Override
    public void speak(URL JSMLurl, SpeakableListener listener)
        throws JSMLException, MalformedURLException, IOException, EngineStateError {

        throw new UnsupportedOperationException();
    }

    @Override
    public void speak(Speakable JSMLtext, SpeakableListener listener) throws JSMLException, EngineStateError {
        speak(new Pair(JSMLtext.getJSMLText(), listener));
    }

    @Override
    public void speakPlainText(String text, SpeakableListener listener) throws EngineStateError {
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
    private final List<EngineListener> listeners = new ArrayList<>();

    @Override
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

    /** TODO outsource? */
    private final Player player = new vavi.speech.JavaSoundPlayer();

    @Override
    public void allocate() throws EngineException, EngineStateError {
        try {
            aquesTalk10 = AquesTalk10Wrapper.getInstance();
            properties.setVolume(1f);
            executer.execute(() -> {
                while (looping) {
                    try {
                        Pair pair = queue.poll();
                        if (pair != null) {
                            if (pair.listener != null) {
                                pair.listener.speakableStarted(new SpeakableEvent(AquesTalk10Synthesizer.this, SpeakableEvent.SPEAKABLE_STARTED));
                            }
logger.log(Level.DEBUG, "\n" + pair.text);
                            playing = true;
                            player.setVolume(properties.getVolume());
                            player.play(aquesTalk10.synthesize(phonemizer.phoneme(pair.text)));
                            playing = false;
                            if (pair.listener != null) {
                                pair.listener.speakableEnded(new SpeakableEvent(AquesTalk10Synthesizer.this, SpeakableEvent.SPEAKABLE_ENDED));
                            }
                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
logger.log(Level.WARNING, e);
                        looping = false;
                    }
                }
            });
        } catch (PropertyVetoException e) {
            throw (EngineException) new EngineException().initCause(e);
        }
    }

    @Override
    public void deallocate() throws EngineException, EngineStateError {
        looping = false;
        executer.shutdown();
        aquesTalk10 = null;
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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public VocabManager getVocabManager() throws EngineStateError {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void pause() throws EngineStateError {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeEngineListener(EngineListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void resume() throws AudioException, EngineStateError {
    }

    @Override
    public boolean testEngineState(long state) throws IllegalArgumentException {
        if (state == Synthesizer.QUEUE_EMPTY) {
            return queue.isEmpty();
        } else if (state == Synthesizer.QUEUE_NOT_EMPTY) {
            return !queue.isEmpty();
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
