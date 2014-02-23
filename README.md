# AirPlay Desktop Mirroring Utility

AirPlay is a project that allows a Linux user to mirror a desktop to an Apple
AirPlay device. This works by taking screenshots on a periodic interval and
sending them to the AirPlay device, and thus is not suitable for any setting
that requires a high framerate.

## Usage

```
java -jar <jar>
java -jar <jar> -l [-i <path> | -d | -s]
java -jar <jar> <device name> [-i <path> | -d | -s]
java -jar <jar> -h <host> [-i <path> | -d | -s]

Options:
    -l, --list            Lists available airplay serviers.
    -h, --host <host>     The host of the airplay server.
    -i, --image <path>    Show a given image.
    -d, --desktop         Shows the desktop (mirroring).
    -s, --stop            Stops showing content.

Omitting all arguments will scan for AirPlay devices. Select a device from the
provided menu to begin desktop mirroring.

When showing an image or the desktop, use CTRL-C to stop.
```

## Build Instructions

Install [gradle](http://www.gradle.org/) and execute `gradle build uberjar` to
create the uberjar.
