#!/bin/bash

set -Eex

if [[ "$PUBLIC_CI" == "true" ]]; then
    export PATH=$JAVA_HOME/bin:$PATH
fi

which java
java -version
which javac
javac -version
mvn "$@" -V -B install -f release-parent
mvn "$@" -B clean install

