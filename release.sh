mvn release:prepare
cp release.properties tmp/release.properties
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/
mvn release:perform
cp tmp/release.properties .
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre/
mvn release:perform
cp tmp/release.properties .
export JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64/jre/
mvn release:perform
git reset --hard && git pull --rebase

