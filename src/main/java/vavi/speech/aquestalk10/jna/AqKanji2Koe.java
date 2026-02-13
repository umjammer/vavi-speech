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
 * AquesTalk(2) language processor
 *
 * Kanji/Kana mixed text -> phonetic symbol string
 *
 * @author N.Yamazaki (Aquest)
 * @version 2010/12/27 N.Yamazaki Creation
 *          2013/06/11 N.Yamazaki Ver.2
 *          2017/11/12 N.Yamazaki Ver.3
 */
public interface AqKanji2Koe extends Library {

    AqKanji2Koe INSTANCE = Native.load("AqKanji2Koe", AqKanji2Koe.class);

    /**
     * Language processing instance creation (initialization)
     * 
     * @param pathDic [in] Specify the dictionary directory (with or without the / at the end)
     * @param pErr [out] If an error occurs, an error code is entered. If the operation is successful,
     *            an undefined value is entered.
     * @return Instance handle. Returns 0 if an error occurs.
     */
    Pointer AqKanji2Koe_Create(String pathDic, int[] pErr);

    /**
     * Language processing instance creation (initialization) part 2
     * The caller loads the dictionary data (binary) into memory and then specifies
     * You can use memory-mapped files to speed up initialization.
     * 
     * @param pSysDic [in] System dictionary data start address (required)
     * @param pUserDic [in] User dictionary data start address (specify 0 if not used)
     * @param pErr [out] If an error occurs, an error code is entered. If the operation is successful,
     *            an undefined value is entered.
     * @return Instance handle. Returns 0 if an error occurs.
     */
    Pointer AqKanji2Koe_Create_Ptr(Pointer pSysDic, Pointer pUserDic, int[] pErr);

    /**
     * Instance release
     * param hAqKanji2Koe[in] Instance handle returned by AqKanji2Koe_Create()
     */
    void AqKanji2Koe_Release(Pointer hAqKanji2Koe);

    /**
     * Language processing Multibyte character (UTF8) version
     *
     * @param hAqKanji2Koe [in] Instance handle returned by AqKanji2Koe_Create()
     * @param kanji [in] Kanji-Kana mixed text (UTF8)
     * @param koe [out] Phonetic symbol string (UTF8)
     * @param nBufKoe [in] koe buffer size [byte]
     * @return 0: Normal completion Other: Error code
     */
    int AqKanji2Koe_Convert(Pointer hAqKanji2Koe, String kanji, String koe, int nBufKoe);

    /**
     * Development license key settings
     * Call once before generating an audio waveform.
     * This removes the limitations of the evaluation version.
     *
     * @param devKey [in] Specify the development license key
     * @return If the license key is correct, 0 is returned; if not, 1 is returned.
     *         To prevent key analysis, the function may return 0 even for invalid keys.
     *         In this case, the restriction will not be lifted.
     */
    int AqKanji2Koe_SetDevKey(byte[] devKey);
}
