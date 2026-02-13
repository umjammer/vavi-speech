/*
 * AquesTalk2 rule-based speech synthesis engine
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk10.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


/**
 * Generate speech waveform data in memory from speech symbol strings
 * Output audio waveform is 8Hz sampling, 16bit, monaural, WAV format
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalk2 extends Library {

    AquesTalk2 INSTANCE = Native.load("AquesTalk2", AquesTalk2.class);

    /**
     * Generates audio waveforms from audio symbol strings. Space is allocated internally for audio waveform data.
     * The voice waveform data is released by the caller of this function using {@link #AquesTalk2_FreeWave(Pointer)}.
     *
     * @param koe [in] Phonetic string
     * @param iSpeed [in] Speech rate [%] Specify between 50-300
     * @param pSize [out] Size of generated audio data [bytes] (If an error occurs, an error code will be returned)
     * @return The start address of the WAV format audio data. If an error occurs, null is returned.
     */
    Pointer AquesTalk2_Synthe_Utf8(String koe, int iSpeed, int[] pSize, Pointer phont);

    /**
     * Free up space for audio data
     *
     * @param wav [in] Specify the address returned by {@link #AquesTalk2_Synthe_Utf8(String, int, int[], Pointer)}
     */
    void AquesTalk2_FreeWave(Pointer wav);
}
