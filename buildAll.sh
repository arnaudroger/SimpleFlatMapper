#!/bin/bash
java8 mvn release:prepare
cp release.properties tmp/release.properties
java8
mvn release:perform
cp tmp/release.properties .
java7
mvn release:perform
cp tmp/release.properties .
java6
mvn release:perform
git reset --hard && git pull --rebase

