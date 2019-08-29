#!/bin/bash
set -e

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

java9 && git reset --hard && mvn clean install javadoc:javadoc -Pdev9 && \
java8 && git reset --hard && mvn clean install javadoc:javadoc -Pdev && \
java7 && git reset --hard && mvn clean install javadoc:javadoc -Dhttps.protocols=TLSv1.2 &&
#java6 && git reset --hard && mvn clean install -Dhttps.protocols=TLSv1.2 
git reset --hard

