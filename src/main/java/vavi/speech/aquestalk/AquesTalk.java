/*
 * �K�����������G���W�� AquesTalk
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk;


/**
 * �����L���񂩂特���g�`�f�[�^����������ɐ�������
 * �o�͉����g�`�́A8HKz�T���v�����O, 16bit,���m����,WAV�t�H�[�}�b�g
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
     * �����L���񂩂特���g�`�𐶐��B
     * 
     * @param koe �����L����
     * @param speed ���b���x [%] 50-300 �̊ԂŎw��
     * @return WAV�t�H�[�}�b�g�̉����f�[�^�B�G���[����null���Ԃ�
     */
    private native byte[] synthe(String koe, int speed);
}

/* */
