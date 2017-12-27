/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

#include "vavi_speech_aquestalk2_AquesTalk2.h"
#include "vavi_speech_aquestalk2_AquesTalk2Da.h"
#import <Cocoa/Cocoa.h>
#import <AquesTalkEva/AquesTalkF.h>
#include "au_play.h"


static char *phont = NULL;
static jbyteArray jdata = NULL;

/*
 * Class:     vavi_speech_aquestalk2_AquesTalk2
 * Method:    synthe
 * Signature: (Ljava/lang/String;I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_vavi_speech_aquestalk2_AquesTalk2_synthe
  (JNIEnv *env, jobject obj, jstring koe, jint iSpeed) {

    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

    const char *_koe = (*env)->GetStringUTFChars(env, koe, NULL);

    int size;
    unsigned char *wav = AquesTalk2_Synthe_Utf8(_koe, (int) iSpeed, &size, phont);

    (*env)->ReleaseStringChars(env, koe, _koe);

    jbyteArray jb = (*env)->NewByteArray(env, size);
    (*env)->SetByteArrayRegion(env, jb, 0, size, (jbyte *) wav);

    AquesTalk2_FreeWave(wav);

    [pool drain];

    return jb;
}

/*
 * Class:     vavi_speech_aquestalk2_AquesTalk2Da
 * Method:    playSync
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_vavi_speech_aquestalk2_AquesTalk2Da_playSync
  (JNIEnv *env, jobject obj, jstring koe, jint iSpeed) {

    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

    const char *_koe = (*env)->GetStringUTFChars(env, koe, NULL);

    int size;
    unsigned char *wav = AquesTalk2_Synthe_Utf8(_koe, (int) iSpeed, &size, phont);

    (*env)->ReleaseStringChars(env, koe, _koe);

    PlaySound((short *)(wav + 44), (size - 44) /2);

    AquesTalk2_FreeWave(wav);

    [pool drain];

    return wav ? 0 : -1;
}

/*
 * Class:     vavi_speech_aquestalk2_AquesTalk2Da
 * Method:    create
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL Java_vavi_speech_aquestalk2_AquesTalk2Da_create
  (JNIEnv *env, jobject obj) {

    return (jlong) 0;
}

/*
 * Class:     vavi_speech_aquestalk2_AquesTalk2Da
 * Method:    create
 * Signature: ()I
 */
JNIEXPORT void JNICALL Java_vavi_speech_aquestalk2_AquesTalk2Da_setPhont
  (JNIEnv *env, jobject obj, jbyteArray data) {

    phont = (*env)->GetByteArrayElements(env, data, NULL);
    jdata = data;
}

/*
 * Class:     vavi_speech_aquestalk2_AquesTalk2Da
 * Method:    release
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_vavi_speech_aquestalk2_AquesTalk2Da_release
  (JNIEnv *env, jobject obj, jlong hMe) {

    if (phont) {
        (*env)->ReleaseByteArrayElements(env, jdata, phont, 0);
    }
}

/* */
