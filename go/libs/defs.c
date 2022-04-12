#include "defs.h"
#include <stdio.h>

void testCallback(void *data,int len,const char* err){
    printf("The data is %s with length:%d\n",(char*)data,len);
    if (err != 0)
        printf("An error occurred: %s\n",err);
}


 void bridgeDataCallback(DataCallbackFunc callback,void *data,int len,const char* err){
	callback(data,len,err);
}