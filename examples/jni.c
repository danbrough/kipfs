

#include <jni.h>
#include <stdlib.h>
#include <stdbool.h>

#include "golib.h"
#include "libkipfs.h"

JNIEXPORT jstring JNICALL
Java_golib_getMessage(JNIEnv *env, jclass clazz) {
    printf("Running jni code ..\n");
  char * msg = KGetMessage();
  jstring s = (*env)->NewStringUTF(env, msg);
  free(msg);
  return s;
}

JNIEXPORT jstring JNICALL
Java_golib_dagCID(JNIEnv *env, jclass clazz,jstring json) {
  const char *jsonC = (*env)->GetStringUTFChars(env,json, NULL);//Java String to C Style string
  char * msg = KCID((char*)jsonC);
  (*env)->ReleaseStringUTFChars(env,json, jsonC);
  jstring s = (*env)->NewStringUTF( env,msg);
  free(msg);
  return s;
}

/*
JNIEXPORT jboolean JNICALL Java_danbroid_kipfs_jni_KIPFS_repoIsInitialialized
  (JNIEnv *env, jclass clz, jstring repoPathJ){
  const char *repoPathC = (*env)->GetStringUTFChars(env,repoPathJ, NULL);//Java String to C Style string
  bool success =  KRepoIsInitialized((char*)repoPathC);
  (*env)->ReleaseStringUTFChars(env,repoPathJ, repoPathC);
  return success;
  }




JNIEXPORT jstring JNICALL Java_danbroid_kipfs_jni_KIPFS_initRepo
  (JNIEnv *env, jclass clz, jstring repoPathJ){
    const char *repoPathC = (*env)->GetStringUTFChars(env,repoPathJ, NULL);//Java String to C Style string
    char * err = KInitRepo((char*)repoPathC);
    jstring errJ = NULL;
    if (err != NULL){
        errJ = (*env)->NewStringUTF( env,err);
        free(err);
    }
    (*env)->ReleaseStringUTFChars(env,repoPathJ, repoPathC);


    return errJ;
  }

*/
