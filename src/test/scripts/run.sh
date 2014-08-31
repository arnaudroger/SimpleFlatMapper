#!/bin/bash



DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

BENCHMARK=$DIR/../../../src/test/benchmarks
SCRIPTS=$DIR/../../../src/test/scripts

cpustable
$SCRIPTS/allBenchmarks.sh mock 1,10,100,1000 1000000 > $BENCHMARK/data/mock.csv
$SCRIPTS/allBenchmarks.sh hsqldb 1,10,100,1000 500000 > $BENCHMARK/data/hsqldb.csv
$SCRIPTS/allBenchmarks.sh mysql 1,10,100,1000 100000 > $BENCHMARK/data/mysql.csv
cpufull



