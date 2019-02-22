#!/bin/bash

set -Eex

if [[ "$PUBLIC_CI" == "true" ]]; then
    export PATH=$JAVA_HOME/bin:$PATH
fi

( while true; do echo . ; sleep 300; done ) &

mvn "$@" -q -V clean install

