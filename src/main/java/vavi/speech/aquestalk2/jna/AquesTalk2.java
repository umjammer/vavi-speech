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
 * 音声記号列から音声波形データをメモリ上に生成する
 * 出力音声波形は、8HKzサンプリング, 16bit,モノラル,WAVフォーマット
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalk2 extends Library {

    public static final AquesTalk2 INSTANCE = (AquesTalk2) Native.loadLibrary(
        System.getProperty("os.name").startsWith("Windows") ? "AquesTalk" : "AquesTalk",
        AquesTalk2.class);

    /**
     * 音声記号列から音声波形を生成 音声波形データは内部で領域確保される。
     * 音声波形データの解放は本関数の呼び出し側で{@link #AquesTalk_FreeWave()}にて行う
     * 
     * @param koe[in] 音声記号列
     * @param iSpeed[in] 発話速度 [%] 50-300 の間で指定
     * @param pSize[out] 生成した音声データのサイズ[byte]（エラーの場合はエラーコードが返る）
     * @return WAVフォーマットの音声データの先頭アドレス。エラー時はnullが返る
     */
    Pointer AquesTalk2_Synthe_Utf8(String koe, int iSpeed, int[] pSize, Pointer phont);

    /**
     * 音声データの領域を開放
     * 
     * @param wav[in] {@link #AquesTalk_Synthe(String, int, int[])}で返されたアドレスを指定
     */
    void AquesTalk2_FreeWave(Pointer wav);
}

/* */
