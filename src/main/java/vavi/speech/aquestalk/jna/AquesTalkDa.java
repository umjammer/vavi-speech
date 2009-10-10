/*
 * �K�����������G���W�� AquesTalk
 * 
 * COPYRIGHT (C) 2006 AQUEST CORP.
 */

package vavi.speech.aquestalk.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;


/**
 * �����L���񂩂特���g�`�f�[�^�𐶐����A�T�E���h�f�o�C�X�ɏo�͂���
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public interface AquesTalkDa extends Library {

    public static final AquesTalkDa INSTANCE = (AquesTalkDa) Native.loadLibrary(
        System.getProperty("os.name").startsWith("Windows") ? "AquesTalkDa" : "AquesTalkDa",
        AquesTalkDa.class);

    /**
     * �������������ďo�́i�����^�C�v�j
     * �����̍Đ����I������܂Ŗ߂�Ȃ�
     * @param  koe[in]     �����L����iNULL�I�[�j
     * @param  iSpeed[in]  ���b���x [%] 50-300 �̊ԂŎw�� (=100)
     * @return 0:����I���@����ȊO�F�G���[�R�[�h
     */
    int AquesTalkDa_PlaySync(String koe, int iSpeed);

    /**
     * ���������G���W���̃C���X�^���X�𐶐��i�񓯊��^�C�v�j
     * @return ���������G���W���̃n���h����Ԃ�
     */
    int AquesTalkDa_Create();

    /**
     * ���������G���W���̃C���X�^���X������i�񓯊��^�C�v�j
     * @param  hMe[in]     ���������G���W���̃n���h�� AquesTalkDa_Create()�Ő���
     */
    void AquesTalkDa_Release(int hMe);

    /**
     * �������������ďo�́i�񓯊��^�C�v�j
     * �����g�`������ɁA�����ɖ߂�
     * hWnd���w�肷��ƍĐ��I����Amsg�Ɏw�肵�����b�Z�[�W��Post�����B
     * �Đ��I���O��AquesTalkDa_Play()���Ăяo���āA�A���I�ɍĐ������邱�Ƃ��\�B
     * 
     * @param  hMe[in]     ���������G���W���̃n���h�� AquesTalkDa_Create()�Ő���
     * @param  koe[in]     �����L����iNULL�I�[�j
     * @param  iSpeed[in]  ���b���x [%] 50-300 �̊ԂŎw�� (default 100)
     * @param  hWnd[in]    �I�����b�Z�[�W���o��E�B���h�E�n���h��
     * @param  msg[in]     �I�����b�Z�[�W (default 0)
     * @param  dwUser[in]  �C�ӂ̃��[�U�p�����[�^(���b�Z�[�W��lParam �ɐݒ肳���) (default 0)
     * @return 0:����I���@����ȊO�F�G���[�R�[�h
     */
    int AquesTalkDa_Play(int hMe, String koe, int iSpeed, int hWnd, int msg, int dwUser);

    /**
     * �Đ��̒��~ 
     * AquesTalkDa_Play()�ōĐ����ɁA�Đ��𒆒f����B
     * �Đ���(�Đ��҂����܂ށj�ł���A�I�����b�Z�[�W���o�悪�w�肳��Ă����Ȃ�A
     * �I�����b�Z�[�W��Post�����B
     * @param  hMe[in]     ���������G���W���̃n���h�� AquesTalkDa_Create()�Ő���
     */
    void AquesTalkDa_Stop(int hMe);

    /**
     * �Đ������ۂ�
     * @param  hMe[in]     ���������G���W���̃n���h�� AquesTalkDa_Create()�Ő���
     * @return 1:�Đ��� 0:�Đ����łȂ�
     */
    int AquesTalkDa_IsPlay(int hMe);
}

/* */
