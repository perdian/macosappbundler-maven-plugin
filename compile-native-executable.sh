#!/bin/bash

realpath() {
    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
}

EXECUTABLE_FILE=target/classes/${EXECUTABLE_FILE_NAME:-JavaLauncher}
INPUT_FILES=$(find src/main/native -name "*.m")
INCLUDE_JAVA=$(/usr/libexec/java_home)/include
INCLUDE_JAVA_DARWIN=$(/usr/libexec/java_home)/include/darwin
SYSROOT=/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk

mkdir -p $(dirname $EXECUTABLE_FILE)

echo "Compiling x86_64 executable into: $(realpath $EXECUTABLE_FILE).x86_64"
clang \
  -I ${INCLUDE_JAVA} \
  -I ${INCLUDE_JAVA_DARWIN} \
  -isysroot ${SYSROOT} \
  -framework Cocoa \
  -mmacosx-version-min=10.12 \
  -target x86_64-apple-darwin-macho \
  -o "${EXECUTABLE_FILE}.x86_64" \
  ${INPUT_FILES}

echo "Compiling arm64 executable into: $(realpath $EXECUTABLE_FILE).arm64"
clang \
  -I ${INCLUDE_JAVA} \
  -I ${INCLUDE_JAVA_DARWIN} \
  -isysroot ${SYSROOT} \
  -framework Cocoa \
  -mmacosx-version-min=10.12 \
  -target arm64-apple-darwin-macho \
  -o "${EXECUTABLE_FILE}.arm64" \
  ${INPUT_FILES}

echo "Generating universal executable into: $(realpath $EXECUTABLE_FILE)"
lipo -create -output ${EXECUTABLE_FILE} "${EXECUTABLE_FILE}.x86_64" "${EXECUTABLE_FILE}.arm64"
