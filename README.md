# Goggle-Epoxy

It's a near-eye display similar to Microsoft HoloLens or Google Glass, and it's powered by a Raspberry Pi. The project is separated into 6 subprojects. There will be a new subproject for every app and runtime environment. The CAD models will be put here when I actually make it. 

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
