# jutils

A Java command line client for miscellaneous tools.

Version: ${my.release.version}



## Homepage / Download

Home for J-Utils is at:

https://webtools.leponceau.org

There you can find binaries and source code. 



## Functionality

* github repositories listing and backup (uses command line git client by default, embedded jgit is available, too)
* recursive grep allowing to filter both file names (ant-style glob) and line patterns (regex)
* jenkins: re-queue all projects
* local maven repository search (class names and assets contained in jar files)
* transforms output of maven enforcer plugin's dependency conflict into a set of dependencies with unique versions
  to put into your dependencyManagement section. No more tedious version checking just to have determinate
  versions.
* Text(html)-2-image conversion with CSS support through CSS-BOX.



## Features

* Environment for easy addition of more functions, ie. command line parsing and help display.
* Functionality modules are detected by annotation scanning during compilation.



## Repository

https://github.com/jjYBdx4IL/misc
