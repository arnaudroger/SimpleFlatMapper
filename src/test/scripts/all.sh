#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TARGET=$DIR/../../../target
#-XX:+UnlockCommercialFeatures -XX:+FlightRecorder 
#JAVA_OPTS="-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly"

java -Xms2g -Xmx2g $JAVA_OPTS -cp $TARGET/classes:$TARGET/test-classes/:\
$HOME/.m2/repository/org/ow2/asm/asm/5.0.3/asm-5.0.3.jar:\
$HOME/.m2/repository/org/hsqldb/hsqldb/2.3.2/hsqldb-2.3.2.jar:\
$HOME/.m2/repository/junit/junit/4.11/junit-4.11.jar:\
$HOME/.m2/repository/org/hibernate/hibernate-core/4.3.6.Final/hibernate-core-4.3.6.Final.jar:\
$HOME/.m2/repository/org/hibernate/hibernate-ehcache/4.3.6.Final/hibernate-ehcache-4.3.6.Final.jar:\
$HOME/.m2/repository/net/sf/ehcache/ehcache-core/2.4.3/ehcache-core-2.4.3.jar:\
$HOME/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:\
$HOME/.m2/repository/org/hibernate/common/hibernate-commons-annotations/4.0.5.Final/hibernate-commons-annotations-4.0.5.Final.jar:\
$HOME/.m2/repository/org/jboss/logging/jboss-logging/3.1.3.GA/jboss-logging-3.1.3.GA.jar:\
$HOME/.m2/repository/org/jboss/spec/javax/transaction/jboss-transaction-api_1.2_spec/1.0.0.Final/jboss-transaction-api_1.2_spec-1.0.0.Final.jar:\
$HOME/.m2/repository/org/hibernate/javax/persistence/hibernate-jpa-2.1-api/1.0.0.Final/hibernate-jpa-2.1-api-1.0.0.Final.jar:\
$HOME/.m2/repository/org/javassist/javassist/3.18.1-GA/javassist-3.18.1-GA.jar:\
$HOME/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:\
$HOME/.m2/repository/org/hdrhistogram/HdrHistogram/1.2.1/HdrHistogram-1.2.1.jar:\
$HOME/.m2/repository/org/mybatis/mybatis/3.2.7/mybatis-3.2.7.jar \
-Dasm.dump.target.dir=$TARGET/asm-classes/ \
org.sfm.benchmark.AllBenchmark
