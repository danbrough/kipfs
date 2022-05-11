
#ifndef __DEFS_H__
#define __DEFS_H__

#include <stdio.h>
#include <stdlib.h>

typedef void (*DataCallbackFunc)(void* data,int len,const char* err);

extern void testCallback(void *data,int len,const char* err);

extern void bridgeDataCallback(DataCallbackFunc callback,void *data,int len,const char* err);
#endif 