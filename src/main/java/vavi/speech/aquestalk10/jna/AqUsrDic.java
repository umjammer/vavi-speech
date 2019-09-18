//
// Copyright AQUEST Corp. 2017-. All Rights Reserved.
// An unpublished and CONFIDENTIAL work. Reproduction, adaptation, or
// translation without prior written permission is prohibited except
// as allowed under the copyright laws.
//

package vavi.speech.aquestalk10.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;


/**
 * AqUsrDic - ユーザ辞書ライブラリ
 * AqKanji2Koe/AqKanji2Roman用
 * <p>
 * 【ディレクトリ構成】
 * <pre>
 * <aq_dic>
 * |- aqdic.binシステム辞書
 * |- aq_usr.dicユーザ辞書
 * </pre>
 * </p>
 * <p>
 * 【注意】
 * ユーザ辞書はシステム辞書に依存するため、
 * 異なるシステム辞書との組み合わせは正常動作しない。
 * </p>
 * <p>
 * 品詞コード(posCode)：品詞名
 * <ul>
 * <li>0:名詞
 * <li>1:名詞(サ変)
 * <li>2:人名
 * <li>3:人名(姓)
 * <li>4:人名(名)
 * <li>5:固有名詞
 * <li>6:固有名詞(組織)
 * <li>7:固有名詞(地域)
 * <li>8:固有名詞(国)
 * <li>9:代名詞
 * <li>10:代名詞(縮約)
 * <li>11:名詞(副詞可能)
 * <li>12:名詞(接続詞的)
 * <li>13:名詞(形容動詞語幹)
 * <li>14:名詞(ナイ形容詞語幹)
 * <li>15:形容詞
 * <li>16:副詞
 * <li>17:副詞(助詞類接続)
 * <li>18:接頭詞(名詞接続)
 * <li>19:接頭詞(動詞接続)
 * <li>20:接頭詞(数接続)
 * <li>21:接頭詞(形容詞接続)
 * <li>22:接続詞
 * <li>23:連体詞
 * <li>24:記号
 * <li>25:記号(アルファベット)
 * <li>26:感動詞
 * <li>27:間投詞
 * </ul>
 * </p>
 * 2017/05/11 N.Yamazaki Creation.
 */
public interface AqUsrDic extends Library {

    public static final AqUsrDic INSTANCE = (AqUsrDic) Native
            .loadLibrary(System.getProperty("os.name").startsWith("Windows") ? "AqUsrDic" : "AqUsrDic", AqUsrDic.class);

    /**
     * CSV辞書からユーザ辞書(aq_usr.dic)を生成(上書)
     * aq_user.dicと同じディレクトリにシステム辞書(aqdic.bin)があること
     *
     * @param pathUserDic ユーザ辞書(aq_user.dic)ファイルのパス
     * @param pathDicCsv CSV辞書ファイルのパス
     * @return 0：正常終了それ以外：エラー
     */
    int AqUsrDic_Import(final String pathUserDic, final String pathDicCsv);

    /**
     * ユーザ辞書(aq_usr.dic)からCSV辞書を生成
     * aq_user.dicと同じディレクトリにシステム辞書(aqdic.bin)があること
     *
     * @param pathUserDic ユーザ辞書(aq_user.dic)ファイルのパス
     * @param pathDicCsv CSV辞書ファイルのパス
     * @return 0：正常終了それ以外：エラー
     */
    int AqUsrDic_Export(final String pathUserDic, final String pathDicCsv);

    /**
     * 書式のチェック
     * CSV辞書に見出し語を追加・修正するとき、ユーザ辞書の生成前に
     * 読み記号列の書式などをチェック
     *
     * @param surface 見出し語文字列(UTF8)
     * @param yomi よみ記号列（アクセント付き発音記号列 UTF8）
     * @param posCode 品詞コード（下記参照）
     * @return 0：チェックOKそれ以外：エラー
     */
    int AqUsrDic_Check(final String surface, final String yomi, int posCode);

    /**
     * 最後のエラーの詳細メッセージを返す
     *
     * @return エラーメッセージ（UTF8, NULL終端)
     */
    String AqUsrDic_GetLastError();
}
