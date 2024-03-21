/*
 * 規則音声合成エンジン AquesTalk
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;


/**
 * 音声記号列から音声波形データを生成し、サウンドデバイスに出力する
 *
 * 32 bit mode only
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalkDa extends Library {

    AquesTalkDa INSTANCE = Native.load("AquesTalkDa", AquesTalkDa.class);

    /**
     * 音声を合成して出力（同期タイプ）
     * 音声の再生が終了するまで戻らない
     * @param koe [in] 音声記号列（NULL終端）
     * @param iSpeed [in] 発話速度 [%] 50-300 の間で指定 (=100)
     * @return 0:正常終了 それ以外：エラーコード
     */
    int AquesTalkDa_PlaySync(String koe, int iSpeed);

    /**
     * 音声合成エンジンのインスタンスを生成（非同期タイプ）
     * @return 音声合成エンジンのハンドルを返す
     */
    int AquesTalkDa_Create();

    /**
     * 音声合成エンジンのインスタンスを解放（非同期タイプ）
     * @param hMe [in] 音声合成エンジンのハンドル AquesTalkDa_Create()で生成
     */
    void AquesTalkDa_Release(int hMe);

    /**
     * 音声を合成して出力（非同期タイプ）
     * 音声波形生成後に、すぐに戻る
     * hWndを指定すると再生終了後、msgに指定したメッセージがPostされる。
     * 再生終了前にAquesTalkDa_Play()を呼び出して、連続的に再生させることも可能。
     * @param hMe [in] 音声合成エンジンのハンドル AquesTalkDa_Create()で生成
     * @param koe [in] 音声記号列（NULL終端）
     * @param iSpeed [in] 発話速度 [%] 50-300 の間で指定 (default 100)
     * @param hWnd [in] 終了メッセージ送出先ウィンドウハンドル
     * @param msg [in] 終了メッセージ (default 0)
     * @param dwUser [in] 任意のユーザパラメータ(メッセージのlParam に設定される) (default 0)
     * @return 0:正常終了 それ以外：エラーコード
     */
    int AquesTalkDa_Play(int hMe, String koe, int iSpeed, int hWnd, int msg, int dwUser);

    /**
     * 再生の中止
     * AquesTalkDa_Play()で再生中に、再生を中断する。
     * 再生中(再生待ちを含む）であり、終了メッセージ送出先が指定されていたなら、
     * 終了メッセージがPostされる。
     * @param hMe [in] 音声合成エンジンのハンドル AquesTalkDa_Create()で生成
     */
    void AquesTalkDa_Stop(int hMe);

    /**
     * 再生中か否か
     * @param hMe [in] 音声合成エンジンのハンドル AquesTalkDa_Create()で生成
     * @return 1:再生中 0:再生中でない
     */
    int AquesTalkDa_IsPlay(int hMe);
}
