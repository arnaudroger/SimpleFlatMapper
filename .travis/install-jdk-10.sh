#!/bin/bash
set -e

JDK_FEATURE=10

#TMP=$(curl -L jdk.java.net/${JDK_FEATURE})
#TMP="${TMP#*Most recent build: jdk-${JDK_FEATURE}+}" # remove everything before the number
#TMP="${TMP%%<*}"                                        # remove everything after the number
#JDK_BUILD="$(echo -e "${TMP}" | tr -d '[:space:]')" # remove all whitespace

JDK_ARCHIVE=openjdk-10_linux-x64_bin.tar.gz
#jdk-${JDK_FEATURE}+${JDK_BUILD}_linux-x64_bin.tar.gz

cd ~
wget --no-check-certificate https://download.java.net/java/GA/jdk10/10/binaries/openjdk-10_linux-x64_bin.tar.gz
#https://download.java.net/java/jdk${JDK_FEATURE}/archive/${JDK_BUILD}/BCL/${JDK_ARCHIVE}
tar -xzf ${JDK_ARCHIVE}
export JAVA_HOME=~/jdk-${JDK_FEATURE}
export PATH=${JAVA_HOME}/bin:$PATH
cd -
echo check java version
java --version

wget https://www-eu.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.zip
unzip -qq apache-maven-3.5.4-bin.zip
export M2_HOME=$PWD/apache-maven-3.5.4
export PATH=$M2_HOME/bin:$PATH
