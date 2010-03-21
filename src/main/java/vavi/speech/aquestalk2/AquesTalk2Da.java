/*
 * �K�����������G���W�� AquesTalk2
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * �����L���񂩂特���g�`�f�[�^�𐶐����A�T�E���h�f�o�C�X�ɏo�͂���
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class AquesTalk2Da {

    /** */
    private long instance;

    static {
        System.loadLibrary("AquesTalk2Wrapper");
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
    public AquesTalk2Da() {
        this.instance = create();
    }

    /** */
    public void setPhont(String file) throws IOException {
        if (file != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = new FileInputStream(new File(file));
            byte[] b = new byte[8192]; 
            while (is.available() > 0) {
                int r = is.read(b, 0, b.length);
                if (r < 0) {
                    break;
                }
                baos.write(b, 0, r);
            }
            setPhont(baos.toByteArray());
        } else {
            setPhont((byte[]) null);
        }
    }

    /** */
    private native void setPhont(byte[] data);

    /**
     * ���������G���W���̃C���X�^���X�𐶐��i�񓯊��^�C�v�j
     * @return ���������G���W���̃n���h����Ԃ�
     */
    private native long create();
    
    /** */
    protected void finalize() throws Throwable {
        release(instance);
    }

    /**
     * ���������G���W���̃C���X�^���X����� (�񓯊��^�C�v)
     * @param hMe ���������G���W���̃n���h�� {@link #create()} �Ő���
     */
    private native void release(long instance);

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
    private native int play(long instance, String koe, int speed, int handle, String message, int param);

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
    private native void stop(long instance);

    /** */
    public boolean isPlay() {
        return isPlay(instance) != 0;
    }

    /**
     * �Đ������ۂ�
     * @param instance ���������G���W���̃n���h�� {@link #create()} �Ő���
     * @return 1:�Đ��� 0:�Đ����łȂ�
     */
    private native int isPlay(long instance);
}

/* */
