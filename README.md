# misc

Misc Java stuff

[![Build Status](https://travis-ci.org/jjYBdx4IL/misc.png?branch=master)](https://travis-ci.org/jjYBdx4IL/misc)

## RELEASE

This repository consists of a lot of smaller, separate sub-projects, each of which gets released
independently and therefore has its own 1.0, 1.1, 1.2, etc. version sequence numbering.

The maven release plugin has issues with releasing subfolders of git repositories. Workaround:

```
# release plugin's pushChanges option is set to false!
cd <project's sub-folder>
mvn release:prepare
git push --all
git push --tags
# now fix scm.url in release.properties manually! then run:
mvn release:perform

When the last step hangs (happends often after the first 4 files have been uploaded), interrupt it and
restart with "mvn clean && mvn release:perform".
