#!/bin/bash
set -e

JDK_FEATURE=10
JDK_BUILD=32
JDK_ARCHIVE=jdk-${JDK_FEATURE}-ea+${JDK_BUILD}_linux-x64_bin.tar.gz
# http://download.java.net/java/jdk10/archive/32/binaries/jdk-10-ea+32_linux-x64_bin.tar.gz
cd ~
wget http://download.java.net/java/jdk${JDK_FEATURE}/archive/${JDK_BUILD}/binaries/${JDK_ARCHIVE}
tar -xzf ${JDK_ARCHIVE}
export JAVA_HOME=~/jdk-${JDK_FEATURE}
export PATH=${JAVA_HOME}/bin:$PATH
cd -
echo check java version
java --version

wget https://archive.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.zip
unzip -qq apache-maven-3.5.2-bin.zip
export M2_HOME=$PWD/apache-maven-3.5.2
export PATH=$M2_HOME/bin:$PATH
