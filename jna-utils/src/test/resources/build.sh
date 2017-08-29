#!/bin/bash

set -Eex

baseDir=$1
targetDir=$2
classesOutputDir=$3
osName=$4
osArch=$5
shift 5

# check for gnu compiler
gcc --version

install -d "$targetDir/generated-test-sources/c"

sysPrefix="$(uname -s)-$(uname -m)"
sysPrefix="$(echo "$sysPrefix" | tr '[:upper:]' '[:lower:]')"
sysPrefix=${sysPrefix//_/-}

libDir="$classesOutputDir/$sysPrefix"
install -d $libDir

# JNI
javah -classpath "$classesOutputDir" \
    -d "$targetDir/generated-test-sources/c" \
    com.github.jjYBdx4IL.utils.jna.ResourceUtilsJNITestHelper
gcc -shared -fPIC -O2 -g \
    -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" \
    -I"$targetDir/generated-test-sources/c" \
    -o "$libDir/libresourceutilsjnitesthelper.so" \
    "$baseDir/src/test/c/ResourceUtilsJNITestHelper.c"

