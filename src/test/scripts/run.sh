#!/bin/bash



DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

BENCHMARK=$DIR/../../../src/test/benchmarks
SCRIPTS=$DIR/../../../src/test/scripts

DATE=`date +%Y%m%d_%H%M%S`

$SCRIPTS/cpuPreBenchmark.sh
$SCRIPTS/allBenchmarks.sh mock 1,10,100,1000 1000000 > $BENCHMARK/data/mock/mock_$DATE.csv
$SCRIPTS/allBenchmarks.sh hsqldb 1,10,100,1000 500000 > $BENCHMARK/data/hsqldb/hsqldb_$DATE.csv
$SCRIPTS/allBenchmarks.sh mysql 1,10,100,1000 100000 > $BENCHMARK/data/mysql/mysql_$DATE.csv
$SCRIPTS/cpuPostBenchmark.sh

git add $BENCHMARK/data/mock/mock_$DATE.csv $BENCHMARK/data/hsqldb/hsqldb_$DATE.csv $BENCHMARK/data/mysql/mysql_$DATE.csv
git commit -m "$DATE benchmark"



