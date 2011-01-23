/*
 * �K�����������G���W�� AquesTalk2
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk2.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


/**
 * �����L���񂩂特���g�`�f�[�^�𐶐����A�T�E���h�f�o�C�X�ɏo�͂���
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalk2Da extends Library {

    public static final AquesTalk2Da INSTANCE = (AquesTalk2Da) Native.loadLibrary(
        System.getProperty("os.name").startsWith("Windows") ? "AquesTalkDa" : "AquesTalkDa",
        AquesTalk2Da.class);

    /**
     * �������������ďo��
     * 
     * @param data 
     * @param size
     */
    void PlaySound(Pointer data, int size);
}

/* */
