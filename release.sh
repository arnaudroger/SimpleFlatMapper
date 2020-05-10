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

  rm -f release.properties
  unset MAVEN_OPTS

  GPG_TTY=$(tty)
  export GPG_TTY
  git reset --hard

  if [ $javaversion == "8" ]
  then
    java8
    git checkout master
    git reset --hard
    git pull
    mvn --batch-mode -Dtag=sfm-$REL -Pdev release:prepare \
                 -DreleaseVersion=$REL \
                 -DdevelopmentVersion=$DEV
    mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID -Drelease"
  elif [ $javaversion == "9" ]
  then
    java9
    git checkout master
    git reset --hard
    git pull
    git checkout -b sfm-$REL-jre9-branch
    rm -f XsltTransform.class
    $JAVA_HOME/bin/javac XsltTransform.java
    find ./ -name 'pom.xml' | xargs java XsltTransform jre9
    git commit -a -m "update artifactid"
    git push --set-upstream origin sfm-$REL-jre9-branch
    export MAVEN_OPTS="--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED "
    mvn --batch-mode -Dtag=sfm-parent-$REL -Pdev release:prepare \
                 -DreleaseVersion=$REL \
                 -Dtag=sfm-$REL-jre9 \
                 -DdevelopmentVersion=$DEV
    mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID -Drelease"
    git reset --hard
    git checkout master
    git push --delete origin sfm-$REL-jre9-branch
    git branch -D sfm-$REL-jre9-branch
    echo build $REL for jre9 done
    unset MAVEN_OPTS
  elif [ $javaversion == "7" ]
  then
    java7
    git checkout master
    git reset --hard
    git pull
    git checkout -b sfm-$REL-jre6-branch
    rm -f XsltTransform.class
    $JAVA_HOME/bin/javac XsltTransform.java
    find ./ -name 'pom.xml' | xargs java XsltTransform jre6
    git commit -a -m "update artifactid"
    git push --set-upstream origin sfm-$REL-jre6-branch
    mvn --batch-mode -Dtag=sfm-parent-$REL release:prepare \
                 -DreleaseVersion=$REL \
                 -Dtag=sfm-$REL-jre6 \
                 -DdevelopmentVersion=$DEV
    mvn release:perform -Darguments="-DstagingRepositoryId=$REPOID -Drelease -DskipTests -Dhttps.protocols=TLSv1.2" -Dhttps.protocols=TLSv1.2
    git reset --hard
    git checkout master
    git push --delete origin sfm-$REL-jre6-branch
    git branch -D sfm-$REL-jre6-branch
    echo build $REL for jre6 done
  else
    echo ERROR: Invalid java version $javaversion
    exit 1
  fi


}

#echo "change versions"
#exit
REL=8.2.3
DEV=8.2.4-SNAPSHOT
REPOID=orgsimpleflatmapper-1685

release 7 $REL $DEV $REPOID
release 8 $REL $DEV $REPOID
release 9 $REL $DEV $REPOID



