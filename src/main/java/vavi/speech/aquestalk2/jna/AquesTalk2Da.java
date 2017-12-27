/*
 * 規則音声合成エンジン AquesTalk2
 *
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk2.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


/**
 * 音声記号列から音声波形データを生成し、サウンドデバイスに出力する
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalk2Da extends Library {

    public static final AquesTalk2Da INSTANCE = (AquesTalk2Da) Native.loadLibrary(
        System.getProperty("os.name").startsWith("Windows") ? "AquesTalkDa" : "AquesTalkDa",
        AquesTalk2Da.class);

    /**
     * 音声を合成して出力
     *
     * @param data
     * @param size
     */
    void PlaySound(Pointer data, int size);
}

/* */
