//////////////////////////////////////////////////////////////////////
/*!	@class	CAquesTalk

	@brief	�K�����������G���W�� AquesTalk

  �����L���񂩂特���g�`�f�[�^����������ɐ�������
  �o�͉����g�`�́A8HKz�T���v�����O, 16bit,���m����,WAV�t�H�[�}�b�g


	@author	N.Yamazaki (Aquest)

	@date	2006/05/08	N.Yamazaki	Creation
	@date	2006/10/03	N.Yamazaki	Ver.2.0
	@date	2006/10/12	N.Yamazaki	Ver.2.1 .def�t�@�C���̎g�p��__stdcall�̒�`
*/
//	���̃\�[�X�R�[�h�̗��p�����ɂ��ẮA
//	������ AqLicense.txt ��������������
//  COPYRIGHT (C) 2006 AQUEST CORP.
//////////////////////////////////////////////////////////////////////
#if !defined(_AQUESTALK_H_)
#define _AQUESTALK_H_
#ifdef __cplusplus
extern "C"{
#endif

#if defined(AQUESTALK_EXPORTS)
#undef	DllExport
#define DllExport	__declspec( dllexport )
#else
#define DllExport
#endif


/////////////////////////////////////////////
//!	�����L���񂩂特���g�`�𐶐�
//!	�����g�`�f�[�^�͓����ŗ̈�m�ۂ����B
//!	�����g�`�f�[�^�̉���͖{�֐��̌Ăяo������AquesTalk_FreeWave()�ɂčs��
//! @param	koe[in]		�����L����iNULL�I�[�j
//! @param	iSpeed[in]	���b���x [%] 50-300 �̊ԂŎw��
//!	@param	pSize[out]	�������������f�[�^�̃T�C�Y[byte]�i�G���[�̏ꍇ�̓G���[�R�[�h���Ԃ�j
//!	@return	WAV�t�H�[�}�b�g�̉����f�[�^�̐擪�A�h���X�B�G���[����NULL���Ԃ�
DllExport unsigned char * __stdcall AquesTalk_Synthe(const char *koe, int iSpeed, int *pSize);

/////////////////////////////////////////////
//!	�����f�[�^�̗̈���J��
//!	@param  wav[in]		AquesTalk_Synthe()�ŕԂ��ꂽ�A�h���X���w��
DllExport void __stdcall AquesTalk_FreeWave(unsigned char *wav);


#ifdef __cplusplus
}
#endif
#endif // !defined(_AQUESTALK_H_)
