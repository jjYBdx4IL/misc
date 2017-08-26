# misc

Misc Java stuff

[![Build Status](https://travis-ci.org/jjYBdx4IL/misc.png?branch=master)](https://travis-ci.org/jjYBdx4IL/misc)

## RELEASE

The maven release plugin has issues with releasing subfolders of git repositories. Workaround:

```
# release plugin's pushChanges option is set to false!
mvn release:prepare
git push --all
# now fix scm.url in release.properties manually! then run:
mvn release:perform
```
