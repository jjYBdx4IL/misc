# jutils

A Java command line client for miscellaneous tools.

## Functionality

* github repositories listing and backup (uses command line git client by default, embedded jgit is available, too)
* recursive grep allowing to filter both file names and line patterns (both regex)
* jenkins: re-queue all projects
* local maven repository search (class names and assets contained in jar files)

## Features

* Environment for easy addition of more functions, ie. command line parsing and help display.
* Functionality modules are detected by annotation scanning during compilation.
* Includes a script that automatically installs newly built jars that it finds in your local maven repository.
