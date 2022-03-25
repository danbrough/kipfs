//
// Created by dan on 29/01/22.
//

#include "libgolib_api.h"
#include "stdio.h"

int main(int argc, char** argv) {
  //obtain reference for calling Kotlin/Native functions
  libgolib_ExportedSymbols* lib = libgolib_symbols();

  lib->kotlin.root.danbroid.mpp.forIntegers(1, 2, 3, 4);
  lib->kotlin.root.danbroid.mpp.forFloats(1.0f, 2.0);

  //use C and Kotlin/Native strings
  const char* str = "Hello from Native!";
  const char* response = lib->kotlin.root.danbroid.mpp.strings(str);
  printf("in: %s\nout:%s\n", str, response);
  lib->DisposeString(response);


  const char* globalStr = lib->kotlin.root.danbroid.mpp.get_globalString();
  printf("Global Str is %s\n",globalStr);
  lib->DisposeString(globalStr);

  //create Kotlin object instancelibgolib_kref_danbroid_mpp_Clazz
  libgolib_kref_danbroid_mpp_Clazz newInstance = lib->kotlin.root.danbroid.mpp.Clazz.Clazz();
  long x = lib->kotlin.root.danbroid.mpp.Clazz.memberFunction(newInstance, 42);
  lib->DisposeStablePointer(newInstance.pinned);

  printf("DemoClazz returned %ld\n", x);

  return 0;
}