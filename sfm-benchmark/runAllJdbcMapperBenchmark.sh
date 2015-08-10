#!/bin/sh
java -jar sfm-benchmark-roma/target/benchmarks.jar -f 1 -rf csv -rff jmh-roma.csv
java -jar sfm-benchmark-jdbc/target/benchmarks.jar -f 1 -rf csv -rff jmh-jdbc.csv

