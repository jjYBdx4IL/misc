# misc

Misc Java stuff

## REQUIREMENTS

Some sub-projects require launch4j, which in turn needs 32bit libraries on Linux. On Debian-like
systems (like Ubuntu), that boils down to the`gcc-multilib` package. Also, if there is an error
involving not finding `jni.h`, it's likely that JAVA_HOME is not set properly.

## LICENSE

All modules are licensed under the Apache License v2.0 by default. However, there are some
exceptions to some modules. Those modules have their own LICENSE file(s).

## RELEASE

Open an ISSUE here on github if you want me to push updates to maven central.

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

## Build Site Archive

In the root directory (`misc-aggregator` project) do:

```
mvn clean install site
mvn clean install -Psite-archive -N
```


--
devel/java/github/misc@7887
