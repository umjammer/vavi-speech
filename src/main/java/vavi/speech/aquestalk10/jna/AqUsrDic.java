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
 * AqUsrDic - User Dictionary Library
 * For AqKanji2Koe/AqKanji2Roman
 * <p>
 * [Directory structure]
 * <pre>
 * <aq_dic>
 * |- aqdic.bin System Dictionary
 * |- aq_usr.dic User Dictionary
 * </pre>
 * </p>
 * <p>
 * [Note]
 * Since the user dictionary depends on the system dictionary,
 * it will not work properly when combined with a different system dictionary.
 * </p>
 * <p>
 * Part of speech code (posCode): part of speech name
 * <ul>
 * <li>0:noun
 * <li>1:Noun (サ変)
 * <li>2:person's name
 * <li>3:Person name (surname)
 * <li>4:Person's name (first name)
 * <li>5:Proper nouns
 * <li>6:Proper noun (Organization)
 * <li>7:Proper noun (Region)
 * <li>8:Proper noun (Country)
 * <li>9:Pronoun
 * <li>10:Pronoun (Contraction)
 * <li>11:Noun (Adverbial)
 * <li>12:Noun (Conjunctive)
 * <li>13:Noun (Adjectival verb stem)
 * <li>14:Noun (ナイ adjective stem)
 * <li>15:Adjective
 * <li>16:Adverb
 * <li>17:Adverb (Particle connection)
 * <li>18:Prefix (Noun connection)
 * <li>19:Prefix (Verb connection)
 * <li>20:Prefix (Number connection)
 * <li>21:Prefix (Adjective connection)
 * <li>22:Conjunction
 * <li>23:Adnominal
 * <li>24:Symbol
 * <li>25:Symbol (Alphabet)
 * <li>26:Interjection
 * <li>27:Interjection (Filler)
 * </ul>
 * </p>
 * 2017/05/11 N.Yamazaki Creation.
 */
public interface AqUsrDic extends Library {

    AqUsrDic INSTANCE = Native.load("AqUsrDic", AqUsrDic.class);

    /**
     * Generate (overwrite) user dictionary (aq_usr.dic) from CSV dictionary
     * The system dictionary (aqdic.bin) must exist in the same directory as aq_user.dic
     *
     * @param pathUserDic Path to the user dictionary (aq_user.dic) file
     * @param pathDicCsv Path to the CSV dictionary file
     * @return 0: Normal termination, Other: Error
     */
    int AqUsrDic_Import(String pathUserDic, String pathDicCsv);

    /**
     * Generate CSV dictionary from user dictionary (aq_usr.dic)
     * The system dictionary (aqdic.bin) must exist in the same directory as aq_user.dic
     *
     * @param pathUserDic Path to the user dictionary (aq_user.dic) file
     * @param pathDicCsv Path to the CSV dictionary file
     * @return 0: Normal termination, Other: Error
     */
    int AqUsrDic_Export(String pathUserDic, String pathDicCsv);

    /**
     * Check format
     * When adding/modifying headwords in the CSV dictionary, before generating the user dictionary,
     * check the format of the reading symbol string, etc.
     *
     * @param surface Headword string (UTF8)
     * @param yomi Reading symbol string (Pronunciation symbol string with accent UTF8)
     * @param posCode Part of speech code (see below)
     * @return 0: Check OK, Other: Error
     */
    int AqUsrDic_Check(String surface, String yomi, int posCode);

    /**
     * Returns the detailed message of the last error
     *
     * @return Error message (UTF8, NULL terminated)
     */
    String AqUsrDic_GetLastError();
}
