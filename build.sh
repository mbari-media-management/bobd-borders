#!/bin/bash
mvn clean compile assembly:single
mv target/bobd-borders-1.0.0.jar bobd-borders.jar
rm target/bobd-borders-1.0.0-jar-with-dependencies.jar