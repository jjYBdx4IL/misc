#!/bin/bash

set -Eex
set -o pipefail

kaitaiVersion=$1
shift

scriptdir="$(readlink -f "$(dirname "$0")")"
cd $scriptdir
basedir="$(readlink -f "$scriptdir/../../..")"
cd $basedir/target
rm -rf kaitai_struct_formats-master
wget -c https://dl.bintray.com/kaitai-io/universal/$kaitaiVersion/kaitai-struct-compiler-$kaitaiVersion.zip && unzip -o kaitai-struct-compiler-$kaitaiVersion.zip
wget -c https://github.com/kaitai-io/kaitai_struct_formats/archive/master.zip && unzip master.zip
compiler=$basedir/target/kaitai-struct-compiler-$kaitaiVersion/bin/kaitai-struct-compiler
cd $basedir/target/kaitai_struct_formats-master
find . -name '*.ksy' -exec install -D '{}' $basedir/src/main/kaitai/'{}' \;
builddir=$basedir/target/kaitaiBuild
rm -rf $builddir
install -d $builddir
cd $basedir/src/main/kaitai
find . -name '*.ksy' | while read ksy; do
    set -Eex
    set -o pipefail
    if ! grep "^  *license:  *CC0-1.0 *$" $ksy; then continue; fi
    reldir="$(dirname "$ksy")"
    pkg=${reldir#./}
    pkg=${pkg%/}
    pkg=${pkg//\//.}
    pkg=com.github.jjYBdx4IL.parser.kaitai.$pkg
    $compiler --target java --outdir $builddir --java-package $pkg $ksy || :
done

rsync -a $builddir/src/ $basedir/src/main/java/
