# KIPFS: Kotlin MultiPlatform library for IPFS

## What's that?

The intention is to create a [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) library
for accessing and embedding an [IPFS](https://ipfs.io/) node.

The initial platforms targetted are android and jvm (via jni) and kotlin native targets linux (x86_64, armv7, aarch64)
and windows 64bit (via mingw).

I haven't access to a mac but support for that might be something you could help with.


## Status

Lots of the low-level proof of concept work is complete.
A basic command-line demo can be run with:

`./gradlew :demos:native_cmdline:runKipfsDemoDebugExecutableLinuxAmd64`

or to cross compile the demo for other targets:


`./gradlew :demos:native_cmdline:linkKipfsDemoDebugExecutableWindowsAmd64`

(or LinuxArm, LinuxArm64, ..)

The [demos](./demos/) use precompiled binaries from my maven repository at https://h1.danbrough.org/maven

They are compiled on a docker image. See: [./bin/docker.sh](./bin/docker.sh) and the Docker files in [./docker/](./docker/).






