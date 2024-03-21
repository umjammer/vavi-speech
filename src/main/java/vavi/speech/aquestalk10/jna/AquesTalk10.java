//
// Copyright AQUEST Corp. 2006-. All Rights Reserved.
// An unpublished and CONFIDENTIAL work. Reproduction, adaptation, or
// translation without prior written permission is prohibited except
// as allowed under the copyright laws.
//

package vavi.speech.aquestalk10.jna;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;


/**
 * Ruled speech synthesis engine AquesTalk10
 *
 * Generate audio waveform data in memory from audio symbol string
 * The output audio waveform is 16KHz, 16bit, monaural, WAV format.
 * Sampling frequency changes depending on voice type parameter {@code fsc}
 *
 * @author N.Yamazaki (AQUEST)
 * @version 2017/11/01 N.Yamazaki Creation for mac
 */
public interface AquesTalk10 extends Library {

    AquesTalk10 INSTANCE = Native.load("AquesTalk10", AquesTalk10.class);

    /** Voice type parameters */
    class AQTK_VOICE extends Structure {
        /**
         * @param bas voice base F1E/F2E/M1E (0/1/2)
         * @param spd speech speed 50-300, default: 100
         * @param vol volume 0-300, default: 100
         * @param pit key 20-200, default: depends on a {@code bas}
         * @param acc accent 0-200, default: depends on a {@code bas}
         * @param lmd pitch 1 0-200, default: 100
         * @param fsc pitch 2 (sampling frequency) 50-200, default: 100
         */
        AQTK_VOICE(VoiceBase bas, int spd, int vol, int pit, int acc, int lmd, int fsc) {
            super();
            this.bas = bas.ordinal();
            this.spd = spd;
            this.vol = vol;
            this.pit = pit;
            this.acc = acc;
            this.lmd = lmd;
            this.fsc = fsc;
        }
        /** voice base */
        public int bas;
        /** speech speed */
        public int spd;
        /** volume */
        public int vol;
        /** pitch */
        public int pit;
        /** accent */
        public int acc;
        /** pitch 1 */
        public int lmd;
        /** pitch 2 (sampling frequency) */
        public int fsc;
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("bas", "spd", "vol", "pit", "acc", "lmd", "fsc");
        }
    }

    /** Voice base */
    enum VoiceBase {
        F1E,
        F2E,
        M1E
    }

    // Preset voices

    /** female F1 */
    AQTK_VOICE gVoice_F1 = new AQTK_VOICE(VoiceBase.F1E, 100, 100, 100, 100, 100, 100);
    /** female F2 */
    AQTK_VOICE gVoice_F2 = new AQTK_VOICE(VoiceBase.F2E, 100, 100, 77, 150, 100, 100);
    /** female F3 */
    AQTK_VOICE gVoice_F3 = new AQTK_VOICE(VoiceBase.F1E, 80, 100, 100, 100, 61, 148);
    /** male M1 */
    AQTK_VOICE gVoice_M1 = new AQTK_VOICE(VoiceBase.M1E, 100, 100, 30, 100, 100, 100);
    /** male M2 */
    AQTK_VOICE gVoice_M2 = new AQTK_VOICE(VoiceBase.M1E, 105, 100, 45, 130, 120, 100);
    /** robot R1 */
    AQTK_VOICE gVoice_R1 = new AQTK_VOICE(VoiceBase.M1E, 100, 100, 30, 20, 190, 100);
    /** robot R2 */
    AQTK_VOICE gVoice_R2 = new AQTK_VOICE(VoiceBase.F2E, 70, 100, 50, 50, 50, 180);

    /**
     * Generates audio waveform from phonetic symbol string.
     * An area is allocated internally for the audio waveform data.
     * The voice waveform data should be released using {@link #AquesTalk_FreeWave(Pointer)}
     * on the caller side of this function.
     *
     * @param pParam [in] voice type parameters (AQTK_VOICE structure address)
     * @param koe [in] phonetic symbol string（UTF8, NULL ended, w/o BOM）
     * @param pSize [out] size of synthesized audio data [byte]（In case of error, error code is returned）
     * @return start address of audio data in WAV format. In case of error, NULL is returned
     */
    Pointer AquesTalk_Synthe_Utf8(AQTK_VOICE pParam, String koe, int[] pSize);

    /**
     * Free up space for audio data.
     * @param wav [in] specify the address returned by {@link #AquesTalk_Synthe_Utf8(AQTK_VOICE, String, int[])} ()}
     */
    void AquesTalk_FreeWave(Pointer wav);

    /**
     * Sets development license key.
     * Call once before synthesizing audio waveform.
     * This removes the limitations of the evaluation version.
     * @param key [in] specify license key
     * @return returns 0 if the license key is correct, 1 if not.
     * In order to prevent key analysis, 0 may be returned even if the key is invalid.
     * In this case, the restriction will not be lifted.
     */
    int AquesTalk_SetDevKey(byte[] key);

    /**
     * Sets use license key.
     * Call once before synthesizing audio waveform.
     * After that, the watermark included in the synthesized voice data will change from
     * "no license" to "use to licensed".
     * @param key [in] specify license key
     * @return returns 0 if the license key is correct, 1 if not.
     * In order to prevent key analysis, 0 may be returned even if the key is invalid.
     * In this case, it remains unlicensed.
     */
    int AquesTalk_SetUsrKey(byte[] key);

    /** Contents of the error code returned by the method */
    Map<Integer, String> errors = new HashMap<>() {{
        put(100, "Other errors");
        put(101, "Insufficient memory");
        put(103, "Errors in specifying phonetic symbol strings (long sounds at the beginning of words, consecutive consonants, etc.)");
        put(104, "There is no valid reading for the phonetic symbol string.");
        put(105, "An undefined phonetic symbol was specified in the phonetic symbol string.");
        put(106, "The tag specification for the phonetic symbol string is incorrect.");
        put(107, "Tag length exceeds limit (or [>] is missing)");
        put(108, "Invalid value specification in tag");
        put(120, "Phonetic string too long");
        put(121, "Too many phonetic symbols in one phrase");
        put(122, "Long phonetic symbol string (internal buffer over 1)");
    }};
}
