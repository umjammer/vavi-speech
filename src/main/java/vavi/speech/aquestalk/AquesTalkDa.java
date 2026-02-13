/*
 * AquesTalk rule-based speech synthesis engine
 *
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk;

import java.io.Closeable;


/**
 * Generates audio waveform data from a sequence of phonetic symbols and outputs it to a sound device.
 *
 * 32 bit mode only
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class AquesTalkDa implements Closeable {

    /** */
    private final long instance;

    static {
        System.loadLibrary("AquesTalkWrapper");
    }

    /** */
    public void play(String koe, boolean sync) {
        play(koe, 100, sync);
    }

    /** */
    public void play(String koe, int speed, boolean sync) {
        if (sync) {
            if (playSync(koe, speed) != 0) {
                throw new IllegalStateException();
            }
        } else {
            play(koe, speed);
        }
    }

    /**
     * Synthesize and output audio (synchronous type)
     * It will not return until the audio finishes playing.
     * @param koe Phonetic string
     * @param iSpeed Speech rate [%] Specify between 50-300
     * @return 0: Normal end, other: Error code
     */
    private native int playSync(String koe, int iSpeed);

    /** */
    public AquesTalkDa() {
        this.instance = create();
    }

    /**
     * Create an instance of a speech synthesis engine (asynchronous type)
     * @return Returns the handle to the speech synthesis engine.
     */
    private native long create();

    @Override
    public void close() {
        release(instance);
    }

    /**
     * Returns the handle to the speech synthesis engine.
     * @param instance Handle to a speech synthesis engine. Created with {@link #create()}
     */
    private native void release(long instance);

    /** */
    public void play(String koe) {
        play(koe, 100);
    }

    /** */
    public void play(String koe, int speed) {
        if (play(instance, koe, speed, 0, null, 0) != 0) {
            throw new IllegalStateException();
        }
    }

    /**
     * Synthesize and output voice (asynchronous type)
     * Return immediately after generating the audio waveform
     * If you specify hWnd, the message specified in msg will be posted after playback ends.
     * It is also possible to play continuously by calling {@link #play(String)} before playback ends.
     *
     * @param instance Handle to a speech synthesis engine. Created with {@link #create()}
     * @param koe Phonetic string
     * @param speed Speech rate [%] Specify between 50-300 (default 100)
     * @param handle Destination window handle for the end message
     * @param message Exit message (default 0)
     * @param param Any user parameters (set in the message's lParam) (default 0)
     * @return 0: Normal end. Other: Error code.
     */
    private native int play(long instance, String koe, int speed, int handle, String message, int param);

    /** */
    public void stop() {
        stop(instance);
    }

    /**
     * Stop playback
     * During playback with {@link #play(String)}, pauses playback.
     * If playback is in progress (including playback waiting),
     * and a destination for sending an end message has been specified, the end message will be posted.
     * @param instance Handle to a speech synthesis engine. Created with {@link #create()}
     */
    private native void stop(long instance);

    /** */
    public boolean isPlay() {
        return isPlay(instance) != 0;
    }

    /**
     * Playing or not
     * @param instance Handle to a speech synthesis engine. Created with {@link #create()}
     * @return 1: Playing 0: Not playing
     */
    private native int isPlay(long instance);
}
