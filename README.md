# Zander Command-Line Client
## Downloading
The latest version of zander can be downloaded from http://files.morleydev.co.uk/zander-client/latest.zip

Unzipping latest.zip will result in a zander-client-0.0.1-prealpha folder. Adding the folder *zander-client-0.0.1-prealpha/bin* to your PATH will allow for running the client through the command line.

## Compiling
Compiling requires the latest version of sbt. The source code can be git cloned from https://github.com/MorleyDev/zander.client, or downloaded from https://github.com/MorleyDev/zander.client/archive/master.zip

Compilation can be executed via the command *sbt compile*
Tests can be ran with sbt test if wished via *sbt test*. Next, the program must be packaged with sbt, the command *sbt universal:package-bin* can be used to produce a zip file. This zip file can be unzipped, and will create a folder zander-client-prealpha-0.0.1 with two sub-folders, bin and lib. The bin folder contains the script files that are used to run zander.

Alternatively, on debian an installer .deb file can be generator with *sbt debian:package-bin*

All of these can be ran in sequence via the command:
*sbt compile test universal:package-bin*

## Configuration
The configuration for zander is a json file named “config.json”, which is by default found in a folder in the the user home directory named “.zander”, this folder and configuration will be created if not present. The folder in which the system will attempt to look for config.json can be changed by setting the environmental variable *ZANDER_CLIENT*.

The json file defines the following values:

| Key | Description | Default Value |
| ------------- |-------------| -----|
| server | The server to request against to retrieve the project information, must point to a running instance of the zander server rest api. | http://zander.morleydev.co.uk |
|programs | The set of the programs the zander client application runs | { “git” : see programs.git, “cmake” : see programs.cmake } |
| programs.git | The git program to be invoked in order to perform git operations | git |
| programs.cmake | The cmake program to be invoked in order to perform cmake operations (e.g build, install) | cmake |
| cache | The location of the cache to store source code and compiled artefacts. | $(ZANDER_HOME)/cache |

## Command-Line
The command line argument takes the format:
*zander_client* [operation] \$(project) [compiler] [build mode]
Where operation, compiler and build mode are one of the supported values, and the project is the name of a project that can be retrieved via a request to the host at /project/$(project)

### install
The install operation will only succeed if the desired project, for the desired build mode and compiler, if that particular combination has not already been installed to the current working directory (or has since been deleted/purged).

It installs the artefacts from the cache, downloading and building them as needed, to the local directory.

### update
The update operation will only succeed if the desired project, for the desired build mode and compiler, if that particular combination has already been installed to the current working directory (and has not since been deleted/purged).

It checks the current locally installed version, attempts to update the code and artefacts in the cache, and if the installed artefacts are not of the same version as the cached artefacts after the attempted update then it removed the current artefacts and installs the new ones.

### purge
The purge operation will only succeed if the desired project, for the desired build mode and compiler, if that particular combination has already been installed to the current working directory (and has not since been deleted/purged).

It deletes all the files for the specified project, unless another installed file also references that file, and the installed project dependency is no longer considered as installed.

### get
The get operation performs an install or update, depending on whether local aretefacts are currently installed (and have not been since deleted/purged). If the artefacts are installed, this operation is equivalent to calling update.

### Supported Compilers
* msvc10: Visual Studio 2010 MSVC10 C++ Compiler
* msvc11: Visual Studio 2012 MSVC11 C++ Compiler
* msvc12: Visual Studio 2013 MSVC12 C++ Compiler
* msvc10w64: Visual Studio 2010 MSVC10 C++ Compiler 64-bit
* msvc11w64: Visual Studio 2012 MSVC11 C++ Compiler 64-bit
* msvc12w64: Visual Studio 2013 MSVC12 C++ Compiler 64-bit
* mingw: MinGW g++ compiler
* unix: Cygwin g++ Compiler
* msys: Msys g++ Compiler
* borland: Borland C++ Compiler

### Supported Build Modes
* debug: Build the project artefacts with debug information and without optimisations
* release: Build the project artefacts with no debug information and with full release optimisations

##Build Status

Travis-CI Build Status
[![Build Status](https://travis-ci.org/MorleyDev/zander.client.svg?branch=master)](https://travis-ci.org/MorleyDev/zander.client)
