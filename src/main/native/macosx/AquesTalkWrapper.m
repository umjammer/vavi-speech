/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

#include "vavi_speech_aquestalk_AquesTalk.h"
#include "vavi_speech_aquestalk_AquesTalkDa.h"
#import <Cocoa/Cocoa.h>
#import <AquesTalkEva/AquesTalkF.h>
#include "au_play.h"


/*
 * Class:     vavi_speech_aquestalk_AquesTalk
 * Method:    synthe
 * Signature: (Ljava/lang/String;I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_vavi_speech_aquestalk_AquesTalk_synthe
  (JNIEnv *env, jobject obj, jstring koe, jint iSpeed) {

    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

    const char *_koe = (*env)->GetStringUTFChars(env, koe, NULL);
    char* __koe = [[NSString stringWithUTF8String:_koe] cStringUsingEncoding:NSShiftJISStringEncoding];
    (*env)->ReleaseStringChars(env, koe, _koe);

    int size;
    unsigned char *wav = AquesTalk_SyntheMV(0, __koe, (int) iSpeed, &size);

    jbyteArray jb = (*env)->NewByteArray(env, size);
    (*env)->SetByteArrayRegion(env, jb, 0, size, (jbyte *) wav);

    AquesTalk_FreeWave(wav);

    [pool drain];

    return jb;
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    playSync
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_playSync
  (JNIEnv *env, jobject obj, jstring koe, jint iSpeed) {

    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

    const char *_koe = (*env)->GetStringUTFChars(env, koe, NULL);
//NSLog([NSString stringWithUTF8String:_koe]);
    char* __koe = [[NSString stringWithUTF8String:_koe] cStringUsingEncoding:NSShiftJISStringEncoding];
//fprintf(stderr, "koe: %s\n", __koe);
//fflush(stderr);
    (*env)->ReleaseStringChars(env, koe, _koe);

    int size;
    unsigned char *wav = AquesTalk_SyntheMV(0, __koe, (int) iSpeed, &size);

    PlaySound((short *)(wav + 44), (size - 44) /2);

    AquesTalk_FreeWave(wav);

    [pool drain];

    return wav ? 0 : -1;
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    create
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_create
  (JNIEnv *env, jobject obj) {

    return (jlong) 1234;
}

/*
 * Class:     vavi_speech_aquestalk_AquesTalkDa
 * Method:    release
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_vavi_speech_aquestalk_AquesTalkDa_release
  (JNIEnv *env, jobject obj, jlong hMe) {

}

/* */
