/*
 * AquesTalk rule-based speech synthesis engine
 *
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk;


/**
 * Generate speech waveform data in memory from speech symbol strings
 * Output audio waveform is 8Hz sampling, 16bit, monaural, WAV format
 *
 * 32 bit mode only
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class AquesTalk {

    static {
        System.loadLibrary("AquesTalkWrapper");
    }

    /** */
    public byte[] synthesize(String koe, int speed) {
        return synthe(koe, speed);
    }

    /**
     * Generates speech waveforms from phonetic symbol sequences.
     *
     * @param koe Phonetic string
     * @param speed Speech rate [%] Specify between 50-300
     * @return WAV format audio data. Returns null if an error occurs.
     */
    private native byte[] synthe(String koe, int speed);
}
