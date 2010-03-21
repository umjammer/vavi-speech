/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

#include <vavi_speech_aquestalk_AquesTalk.h>
#include <vavi_speech_aquestalk_AquesTalkDa.h>
#include "AquesTalk.h"
#include "AquesTalkDa.h"
#include <windows.h>


/*
 * Class:     vavi_speech_aquestalk_AquesTalk
 * Method:    synthe
 * Signature: (Ljava/lang/String;I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_vavi_speech_aquestalk_AquesTalk_synthe
  (JNIEnv *env, jobject obj, jstring koe, jint iSpeed) {

    const char *_koe = (*env)->GetStringChars(env, koe, NULL);
    int _koe_length = (*env)->GetStringLength(env, koe);
    char* __koe = (char*) malloc(_koe_length * 2 + 1);
    int __koe_length = WideCharToMultiByte(CP_ACP, 0, _koe, _koe_length, __koe, _koe_length * 2 + 1, NULL, NULL);
    __koe[__koe_length] = '\0';
//fprintf(stderr, "koe: %s\n", __koe);
//fflush(stderr);
    (*env)->ReleaseStringChars(env, koe, _koe);

    int size;
    unsigned char *wav = AquesTalk_Synthe(__koe, (int) iSpeed, &size);

    free(__koe);

    jbyteArray jb = (*env)->NewByteArray(env, size);
    (*env)->SetByteArrayRegion(env, jb, 0, size, (jbyte *) wav);

    AquesTalk_FreeWave(wav);

    return jb;
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    playSync
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_playSync
  (JNIEnv *env, jobject obj, jstring koe, jint iSpeed) {

    const char *_koe = (*env)->GetStringChars(env, koe, NULL);
    int _koe_length = (*env)->GetStringLength(env, koe);
    char* __koe = (char*) malloc(_koe_length * 2 + 1);
    int __koe_length = WideCharToMultiByte(CP_ACP, 0, _koe, _koe_length, __koe, _koe_length * 2 + 1, NULL, NULL);
    __koe[__koe_length] = '\0';
//fprintf(stderr, "koe: %s\n", __koe);
//fflush(stderr);
    (*env)->ReleaseStringChars(env, koe, _koe);

    int result = (jint) AquesTalkDa_PlaySync(__koe, (int) iSpeed);

    free(__koe);

    return result;
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    create
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_create
  (JNIEnv *env, jobject obj) {

    return (jlong) AquesTalkDa_Create();
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    release
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_release
  (JNIEnv *env, jobject obj, jlong hMe) {

    AquesTalkDa_Release((H_AQTKDA) hMe);
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    play
 * Signature: (ILjava/lang/String;IIII)I
 */
JNIEXPORT jint JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_play
  (JNIEnv *env, jobject obj, jlong hMe, jstring koe, jint iSpeed, jint hWnd, jstring msg, jint dwUser) {

    const char *_koe = (*env)->GetStringChars(env, koe, NULL);
    int _koe_length = (*env)->GetStringLength(env, koe);
    char* __koe = (char*) malloc(_koe_length * 2 + 1);
    int __koe_length = WideCharToMultiByte(CP_ACP, 0, _koe, _koe_length, __koe, _koe_length * 2 + 1, NULL, NULL);
    __koe[__koe_length] = '\0';
fprintf(stderr, "koe: %s\n", __koe);
fflush(stderr);
    (*env)->ReleaseStringChars(env, koe, _koe);

    int result = (jint) AquesTalkDa_Play((H_AQTKDA) hMe, __koe, (int) iSpeed, (HWND) hWnd, (unsigned long) 0, (unsigned long) dwUser);

    free(__koe);

    return result;
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    stop
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_stop
  (JNIEnv *env, jobject obj, jlong hMe) {

    AquesTalkDa_Stop((H_AQTKDA) hMe);
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    isPlay
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_isPlay
  (JNIEnv *env, jobject obj, jlong hMe) {

    return (jint) AquesTalkDa_IsPlay((H_AQTKDA) hMe);
}

/* */
