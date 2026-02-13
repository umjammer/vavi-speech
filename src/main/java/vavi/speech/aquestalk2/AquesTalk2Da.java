/*
 * Rule-based speech synthesis engine AquesTalk2
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk2;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


/**
 * Generates audio waveform data from a phonetic symbol string and outputs it to the sound device.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class AquesTalk2Da implements Closeable {

    /** */
    private long instance;

    static {
        System.loadLibrary("AquesTalk2Wrapper");
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
     * Synthesizes and outputs speech (synchronous type)
     * Does not return until speech playback is finished.
     * @param koe Phonetic symbol string
     * @param iSpeed Speaking speed [%] Specify between 50-300
     * @return 0: Normal termination, Other: Error code
     */
    private native int playSync(String koe, int iSpeed);

    /** */
    public AquesTalk2Da() {
        this.instance = create();
    }

    /** */
    public void setPhont(String file) throws IOException {
        if (file != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = Files.newInputStream(new File(file).toPath());
            byte[] b = new byte[8192]; 
            while (is.available() > 0) {
                int r = is.read(b, 0, b.length);
                if (r < 0) {
                    break;
                }
                baos.write(b, 0, r);
            }
            is.close();
            setPhont(baos.toByteArray());
        } else {
            setPhont((byte[]) null);
        }
    }

    /** */
    private native void setPhont(byte[] data);

    /**
     * Creates an instance of the speech synthesis engine (asynchronous type)
     * @return Returns the handle of the speech synthesis engine
     */
    private native long create();

    @Override
    public void close() {
        release(instance);
    }

    /**
     * Releases the instance of the speech synthesis engine (asynchronous type)
     * @param instance Handle of the speech synthesis engine created by {@link #create()}
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
     * Synthesizes and outputs speech (asynchronous type)
     * Returns immediately after generating the audio waveform.
     * If handle (hWnd) is specified, the message specified in message (msg) will be posted after playback ends.
     * It is also possible to call {@link #play(String)} before playback ends for continuous playback.
     * 
     * @param instance Handle of the speech synthesis engine created by {@link #create()}
     * @param koe Phonetic symbol string
     * @param speed Speaking speed [%] Specify between 50-300 (default 100)
     * @param handle Window handle for the end message destination
     * @param message End message (default 0)
     * @param param Arbitrary user parameter (set in lParam of the message) (default 0)
     * @return 0: Normal termination, Other: Error code
     */
    private native int play(long instance, String koe, int speed, int handle, String message, int param);

    /** */
    public void stop() {
        stop(instance);
    }

    /**
     * Stop playback 
     * Interrupts playback while playing with {@link #play(String)}.
     * If it is playing (including waiting for playback) and an end message destination is specified,
     * the end message will be posted.
     * @param instance Handle of the speech synthesis engine created by {@link #create()}
     */
    private native void stop(long instance);

    /** */
    public boolean isPlay() {
        return isPlay(instance) != 0;
    }

    /**
     * Whether it is playing or not
     * @param instance Handle of the speech synthesis engine created by {@link #create()}
     * @return 1: Playing, 0: Not playing
     */
    private native int isPlay(long instance);
}
