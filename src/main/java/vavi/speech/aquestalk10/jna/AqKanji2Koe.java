//
// Copyright AQUEST Corp. 2006-. All Rights Reserved.
// An unpublished and CONFIDENTIAL work. Reproduction, adaptation, or
// translation without prior written permission is prohibited except
// as allowed under the copyright laws.
//

package vavi.speech.aquestalk10.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


/**
 * AquesTalk(2)用言語処理部
 *
 * 漢字かな混じりテキスト->音声記号列
 *
 * @author N.Yamazaki (Aquest)
 * @date 2010/12/27 N.Yamazaki Creation
 * @date 2013/06/11 N.Yamazaki Ver.2
 * @date 2017/11/12 N.Yamazaki Ver.3
 */
public interface AqKanji2Koe extends Library {

    public static final AqKanji2Koe INSTANCE = (AqKanji2Koe) Native
            .loadLibrary(System.getProperty("os.name").startsWith("Windows") ? "AqKanji2Koe" : "AqKanji2Koe",
                         AqKanji2Koe.class);

    /**
     * 言語処理インスタンス生成（初期化）
     * 
     * @param pathDic [in] 辞書のディレクトリを指定(最後に/が有っても無くても良い）
     * @param pErr [out] エラー時にはエラーコードが入る 正常終了時は不定値
     * @return インスタンスハンドル エラーの時は０が返る
     */
    Pointer AqKanji2Koe_Create(String pathDic, int[] pErr);

    /**
     * 言語処理インスタンス生成 （初期化）その２
     * 呼び出し側で辞書データ(バイナリ)をメモリに読み込んでから指定
     * 初期化を高速化するためのメモリマップトファイルなどが使える
     * 
     * @param pSysDic [in] システム辞書データ先頭アドレス(必須)
     * @param pUserDic [in] ユーザ辞書データ先頭アドレス（使用しないときは0を指定）
     * @param pErr [out] エラー時にはエラーコードが入る 正常終了時は不定値
     * @return インスタンスハンドル エラーの時は０が返る
     */
    Pointer AqKanji2Koe_Create_Ptr(Pointer pSysDic, Pointer pUserDic, int[] pErr);

    /**
     * インスタンス解放
     * param hAqKanji2Koe[in] AqKanji2Koe_Create()で返されたインスタンスハンドル
     */
    void AqKanji2Koe_Release(Pointer hAqKanji2Koe);

    /**
     * 言語処理 マルチバイト文字(UTF8)版
     *
     * @param hAqKanji2Koe [in] AqKanji2Koe_Create()で返されたインスタンスハンドル
     * @param kanji [in] 漢字かな混じり文テキスト（UTF8)
     * @param koe [out] 音声記号列（UTF8)
     * @param nBufKoe [in] koeのバッファサイズ[byte]
     * @return 0:正常終了 それ以外：エラーコード
     */
    int AqKanji2Koe_Convert(Pointer hAqKanji2Koe, final String kanji, String koe, int nBufKoe);

    /**
     * 開発ライセンスキー設定
     * 音声波形を生成する前に一度呼び出す。
     * これにより評価版の制限がなくなる。
     *
     * @param devKey [in] 開発ライセンスキーを指定
     * @return ライセンスキーが正しければ0、正しくなければ1が返る
     *         キーの解析を防ぐため不正なキーでも0を返す場合がある。このとき制限は解除されない。
     */
    int AqKanji2Koe_SetDevKey(final byte[] devKey);
}
