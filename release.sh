#!/bin/bash
set -e
function java19 {
	sudo update-alternatives --set java /usr/lib/jvm/zulu19/bin/java;export JAVA_HOME=/usr/lib/jvm/zulu19
}


function release {
  REL=$1
  DEV=$2
  REPOID=$3

  rm -f release.properties
  unset MAVEN_OPTS

  GPG_TTY=$(tty)
  export GPG_TTY
  git reset --hard

  java19
  git checkout master
  git reset --hard
  git pull
  export MAVEN_OPTS="--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED "
  mvn --batch-mode -Dtag=sfm-$REL -Pdev9 -Pjdk19 release:prepare \
               -DreleaseVersion=$REL \
               -DdevelopmentVersion=$DEV
  mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID -Drelease"

}

#echo "change versions"
#exit
REL=9.0.2
DEV=9.0.3-SNAPSHOT
REPOID=orgsimpleflatmapper-1690

release $REL $DEV $REPOID



