#!/bin/bash

EXECUTABLE_FILE=target/classes/${EXECUTABLE_FILE_NAME:-JavaLauncher}
INPUT_FILES=$(find src/main/native -name "*.m")
INCLUDE_JAVA=$(/usr/libexec/java_home)/include
INCLUDE_JAVA_DARWIN=$(/usr/libexec/java_home)/include/darwin
SYSROOT=/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk

mkdir -p $(dirname $EXECUTABLE_FILE)

echo "Compiling executable into: $(realpath $EXECUTABLE_FILE)"
clang \
  -I ${INCLUDE_JAVA} \
  -I ${INCLUDE_JAVA_DARWIN} \
  -isysroot ${SYSROOT} \
  -framework Cocoa \
  -o ${EXECUTABLE_FILE} \
  ${INPUT_FILES}
