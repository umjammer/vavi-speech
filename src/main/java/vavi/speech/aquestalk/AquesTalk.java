/*
 * 規則音声合成エンジン AquesTalk
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk;


/**
 * 音声記号列から音声波形データをメモリ上に生成する
 * 出力音声波形は、8HKzサンプリング, 16bit,モノラル,WAVフォーマット
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
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
     * 音声記号列から音声波形を生成。
     * 
     * @param koe 音声記号列
     * @param speed 発話速度 [%] 50-300 の間で指定
     * @return WAVフォーマットの音声データ。エラー時はnullが返る
     */
    private native byte[] synthe(String koe, int speed);
}

/* */
