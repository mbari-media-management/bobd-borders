# bobd-borders
Application to fetch images from VARS database and remove black borders.

## Usage
To run this application, run the following from the command line (JRE 8 / JDK 8 must be installed):
```bash
cd target
java -jar bobd-borders-1.0.0.jar
```

### Options
Add the -v or --verbose option to enable higher verbosity:
```bash
java -jar bobd-borders-1.0.0.jar -v
```

## Build

To build the application, run the following from the root directory of the project:
_Note: This project uses the latest version of Apache Maven. Download from the [Maven website](https://maven.apache.org/)._
```bash
./build.sh
```
