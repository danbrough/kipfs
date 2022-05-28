#include "defs.h"

void testCallback(void *data,int len,const char* err){
    printf("The data is %s with length:%d\n",(char*)data,len);
    if (err != 0)
        printf("An error occurred: %s\n",err);
}

 void bridgeDataCallback(DataCallbackFunc callback,void *data,int len,const char* err){
	callback(data,len,err);
}

void pass_string(char* str) {
    printf("pass_string received: %s\n",str);
}

char* return_string() {
  return "C string";
}

void StructTest(MyStruct s){
    printf("MyStruct: %d %f\n",s.a,s.b);
}

int copy_string(char* str, int size) {


  *str++ = 'H';
  *str++ = 'e';
  *str++ = 'l';
  *str++ = 'l';
  *str++ = 'o';
  *str++ = ' ';
  *str++ = 'W';
  *str++ = 'o';
  *str++ = 'r';
  *str++ = 'l';
  *str++ = 'd';
  *str++ = '!';
  *str++ = 0;
  return 0;
}