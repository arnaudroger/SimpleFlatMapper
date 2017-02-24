#!/bin/bash

function java9 {
	sudo update-alternatives --set java /usr/lib/jvm/java-9-oracle/bin/java;export JAVA_HOME=/usr/lib/jvm/java-9-oracle
}
function java8 {
	sudo update-alternatives --set java /usr/lib/jvm/java-8-oracle/jre/bin/java;export JAVA_HOME=/usr/lib/jvm/java-8-oracle
}
function java7 {
	sudo update-alternatives --set java /usr/lib/jvm/java-7-oracle/jre/bin/java;export JAVA_HOME=/usr/lib/jvm/java-7-oracle
}
function java6 {
	sudo update-alternatives --set java /usr/lib/jvm/java-6-oracle/jre/bin/java;export JAVA_HOME=/usr/lib/jvm/java-6-oracle
}

#echo "change versions"
#exit
java8
REL=3.11.1
DEV=3.12-SNAPSHOT
mvn --batch-mode -Dtag=sfm-parent-$REL -Pdev release:prepare \
                 -DreleaseVersion=$REL \
                 -DdevelopmentVersion=$DEV
#mvn release:prepare -Pdev
cp release.properties tmp/release.properties


java8
mvn release:perform


java7
cp tmp/release.properties .
mvn release:perform

java9
git reset --hard
cp tmp/release.properties .
export MAVEN_OPTS="--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED "
mvn release:perform
unset MAVEN_OPTS


git reset --hard && git pull --rebase

