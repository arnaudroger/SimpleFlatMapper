#!/bin/bash
set -e
function java11 {
	sudo update-alternatives --set java /usr/lib/jvm/java-9-oracle/bin/java;export JAVA_HOME=/usr/lib/jvm/java-9-oracle
}
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

function release {
  javaversion=$1
  REL=$2
  DEV=$3
  REPOID=$4

  rm -f release.properties
  unset MAVEN_OPTS

  GPG_TTY=$(tty)
  export GPG_TTY
  git reset --hard

  if [ $javaversion == "8" ]
  then
    exit 1
  elif [ $javaversion == "9" ]
  then
    java11
    git checkout master
    git reset --hard
    git pull
    export MAVEN_OPTS="--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED "
    mvn --batch-mode -Dtag=sfm-$REL -Pdev release:prepare \
                 -DreleaseVersion=$REL \
                 -DdevelopmentVersion=$DEV
    mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID -Drelease"
  elif [ $javaversion == "7" ]
  then
    exit 1
  else
    echo ERROR: Invalid java version $javaversion
    exit 1
  fi


}

#echo "change versions"
#exit
REL=9.0.0.A
DEV=9.0.0-SNAPSHOT
REPOID=orgsimpleflatmapper-1685

#release 7 $REL $DEV $REPOID
#release 8 $REL $DEV $REPOID
release 9 $REL $DEV $REPOID



