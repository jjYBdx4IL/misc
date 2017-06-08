#!/bin/bash

set -Ee
set -o pipefail

INSTDIR=~/.cache/jutils-bin
SRCJAR=~/.m2/repository/com/github/jjYBdx4IL/misc/jutils/1.0-SNAPSHOT/jutils-1.0-SNAPSHOT-bin.zip
SUBDIR=jutils
EXECJARF=jutils-1.0-SNAPSHOT.jar

CHGTODIR="$INSTDIR/$SUBDIR"
EXECJAR="$INSTDIR/$SUBDIR/$EXECJARF"

if test -e "$EXECJAR"; then
    if [[ "$SRCJAR" -nt "$EXECJAR" ]]; then
        rm -rf "$INSTDIR"
    fi
fi

if ! test -e "$EXECJAR"; then
    install -d "$INSTDIR"
    pushd "$INSTDIR" >/dev/null
    unzip -q "$SRCJAR"
    touch "$EXECJAR"
    popd >/dev/null
fi

cd $CHGTODIR
java -jar $EXECJARF "$@"

