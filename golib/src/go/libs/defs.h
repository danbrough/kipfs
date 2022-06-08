
#ifndef __DEFS_H__
#define __DEFS_H__

#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>

typedef struct {
  int a;
  double b;
} MyStruct;

typedef void (*DataCallbackFunc)(void* data,int len,const char* err);

void StructTest(MyStruct s);

void testCallback(void *data,int len,const char* err);

void bridgeDataCallback(DataCallbackFunc callback,void *data,int len,const char* err);

void pass_string(char* str);
char* return_string();
int copy_string(char* str, int size);

#endif 