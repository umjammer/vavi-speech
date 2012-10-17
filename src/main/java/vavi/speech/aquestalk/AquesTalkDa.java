/*
 * 規則音声合成エンジン AquesTalk
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk;


/**
 * 音声記号列から音声波形データを生成し、サウンドデバイスに出力する
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class AquesTalkDa {

    /** */
    private long instance;

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
     * 音声を合成して出力（同期タイプ）
     * 音声の再生が終了するまで戻らない
     * @param koe 音声記号列
     * @param speed 発話速度 [%] 50-300 の間で指定
     * @return 0: 正常終了、それ以外: エラーコード
     */
    private native int playSync(String koe, int iSpeed);

    /** */
    public AquesTalkDa() {
        this.instance = create();
    }

    /**
     * 音声合成エンジンのインスタンスを生成（非同期タイプ）
     * @return 音声合成エンジンのハンドルを返す
     */
    private native long create();

    /** */
    protected void finalize() throws Throwable {
        release(instance);
    }

    /**
     * 音声合成エンジンのインスタンスを解放 (非同期タイプ)
     * @param hMe 音声合成エンジンのハンドル {@link #create()} で生成
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
     * 音声を合成して出力 (非同期タイプ)
     * 音声波形生成後に、すぐに戻る
     * hWndを指定すると再生終了後、msgに指定したメッセージがPostされる。
     * 再生終了前に {@link play()}を呼び出して、連続的に再生させることも可能。
     * 
     * @param instance 音声合成エンジンのハンドル {@link #create()} で生成
     * @param koe 音声記号列
     * @param speed 発話速度 [%] 50-300 の間で指定 (default 100)
     * @param handle 終了メッセージ送出先ウィンドウハンドル
     * @param message 終了メッセージ (default 0)
     * @param param 任意のユーザパラメータ(メッセージのlParam に設定される) (default 0)
     * @return 0:正常終了　それ以外：エラーコード
     */
    private native int play(long instance, String koe, int speed, int handle, String message, int param);

    /** */
    public void stop() {
        stop(instance);
    }

    /**
     * 再生の中止 
     * {@link #play()}で再生中に、再生を中断する。
     * 再生中(再生待ちを含む）であり、終了メッセージ送出先が指定されていたなら、
     * 終了メッセージがPostされる。
     * @param instance 音声合成エンジンのハンドル {@link #create()} で生成
     */
    private native void stop(long instance);

    /** */
    public boolean isPlay() {
        return isPlay(instance) != 0;
    }

    /**
     * 再生中か否か
     * @param instance 音声合成エンジンのハンドル {@link #create()} で生成
     * @return 1:再生中 0:再生中でない
     */
    private native int isPlay(long instance);
}

/* */
