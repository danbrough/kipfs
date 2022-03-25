//
// Created by dan on 29/01/22.
//

#include "libkipfs.h"
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char** argv) {
  //obtain reference for calling Kotlin/Native functions


     char * msg = KGetMessage();
    printf("Message is %s\n",msg);
    free((void*)msg);
   msg = KGetMessage2();
    printf("Message2 is %s\n",msg);
    free((void*)msg);
	msg = KCID("\"Hello World\"");
	printf("DAG: %s\n",msg);
	free((void*)msg);

  return 0;
}
