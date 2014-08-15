#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TARGET=$DIR/../../../target

java -Xms1g -cp $TARGET/classes:$TARGET/test-classes/:\
$HOME/.m2/repository/org/hsqldb/hsqldb/2.3.2/hsqldb-2.3.2.jar:\
$HOME/.m2/repository/org/hdrhistogram/HdrHistogram/1.2.1/HdrHistogram-1.2.1.jar:\
$HOME/.m2/repository/junit/junit/4.11/junit-4.11.jar \
org.sfm.benchmark.PureJdbcBenchmark
