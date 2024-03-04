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
 * 規則音声合成エンジン AquesTalk10
 *
 * 音声記号列から音声波形データをメモリ上に生成する
 * 出力音声波形は、16KHz, 16bit,モノラル,WAVフォーマット。
 * サンプリング周波数は、声種パラメータfscにより変化
 *
 * @author N.Yamazaki (AQUEST)
 * @date 2017/11/01 N.Yamazaki Creation for mac
 */
public interface AquesTalk10 extends Library {

    AquesTalk10 INSTANCE = Native.load("AquesTalk10", AquesTalk10.class);

    /** 声質パラメータ */
    class AQTK_VOICE extends Structure {
        /**
         * @param bas
         * @param spd
         * @param vol
         * @param pit
         * @param acc
         * @param lmd
         * @param fsc
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
        /** 基本素片 F1E/F2E/M1E (0/1/2) */
        public int bas;
        /** 話速 50-300 default:100 */
        public int spd;
        /** 音量 0-300 default:100 */
        public int vol;
        /** 高さ 20-200 default:基本素片に依存 */
        public int pit;
        /** アクセント 0-200 default:基本素片に依存 */
        public int acc;
        /** 音程１ 0-200 default:100 */
        public int lmd;
        /** 音程２(サンプリング周波数) 50-200 default:100 */
        public int fsc;
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("bas", "spd", "vol", "pit", "acc", "lmd", "fsc");
        }
    }

    /** 基本素片 */
    enum VoiceBase {
        F1E,
        F2E,
        M1E
    }

    // プリセット声種

    /** 女声 F1 */
    AQTK_VOICE gVoice_F1 = new AQTK_VOICE(VoiceBase.F1E, 100, 100, 100, 100, 100, 100);
    /** 女声 F2 */
    AQTK_VOICE gVoice_F2 = new AQTK_VOICE(VoiceBase.F2E, 100, 100, 77, 150, 100, 100);
    /** 女声 F3 */
    AQTK_VOICE gVoice_F3 = new AQTK_VOICE(VoiceBase.F1E, 80, 100, 100, 100, 61, 148);
    /** 男声 M1 */
    AQTK_VOICE gVoice_M1 = new AQTK_VOICE(VoiceBase.M1E, 100, 100, 30, 100, 100, 100);
    /** 男声 M2 */
    AQTK_VOICE gVoice_M2 = new AQTK_VOICE(VoiceBase.M1E, 105, 100, 45, 130, 120, 100);
    /** ロボット R1 */
    AQTK_VOICE gVoice_R1 = new AQTK_VOICE(VoiceBase.M1E, 100, 100, 30, 20, 190, 100);
    /** ロボット R2 */
    AQTK_VOICE gVoice_R2 = new AQTK_VOICE(VoiceBase.F2E, 70, 100, 50, 50, 50, 180);

    /**
     * 音声記号列から音声波形を生成
     * 音声波形データは内部で領域確保される。
     * 音声波形データの解放は本関数の呼び出し側でAquesTalk_FreeWave()にて行うこと
     *
     * @param pParam [in] 声質パラメータ (AQTK_VOICE構造体のアドレス)
     * @param koe [in] 音声記号列（UTF8 NULL終端 BOM無し）
     * @param pSize [out] 生成した音声データのサイズ[byte]（エラーの場合はエラーコードが返る）
     * @return WAVフォーマットの音声データの先頭アドレス。エラー時はNULLが返る
     */
    Pointer AquesTalk_Synthe_Utf8(AQTK_VOICE pParam, String koe, int[] pSize);

    /**
     * 音声データの領域を開放
     * @param wav [in] AquesTalk_Synthe()で返されたアドレスを指定
     */
    void AquesTalk_FreeWave(Pointer wav);

    /**
     * 開発ライセンスキー設定
     * 音声波形を生成する前に一度呼び出す。
     * これにより評価版の制限がなくなる。
     * @param key [in] ライセンスキーを指定
     * @return ライセンスキーが正しければ0、正しくなければ1が返る
     * キーの解析を防ぐため不正なキーでも0を返す場合がある。このとき制限は解除されない。
     */
    int AquesTalk_SetDevKey(byte[] key);

    /**
     * 使用ライセンスキー設定
     * 音声波形を生成する前に一度呼び出す。
     * 以降、合成音声データに含まれる透かしが、使用ライセンス無しから取得済みに変化する
     * @param key [in] ライセンスキーを指定
     * @return ライセンスキーが正しければ0、正しくなければ1が返る
     * キーの解析を防ぐため不正なキーでも0を返す場合がある。この場合、ライセンス無のままである。
     */
    int AquesTalk_SetUsrKey(byte[] key);

    /** メソッドが返すエラーコードの内容 */
    Map<Integer, String> errors = new HashMap<>() {{
        put(100, "その他のエラー");
        put(101, "メモリ不足");
        put(103, "音声記号列指定エラー(語頭の長音、促音の連続など)");
        put(104, "音声記号列に有効な読みがない");
        put(105, "音声記号列に未定義の読み記号が指定された");
        put(106, "音声記号列のタグの指定が正しくない");
        put(107, "タグの長さが制限を越えている（または[>]がみつからない）");
        put(108, "タグ内の値の指定が正しくない");
        put(120, "音声記号列が長すぎる");
        put(121, "１つのフレーズ中の読み記号が多すぎる");
        put(122, "音声記号列が長い（内部バッファオーバー1）");
    }};
}
