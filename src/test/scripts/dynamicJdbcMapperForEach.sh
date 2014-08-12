#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TARGET=$DIR/../../../target

java -Xms1g  -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -cp $TARGET/classes:$TARGET/test-classes/:\
$HOME/.m2/repository/org/ow2/asm/asm/5.0.3/asm-5.0.3.jar:\
$HOME/.m2/repository/org/hsqldb/hsqldb/2.3.2/hsqldb-2.3.2.jar:\
$HOME/.m2/repository/org/hdrhistogram/HdrHistogram/1.2.1/HdrHistogram-1.2.1.jar:\
$HOME/.m2/repository/junit/junit/4.11/junit-4.11.jar \
org.sfm.benchmark.sfm.DynamicJdbcMapperForEachBenchmark
