#!/bin/sh

# Create the wrapper directory if it doesn't exist
mkdir -p gradle/wrapper

# Download the gradle-wrapper.jar
curl -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.0.0/gradle/wrapper/gradle-wrapper.jar 