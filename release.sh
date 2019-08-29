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

function release {
  javaversion=$1
  REL=$2
  DEV=$3
  REPOID=$4

  rm release.properties
  unset MAVEN_OPTS

  GPG_TTY=$(tty)
  export GPG_TTY
  git reset --hard

  if [ $javaversion == "8" ]
  then
    java8
    mvn --batch-mode -Dtag=sfm-$REL -Pdev release:prepare \
                 -DreleaseVersion=$REL \
                 -DdevelopmentVersion=$DEV
    mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID"
  elif [ $javaversion == "9" ]
  then
    java9
    export MAVEN_OPTS="--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED "
    mvn --batch-mode -Dtag=sfm-parent-$REL -Pdev release:prepare \
                 -DreleaseVersion=$REL-jre9 \
                 -Dtag=sfm-$REL-jre9 \
                 -DdevelopmentVersion=$DEV
    mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID"
    unset MAVEN_OPTS
  elif [ $javaversion == "7" ]
  then
    java7
    mvn --batch-mode -Dtag=sfm-parent-$REL release:prepare \
                 -DreleaseVersion=$REL-jre6 \
                 -Dtag=sfm-$REL-jre6 \
                 -DdevelopmentVersion=$DEV
    mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID -DskipTests -Dhttps.protocols=TLSv1.2" -Dhttps.protocols=TLSv1.2
  else
    echo ERROR: Invalid java version $javaversion
    exit 1
  fi


}

#echo "change versions"
#exit
REL=8.0.0
DEV=8.0.1-SNAPSHOT
REPOID=orgsimpleflatmapper-1671

release 7 $REL $DEV $REPOID
release 8 $REL $DEV $REPOID
release 9 $REL $DEV $REPOID



