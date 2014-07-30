#!/bin/sh
java -Xms1g -cp target/classes:target/test-classes/:\
$HOME/.m2/repository/org/ow2/asm/asm/5.0.3/asm-5.0.3.jar:\
$HOME/.m2/repository/org/hsqldb/hsqldb/2.3.2/hsqldb-2.3.2.jar:\
$HOME/.m2/repository/junit/junit/4.11/junit-4.11.jar \
org.sfm.benchmark.StaticJdbcMapperBenchmark
