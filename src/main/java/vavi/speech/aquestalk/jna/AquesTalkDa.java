/*
 * AquesTalk rule-based speech synthesis engine
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;


/**
 * Generates audio waveform data from a sequence of phonetic symbols and outputs it to a sound device.
 *
 * 32 bit mode only
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalkDa extends Library {

    AquesTalkDa INSTANCE = Native.load("AquesTalkDa", AquesTalkDa.class);

    /**
     * Synthesizes and outputs audio (synchronous type)
     * It won't return until the audio finishes playing.
     * @param koe [in] Phonetic symbol string (NULL terminated)
     * @param iSpeed [in] Speech rate [%] Specify between 50-300 (=100)
     * @return 0: Normal completion Other: Error code
     */
    int AquesTalkDa_PlaySync(String koe, int iSpeed);

    /**
     * Create an instance of a speech synthesis engine (asynchronous type)
     * @return Returns the handle of the speech synthesis engine.
     */
    int AquesTalkDa_Create();

    /**
     * Releases the speech synthesis engine instance (asynchronous type)
     * @param hMe [in] Handle of speech synthesis engine Created by AquesTalkDa_Create()
     */
    void AquesTalkDa_Release(int hMe);

    /**
     * Synthesizes and outputs voice (asynchronous type)
     * Return immediately after generating the audio waveform
     * If you specify hWnd, the message specified in msg will be posted after playback ends.
     * It is also possible to play continuously by calling AquesTalkDa_Play() before playback ends.
     * @param hMe [in] Handle of speech synthesis engine Created by AquesTalkDa_Create()
     * @param koe [in] Phonetic symbol string (NULL terminated)
     * @param iSpeed [in] Speech rate [%] Specify between 50-300 (default 100)
     * @param hWnd [in] Window handle to which the end message is sent
     * @param msg [in] Exit message (default 0)
     * @param dwUser [in] Any user parameter (set in lParam of the message) (default 0)
     * @return 0: Normal completion Other: Error code
     */
    int AquesTalkDa_Play(int hMe, String koe, int iSpeed, int hWnd, int msg, int dwUser);

    /**
     * Stop playback
     * Pauses playback while playing with AquesTalkDa_Play().
     * If playback is in progress (including playback waiting),
     * and a destination for sending an end message has been specified, an end message is posted.
     * @param hMe [in] Handle of speech synthesis engine Created by AquesTalkDa_Create()
     */
    void AquesTalkDa_Stop(int hMe);

    /**
     * Playing or not
     * @param hMe [in] Handle of speech synthesis engine Created by AquesTalkDa_Create()
     * @return 1: Playing 0: Not playing
     */
    int AquesTalkDa_IsPlay(int hMe);
}
