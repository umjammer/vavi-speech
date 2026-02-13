/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rococoa.jsapi;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
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

import org.rococoa.Foundation;
import org.rococoa.ObjCBlocks.BlockLiteral;
import org.rococoa.ObjCObjectByReference;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSError;
import vavi.speech.JavaSoundPlayer;
import vavi.speech.Player;
import vavi.speech.rococoa.SynthesizerDelegate;
import vavix.rococoa.avfoundation.AVAudioFile;
import vavix.rococoa.avfoundation.AVAudioFormat;
import vavix.rococoa.avfoundation.AVAudioPCMBuffer;
import vavix.rococoa.avfoundation.AVSpeechSynthesizer;
import vavix.rococoa.avfoundation.AVSpeechSynthesizer.AVSpeechSynthesizerBufferCallback;
import vavix.rococoa.avfoundation.AVSpeechUtterance;

import static java.lang.System.getLogger;
import static org.rococoa.ObjCBlocks.block;


/**
 * Provides  partial support for a JSAPI 1.0 synthesizer for the
 * Rococoa speech synthesis system.
 */
public class RococoaSynthesizer implements Synthesizer {

    private static final Logger logger = getLogger(RococoaSynthesizer.class.getName());

    /** */
    private final SynthesizerModeDesc desc;

    /** */
    private final SynthesizerProperties properties = new BaseSynthesizerProperties();

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
    private final Queue<Pair> queue = new LinkedList<>();

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
    private final ExecutorService executer = Executors.newSingleThreadExecutor();

    /** */
    private boolean looping = true;

    /** */
    private boolean playing;

    /** */
    private final Player player = new JavaSoundPlayer();

    @Override
    public void allocate() throws EngineException, EngineStateError {
        try {
            synthesizer = AVSpeechSynthesizer.newInstance();
//            delegate = new SynthesizerDelegate(synthesizer);
            properties.setVolume(1f);
            executer.execute(() -> {
                while (looping) {
                    try {
                        Pair pair = queue.poll();
                        if (pair != null) {
                            if (pair.listener != null) {
                                pair.listener.speakableStarted(new SpeakableEvent(RococoaSynthesizer.this, SpeakableEvent.SPEAKABLE_STARTED));
                            }
                            playing = true;
logger.log(Level.DEBUG, "\n" + pair.text);
                            player.setVolume(properties.getVolume());
                            player.play(synthesize(pair.text));
                            playing = false;
                            if (pair.listener != null) {
                                pair.listener.speakableEnded(new SpeakableEvent(RococoaSynthesizer.this, SpeakableEvent.SPEAKABLE_ENDED));
                            }
                        }
                        Thread.sleep(300);
                    } catch (Exception e) {
logger.log(Level.DEBUG, e.getMessage(), e);
                    }
                }
            });
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    /** */
    private byte[] synthesize(String text) {
//logger.log(Level.TRACE, "voice: " + getSynthesizerProperties().getVoice());
        try {
            Path path = Files.createTempFile(getClass().getName(), ".wav");
            BlockLiteral bufferCallback = null;
            try {
                AVSpeechUtterance utterance = AVSpeechUtterance.of(text);
                utterance.setVolume(properties.getVolume());

                CountDownLatch cdl = new CountDownLatch(1);
                AtomicReference<AVAudioFile> audioFile = new AtomicReference<>();

                bufferCallback = block((AVSpeechSynthesizerBufferCallback) (block, audioBufferId) -> {
logger.log(Level.TRACE, "block enter");
                    try {
                        AVAudioPCMBuffer audioBuffer = Rococoa.wrap(audioBufferId, AVAudioPCMBuffer.class);
                        if (audioBuffer == null) {
logger.log(Level.ERROR, "buffer is not pcm");
                            cdl.countDown();
                            throw new IllegalStateException("buffer is not pcm");
                        }
logger.log(Level.TRACE, "frameLength: " + audioBuffer.frameLength());
                        if (audioBuffer.frameLength() == 0) {
                            // done
                            cdl.countDown();
logger.log(Level.TRACE, "file write done");
                        } else {
                            if (audioFile.get() == null) {
                                AVAudioFormat format16 = AVAudioFormat.init(3, audioBuffer.format().sampleRate(), 1, true);
logger.log(Level.INFO, "file: " + path + ", " + format16.settings());
                                audioFile.set(AVAudioFile.init(path.toUri(), format16.settings(), audioBuffer.format().commonFormat(), audioBuffer.format().isInterleaved()));
                                if (audioFile.get() == null) {
logger.log(Level.ERROR, "file creation failed");
                                    cdl.countDown();
                                    throw new IllegalStateException("file creation failed");
                                }
                            }
logger.log(Level.TRACE, "write: " + audioBuffer.frameLength());
                            ObjCObjectByReference outError = new ObjCObjectByReference();
                            audioFile.get().writeFromBuffer_error(audioBuffer, outError);
                            NSError error = outError.getValueAs(NSError.class);
                            if (error != null) {
logger.log(Level.ERROR, error.description());
                                cdl.countDown();
                                throw new IllegalStateException(error.description());
                            }
                        }
                    } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
                        cdl.countDown();
                        throw new UncheckedIOException(e);
                    }
                });

                synthesizer.writeUtterance_toBufferCallback(utterance, bufferCallback);
                cdl.await();

                if (audioFile.get() != null) {
                    audioFile.get().close();
                }

logger.log(Level.TRACE, "synthesize exit");
                return Files.readAllBytes(path);
            } finally {
                Files.deleteIfExists(path);
                if (bufferCallback != null)
                    Foundation.getRococoaLibrary().releaseObjCBlock(bufferCallback.getPointer());
            }
        } catch (Exception e) {
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
               (synthesizer != null && synthesizer.isPaused() ? Synthesizer.PAUSED : 0) |
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
