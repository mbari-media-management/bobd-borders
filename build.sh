#!/bin/bash
mvn clean compile assembly:single
cp target/bobd-borders-1.0.0.jar bobd-borders.jar