/*
 * �K�����������G���W�� AquesTalk
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


/**
 * �����L���񂩂特���g�`�f�[�^����������ɐ�������
 * �o�͉����g�`�́A8HKz�T���v�����O, 16bit,���m����,WAV�t�H�[�}�b�g
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalk extends Library {

    public static final AquesTalk INSTANCE = (AquesTalk) Native.loadLibrary(
        System.getProperty("os.name").startsWith("Windows") ? "AquesTalk" : "AquesTalk",
        AquesTalk.class);

    /**
     * �����L���񂩂特���g�`�𐶐� �����g�`�f�[�^�͓����ŗ̈�m�ۂ����B
     * �����g�`�f�[�^�̉���͖{�֐��̌Ăяo������{@link #AquesTalk_FreeWave()}�ɂčs��
     * 
     * @param koe[in] �����L����
     * @param iSpeed[in] ���b���x [%] 50-300 �̊ԂŎw��
     * @param pSize[out] �������������f�[�^�̃T�C�Y[byte]�i�G���[�̏ꍇ�̓G���[�R�[�h���Ԃ�j
     * @return WAV�t�H�[�}�b�g�̉����f�[�^�̐擪�A�h���X�B�G���[����null���Ԃ�
     */
    Pointer AquesTalk_Synthe(String koe, int iSpeed, int[] pSize);

    /**
     * �����f�[�^�̗̈���J��
     * 
     * @param wav[in] {@link #AquesTalk_Synthe(String, int, int[])}�ŕԂ��ꂽ�A�h���X���w��
     */
    void AquesTalk_FreeWave(Pointer wav);
}

/* */
