# Goggle-Epoxy

Basically, it's a near-eye display similar to Microsoft HoloLens or Google Glass powered by a Raspberry Pi. Unlike Google and Miccrosoft, I am poor and have few means of production, so it's basically crappier.

Because I'm cool (and because I want to learn Gradle) I actually separated the project into 6 subprojects.

## Instructions to Run

To run an emulation, do:
```bash
./gradlew :run-emulator:run
``` 

To run on an embedded system, like Raspberry Pi, do:
```bash
./gradlew :run-embedded:run
``` 

To package a JAR for Raspberry Pi, do:
```bash
./gradlew :run-embedded:jar
``` 

## Attributions

- ["Pixelated" font](http://fontstruct.com/fontstructions/show/426637) by “Greenma201” is
licensed under a [Creative Commons Attribution Share Alike license](http://creativecommons.org/licenses/by-sa/3.0/).
- Star data is included from [The Astronomy Nexus](http://www.astronexus.com/hyg).
