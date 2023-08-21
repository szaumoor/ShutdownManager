## Description
This command line application allows you to time a shutdown of your Windows or Unix system with three methods:
1. Shutdown by timer: After a countdown, it will initiate the shutdown in 60 seconds.
2. Shutdown by process state: Regularly monitor an active process. When the process dies, the shutdown will be triggered in 60 seconds.
3. Shutdown by scheduling it: Select one particular hour today or future days, and the shutdown will be triggered in 60 seconds after that.

## Requirements
A Unix or Windows system with at least Java 8 installed.

## How to use
Download the ShutdownManager.zip of the last version in the Releases section of this repository. Unzip it anywhere.
Execute the bat file if using Windows or the sh file if in Unix (only tested on Linux). You can also execute the .jar through the terminal with the following command: 
```
java -jar ShutdownManager.jar
```
Also please bear in mind that the script must be in the same folder as the .jar file for it to work. 
