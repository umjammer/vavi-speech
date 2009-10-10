/*
 * �K�����������G���W�� AquesTalk
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk;


/**
 * �����L���񂩂特���g�`�f�[�^�𐶐����A�T�E���h�f�o�C�X�ɏo�͂���
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class AquesTalkDa {

    /** */
    private int instance;

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
     * �������������ďo�́i�����^�C�v�j
     * �����̍Đ����I������܂Ŗ߂�Ȃ�
     * @param koe �����L����
     * @param speed ���b���x [%] 50-300 �̊ԂŎw��
     * @return 0: ����I���A����ȊO: �G���[�R�[�h
     */
    private native int playSync(String koe, int iSpeed);

    /** */
    public AquesTalkDa() {
        this.instance = create();
    }

    /**
     * ���������G���W���̃C���X�^���X�𐶐��i�񓯊��^�C�v�j
     * @return ���������G���W���̃n���h����Ԃ�
     */
    private native int create();

    /** */
    protected void finalize() throws Throwable {
        release(instance);
    }

    /**
     * ���������G���W���̃C���X�^���X����� (�񓯊��^�C�v)
     * @param hMe ���������G���W���̃n���h�� {@link #create()} �Ő���
     */
    private native void release(int instance);

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
     * �������������ďo�� (�񓯊��^�C�v)
     * �����g�`������ɁA�����ɖ߂�
     * hWnd���w�肷��ƍĐ��I����Amsg�Ɏw�肵�����b�Z�[�W��Post�����B
     * �Đ��I���O�� {@link play()}���Ăяo���āA�A���I�ɍĐ������邱�Ƃ��\�B
     * 
     * @param instance ���������G���W���̃n���h�� {@link #create()} �Ő���
     * @param koe �����L����
     * @param speed ���b���x [%] 50-300 �̊ԂŎw�� (default 100)
     * @param handle �I�����b�Z�[�W���o��E�B���h�E�n���h��
     * @param message �I�����b�Z�[�W (default 0)
     * @param param �C�ӂ̃��[�U�p�����[�^(���b�Z�[�W��lParam �ɐݒ肳���) (default 0)
     * @return 0:����I���@����ȊO�F�G���[�R�[�h
     */
    private native int play(int instance, String koe, int speed, int handle, String message, int param);

    /** */
    public void stop() {
        stop(instance);
    }

    /**
     * �Đ��̒��~ 
     * {@link #play()}�ōĐ����ɁA�Đ��𒆒f����B
     * �Đ���(�Đ��҂����܂ށj�ł���A�I�����b�Z�[�W���o�悪�w�肳��Ă����Ȃ�A
     * �I�����b�Z�[�W��Post�����B
     * @param instance ���������G���W���̃n���h�� {@link #create()} �Ő���
     */
    private native void stop(int instance);

    /** */
    public boolean isPlay() {
        return isPlay(instance) != 0;
    }

    /**
     * �Đ������ۂ�
     * @param instance ���������G���W���̃n���h�� {@link #create()} �Ő���
     * @return 1:�Đ��� 0:�Đ����łȂ�
     */
    private native int isPlay(int instance);
}

/* */
