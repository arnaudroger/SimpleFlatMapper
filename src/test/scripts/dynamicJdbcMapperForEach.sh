#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TARGET=$DIR/../../../target

#JAVA_OPTS="-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly"
#JAVA_OPTS="-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true,dumponexitpath=staticjdbcmapper.jfr"
java -Xms1g $JAVA_OPTS -cp $TARGET/classes:$TARGET/test-classes/:\
$HOME/.m2/repository/org/ow2/asm/asm/5.0.3/asm-5.0.3.jar:\
$HOME/.m2/repository/org/hsqldb/hsqldb/2.3.2/hsqldb-2.3.2.jar:\
$HOME/.m2/repository/org/hdrhistogram/HdrHistogram/1.2.1/HdrHistogram-1.2.1.jar:\
$HOME/.m2/repository/junit/junit/4.11/junit-4.11.jar \
-Dasm.dump.target.dir=$TARGET/asm-classes/ \
org.sfm.benchmark.sfm.DynamicJdbcMapperForEachBenchmark $*
