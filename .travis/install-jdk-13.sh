#!/bin/bash
set -e

JDK_FEATURE=13

#TMP=$(curl -L jdk.java.net/${JDK_FEATURE})
#TMP="${TMP#*Latest build: }" # remove everything before the number
#TMP="${TMP%%\(*}"                                        # remove everything after the number
#JDK_BUILD="$(echo -e "${TMP}" | tr -d '[:space:]')" # remove all whitespace

cd ~
#https://download.java.net/java/GA/jdk13/5b8a42f3905b406298b72d750b6919f6/33/GPL/openjdk-13_linux-x64_bin.tar.gz
#JDK_ARCHIVE=openjdk-${JDK_FEATURE}-ea+${JDK_BUILD}_linux-x64_bin.tar.gz

JDK_ARCHIVE=openjdk-13_linux-x64_bin.tar.gz
#wget --no-check-certificate https://download.java.net/java/early_access/jdk${JDK_FEATURE}/${JDK_BUILD}/GPL/${JDK_ARCHIVE}
wget --no-check-certificate https://download.java.net/java/GA/jdk13/5b8a42f3905b406298b72d750b6919f6/33/GPL/openjdk-13_linux-x64_bin.tar.gz
tar -xzf ${JDK_ARCHIVE}
export JAVA_HOME=~/jdk-${JDK_FEATURE}
export PATH=${JAVA_HOME}/bin:$PATH
cd -
echo check java version
java -version

wget https://www-eu.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.zip
unzip -qq apache-maven-3.5.4-bin.zip
export M2_HOME=$PWD/apache-maven-3.5.4
export PATH=$M2_HOME/bin:$PATH
